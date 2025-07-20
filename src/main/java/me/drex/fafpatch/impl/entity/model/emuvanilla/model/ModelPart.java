package me.drex.fafpatch.impl.entity.model.emuvanilla.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.drex.fafpatch.impl.entity.model.emuvanilla.CubeConsumer;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.joml.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ModelPart {
    public static final float DEFAULT_SCALE = 1.0F;
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float xScale = DEFAULT_SCALE;
    public float yScale = DEFAULT_SCALE;
    public float zScale = DEFAULT_SCALE;
    public boolean visible = true;
    public boolean skipDraw;
    private final List<Cube> cubes;
    private final Map<String, ModelPart> children;
    private PartPose initialPose;

    public ModelPart(List<Cube> list, Map<String, ModelPart> map) {
        this.initialPose = PartPose.ZERO;
        this.cubes = list;
        this.children = map;
    }

    public PartPose storePose() {
        return PartPose.offsetAndRotation(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot);
    }

    public PartPose getInitialPose() {
        return this.initialPose;
    }

    public void setInitialPose(PartPose partPose) {
        this.initialPose = partPose;
    }

    public void resetPose() {
        this.loadPose(this.initialPose);
    }

    public void loadPose(PartPose partPose) {
        this.x = partPose.x();
        this.y = partPose.y();
        this.z = partPose.z();
        this.xRot = partPose.xRot();
        this.yRot = partPose.yRot();
        this.zRot = partPose.zRot();
        this.xScale = partPose.xScale();
        this.yScale = partPose.yScale();
        this.zScale = partPose.zScale();
    }

    public void copyFrom(ModelPart modelPart) {
        this.xScale = modelPart.xScale;
        this.yScale = modelPart.yScale;
        this.zScale = modelPart.zScale;
        this.xRot = modelPart.xRot;
        this.yRot = modelPart.yRot;
        this.zRot = modelPart.zRot;
        this.x = modelPart.x;
        this.y = modelPart.y;
        this.z = modelPart.z;
    }

    public boolean hasChild(String string) {
        return this.children.containsKey(string);
    }

    public ModelPart getChild(String string) {
        ModelPart modelPart = this.children.get(string);
        if (modelPart == null) {
            throw new NoSuchElementException("Can't find part " + string);
        } else {
            return modelPart;
        }
    }

    public void setPos(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public void setRotation(float f, float g, float h) {
        this.xRot = f;
        this.yRot = g;
        this.zRot = h;
    }

    public void renderServerSide(Matrix4fStack matrices, CubeConsumer vertices) {
        if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
            matrices.pushMatrix();
            this.applyTransform(matrices);
            vertices.consume(this, matrices, !this.visible);

            for(ModelPart modelPart : this.children.values()) {
                modelPart.renderServerSide(matrices, vertices);
            }

            matrices.popMatrix();
        }
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j) {
        this.render(poseStack, vertexConsumer, i, j, -1);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                poseStack.pushPose();
                this.translateAndRotate(poseStack);
                if (!this.skipDraw) {
                    this.compile(poseStack.last(), vertexConsumer, i, j, k);
                }

                for(ModelPart modelPart : this.children.values()) {
                    modelPart.render(poseStack, vertexConsumer, i, j, k);
                }

                poseStack.popPose();
            }
        }
    }

    public void rotateBy(Quaternionf quaternionf) {
        Matrix3f matrix3f = (new Matrix3f()).rotationZYX(this.zRot, this.yRot, this.xRot);
        Matrix3f matrix3f2 = matrix3f.rotate(quaternionf);
        Vector3f vector3f = matrix3f2.getEulerAnglesZYX(new Vector3f());
        this.setRotation(vector3f.x, vector3f.y, vector3f.z);
    }

    public void getExtentsForGui(PoseStack poseStack, Set<Vector3f> set) {
        this.visit(poseStack, (pose, string, i, cube) -> {
            for(Polygon polygon : cube.polygons) {
                for(Vertex vertex : polygon.vertices()) {
                    float f = vertex.pos().x() / 16.0F;
                    float g = vertex.pos().y() / 16.0F;
                    float h = vertex.pos().z() / 16.0F;
                    Vector3f vector3f = pose.pose().transformPosition(f, g, h, new Vector3f());
                    set.add(vector3f);
                }
            }

        });
    }

    public void visit(PoseStack poseStack, Visitor visitor) {
        this.visit(poseStack, visitor, "");
    }

    private void visit(PoseStack poseStack, Visitor visitor, String string) {
        if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
            poseStack.pushPose();
            this.translateAndRotate(poseStack);
            PoseStack.Pose pose = poseStack.last();

            for(int i = 0; i < this.cubes.size(); ++i) {
                visitor.visit(pose, string, i, this.cubes.get(i));
            }

            this.children.forEach((string2, modelPart) -> modelPart.visit(poseStack, visitor, string2 + string2));
            poseStack.popPose();
        }
    }

    public void applyTransform(Matrix4fStack matrices) {
        matrices.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
        if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F) {
            matrices.rotate((new Quaternionf()).rotationZYX(this.zRot, this.yRot, this.xRot));
        }

        if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
            matrices.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    public void translateAndRotate(PoseStack poseStack) {
        poseStack.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
        if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F) {
            poseStack.mulPose((new Quaternionf()).rotationZYX(this.zRot, this.yRot, this.xRot));
        }

        if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
            poseStack.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    private void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int i, int j, int k) {
        for(Cube cube : this.cubes) {
            cube.compile(pose, vertexConsumer, i, j, k);
        }
    }

    public List<Cube> getCubes() {
        return this.cubes;
    }

    public Cube getRandomCube(RandomSource randomSource) {
        return this.cubes.get(randomSource.nextInt(this.cubes.size()));
    }

    public boolean isEmpty() {
        return this.cubes.isEmpty();
    }

    public void offsetPos(Vector3f vector3f) {
        this.x += vector3f.x();
        this.y += vector3f.y();
        this.z += vector3f.z();
    }

    public void offsetRotation(Vector3f vector3f) {
        this.xRot += vector3f.x();
        this.yRot += vector3f.y();
        this.zRot += vector3f.z();
    }

    public void offsetScale(Vector3f vector3f) {
        this.xScale += vector3f.x();
        this.yScale += vector3f.y();
        this.zScale += vector3f.z();
    }

    public List<ModelPart> getAllParts() {
        List<ModelPart> list = new ArrayList<>();
        list.add(this);
        this.addAllChildren((string, modelPart) -> list.add(modelPart));
        return List.copyOf(list);
    }

    public Function<String, ModelPart> createPartLookup() {
        Map<String, ModelPart> map = new HashMap<>();
        map.put("root", this);
        Objects.requireNonNull(map);
        this.addAllChildren(map::putIfAbsent);
        Objects.requireNonNull(map);
        return map::get;
    }

    private void addAllChildren(BiConsumer<String, ModelPart> biConsumer) {
        for(Map.Entry<String, ModelPart> entry : this.children.entrySet()) {
            biConsumer.accept(entry.getKey(), entry.getValue());
        }

        for(ModelPart modelPart : this.children.values()) {
            modelPart.addAllChildren(biConsumer);
        }

    }

    public static class Cube {
        public final Polygon[] polygons;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float maxX;
        public final float maxY;
        public final float maxZ;

        public Cube(int i, int j, float f, float g, float h, float k, float l, float m, float n, float o, float p, boolean bl, float q, float r, Set<Direction> set) {
            this.minX = f;
            this.minY = g;
            this.minZ = h;
            this.maxX = f + k;
            this.maxY = g + l;
            this.maxZ = h + m;
            this.polygons = new Polygon[set.size()];
            float s = f + k;
            float t = g + l;
            float u = h + m;
            f -= n;
            g -= o;
            h -= p;
            s += n;
            t += o;
            u += p;
            if (bl) {
                float v = s;
                s = f;
                f = v;
            }

            Vertex vertex = new Vertex(f, g, h, 0.0F, 0.0F);
            Vertex vertex2 = new Vertex(s, g, h, 0.0F, 8.0F);
            Vertex vertex3 = new Vertex(s, t, h, 8.0F, 8.0F);
            Vertex vertex4 = new Vertex(f, t, h, 8.0F, 0.0F);
            Vertex vertex5 = new Vertex(f, g, u, 0.0F, 0.0F);
            Vertex vertex6 = new Vertex(s, g, u, 0.0F, 8.0F);
            Vertex vertex7 = new Vertex(s, t, u, 8.0F, 8.0F);
            Vertex vertex8 = new Vertex(f, t, u, 8.0F, 0.0F);
            float w = (float)i;
            float x = (float)i + m;
            float y = (float)i + m + k;
            float z = (float)i + m + k + k;
            float aa = (float)i + m + k + m;
            float ab = (float)i + m + k + m + k;
            float ac = (float)j;
            float ad = (float)j + m;
            float ae = (float)j + m + l;
            int af = 0;
            if (set.contains(Direction.DOWN)) {
                this.polygons[af++] = new Polygon(new Vertex[]{vertex6, vertex5, vertex, vertex2}, x, ac, y, ad, q, r, bl, Direction.DOWN);
            }

            if (set.contains(Direction.UP)) {
                this.polygons[af++] = new Polygon(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, y, ad, z, ac, q, r, bl, Direction.UP);
            }

            if (set.contains(Direction.WEST)) {
                this.polygons[af++] = new Polygon(new Vertex[]{vertex, vertex5, vertex8, vertex4}, w, ad, x, ae, q, r, bl, Direction.WEST);
            }

            if (set.contains(Direction.NORTH)) {
                this.polygons[af++] = new Polygon(new Vertex[]{vertex2, vertex, vertex4, vertex3}, x, ad, y, ae, q, r, bl, Direction.NORTH);
            }

            if (set.contains(Direction.EAST)) {
                this.polygons[af++] = new Polygon(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, y, ad, aa, ae, q, r, bl, Direction.EAST);
            }

            if (set.contains(Direction.SOUTH)) {
                this.polygons[af] = new Polygon(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, aa, ad, ab, ae, q, r, bl, Direction.SOUTH);
            }

        }

        public void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int i, int j, int k) {
            Matrix4f matrix4f = pose.pose();
            Vector3f vector3f = new Vector3f();

            for(Polygon polygon : this.polygons) {
                Vector3f vector3f2 = pose.transformNormal(polygon.normal, vector3f);
                float f = vector3f2.x();
                float g = vector3f2.y();
                float h = vector3f2.z();

                for(Vertex vertex : polygon.vertices) {
                    float l = vertex.pos.x() / 16.0F;
                    float m = vertex.pos.y() / 16.0F;
                    float n = vertex.pos.z() / 16.0F;
                    Vector3f vector3f3 = matrix4f.transformPosition(l, m, n, vector3f);
                    vertexConsumer.addVertex(vector3f3.x(), vector3f3.y(), vector3f3.z(), k, vertex.u, vertex.v, j, i, f, g, h);
                }
            }

        }
    }

    public record Polygon(Vertex[] vertices, Vector3f normal) {

        public Polygon(Vertex[] vertexs, float f, float g, float h, float i, float j, float k, boolean bl, Direction direction) {
            this(vertexs, direction.step());
            float l = 0.0F / j;
            float m = 0.0F / k;
            vertexs[0] = vertexs[0].remap(h / j - l, g / k + m);
            vertexs[1] = vertexs[1].remap(f / j + l, g / k + m);
            vertexs[2] = vertexs[2].remap(f / j + l, i / k - m);
            vertexs[3] = vertexs[3].remap(h / j - l, i / k - m);
            if (bl) {
                int n = vertexs.length;

                for(int o = 0; o < n / 2; ++o) {
                    Vertex vertex = vertexs[o];
                    vertexs[o] = vertexs[n - 1 - o];
                    vertexs[n - 1 - o] = vertex;
                }
            }

            if (bl) {
                this.normal.mul(-1.0F, 1.0F, 1.0F);
            }

        }
    }

    public record Vertex(Vector3f pos, float u, float v) {

        public Vertex(float f, float g, float h, float i, float j) {
            this(new Vector3f(f, g, h), i, j);
        }

        public Vertex remap(float f, float g) {
            return new Vertex(this.pos, f, g);
        }
    }

    @FunctionalInterface
    public interface Visitor {
        void visit(PoseStack.Pose pose, String string, int i, Cube cube);
    }
}
