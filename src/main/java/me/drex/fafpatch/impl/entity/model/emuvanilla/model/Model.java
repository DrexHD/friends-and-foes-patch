package me.drex.fafpatch.impl.entity.model.emuvanilla.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.drex.fafpatch.impl.entity.model.emuvanilla.CubeConsumer;
import org.joml.Matrix4fStack;

import java.util.List;

public abstract class Model {
    protected final ModelPart root;
    private final List<ModelPart> allParts;

    public Model(ModelPart modelPart) {
        this.root = modelPart;
        this.allParts = modelPart.getAllParts();
    }

    public final void renderServerSide(Matrix4fStack matrices, CubeConsumer vertices) {
        this.root().renderServerSide(matrices, vertices);
    }

    public final void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        this.root().render(poseStack, vertexConsumer, i, j, k);
    }

    public final void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j) {
        this.renderToBuffer(poseStack, vertexConsumer, i, j, -1);
    }

    public final ModelPart root() {
        return this.root;
    }

    public final List<ModelPart> allParts() {
        return this.allParts;
    }

    public final void resetPose() {
        for (ModelPart modelPart : this.allParts) {
            modelPart.resetPose();
        }
    }

    public static class Simple extends Model {
        public Simple(ModelPart modelPart) {
            super(modelPart);
        }
    }
}
