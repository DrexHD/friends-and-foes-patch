package me.drex.fafpatch.impl.entity.model.emuvanilla;

import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelElement;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelTransformation;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.EntityModel;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.LayerDefinition;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.IdentityHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record PolyModelInstance<T extends EntityModel<?>>(T model, LayerDefinition data, ResourceLocation texture,
                                                          Function<ModelPart, ItemStack> modelParts, Function<ModelPart, ItemStack> damagedModelParts) {

    public static <T extends EntityModel<?>> PolyModelInstance<T> create(Function<ModelPart, T> modelCreator, LayerDefinition data, ResourceLocation texture) {
        var model = modelCreator.apply(data.bakeRoot());

        return of(model, data, texture);
    }

    public PolyModelInstance<T> withTexture(ResourceLocation texture) {
        return of(this.model, this.data, texture);
    }

    private static <T extends EntityModel<?>> PolyModelInstance<T> of(T model, LayerDefinition data, ResourceLocation texture) {
        var map = new IdentityHashMap<ModelPart, ItemStack>();
        var damagedMap = new IdentityHashMap<ModelPart, ItemStack>();
        int id = 0;
        for (var part : model.allParts()) {
            if (part.isEmpty()) continue;
            var stack = ItemDisplayElementUtil.getModel(texture.withSuffix("/part_" + (id++)));
            map.put(part, stack);
            stack = stack.copy();
            stack.set(DataComponents.MAP_COLOR, new MapItemColor(0xff7e7e));
            damagedMap.put(part, stack);
        }
        return new PolyModelInstance<>(model, data, texture, map::get, damagedMap::get);
    }

    public void generateAssets(BiConsumer<String, byte[]> writer, AtlasAsset.Builder atlas) {
        atlas.single(texture);

        int id = 0;

        for (var part : model.allParts()) {
            if (part.isEmpty()) continue;
            var modelId = texture.withSuffix("/part_" + (id++));
            var model = ModelAsset.builder();
            model.texture("txt", texture.toString());
            model.texture("empty", "friendsandfoes-polymer-patch:block/empty");
            model.texture("particle", "#txt");

            part.getCubes().forEach(cuboid -> {
                for (var quad : cuboid.polygons) {
                    var min = new Vector3f(Float.POSITIVE_INFINITY);
                    var max = new Vector3f(Float.NEGATIVE_INFINITY);
                    ModelPart.Vertex v1 = quad.vertices()[0];
                    ModelPart.Vertex v2 = quad.vertices()[0];

                    for (var vert : quad.vertices()) {
                        min.min(vert.pos());
                        max.max(vert.pos());
                    }

                    for (var vert : quad.vertices()) {
                        if (min.equals(vert.pos())) {
                            v1 = vert;
                        }
                        if (max.equals(vert.pos())) {
                            v2 = vert;
                        }
                    }


                    var b = ModelElement.builder(new Vec3(min.x, min.y, min.z).scale(0.25).add(8), new Vec3(max.x, max.y, max.z).scale(0.25).add(8));
                    for (var dir : Direction.values()) {
                        b.face(dir, "#empty");
                    }

                    var dir = Direction.getNearest((int) quad.normal().x, (int) quad.normal().y, (int) quad.normal().z, null);

                    if ((dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE) == (dir.getAxis() == Direction.Axis.Z)) {
                       dir = dir.getOpposite();
                    }

                    b.face(dir, v1.u() * 16, v2.v() * 16, v2.u() * 16, v1.v() * 16, "#txt", dir, 0, 0);
                    b.face(dir.getOpposite(), v2.u() * 16, v2.v() * 16, v1.u() * 16, v1.v() * 16, "#txt", dir.getOpposite(), 0, 0);


                    model.element(b.build());
                }
            });

            model.transformation(ItemDisplayContext.FIXED, new ModelTransformation(new Vec3(0, 180, 0), Vec3.ZERO, new Vec3(4, 4, 4)));

            writer.accept(AssetPaths.model(modelId) + ".json", model.build().toBytes());
        }
    }

}
