package me.drex.fafpatch.impl.res;


import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateModelVariant;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.drex.fafpatch.impl.FriendsAndFoesPatch;
import me.drex.fafpatch.impl.entity.model.EntityModels;
import me.drex.fafpatch.impl.model.generic.BlockStateModelManager;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static me.drex.fafpatch.impl.FriendsAndFoesPatch.id;

public class ResourcePackGenerator {
    private static final Set<String> EXPANDABLE = Set.of("button", "egg", "lightning_rod");
    private static final Vec3 EXPANSION = new Vec3(0.06, 0.06, 0.06);
    public static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = Util.make(new Int2ObjectOpenHashMap<>(), int2ObjectOpenHashMap -> {
        int2ObjectOpenHashMap.put(1, ResourceLocation.withDefaultNamespace("stone"));
        int2ObjectOpenHashMap.put(2, ResourceLocation.withDefaultNamespace("iron"));
        int2ObjectOpenHashMap.put(3, ResourceLocation.withDefaultNamespace("gold"));
        int2ObjectOpenHashMap.put(4, ResourceLocation.withDefaultNamespace("emerald"));
        int2ObjectOpenHashMap.put(5, ResourceLocation.withDefaultNamespace("diamond"));
    });

    public static void setup() {
        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(ResourcePackGenerator::build);
    }

    private static void build(ResourcePackBuilder builder) {
        var atlas = AtlasAsset.builder();

        copyVanillaAssets(builder,
            "assets/minecraft/textures/entity/villager/villager.png",
            "assets/friendsandfoes/textures/entity/villager/villager.png"
        );
        for (ResourceLocation resourceLocation : BuiltInRegistries.VILLAGER_TYPE.keySet()) {
            copyVanillaAssets(builder,
                "assets/minecraft/textures/entity/villager/type/" + resourceLocation.getPath() + ".png",
                "assets/friendsandfoes/textures/entity/villager/type/" + resourceLocation.getPath() + ".png"
            );
        }

        LEVEL_LOCATIONS.forEach((level, resourceLocation) -> {
            copyVanillaAssets(builder,
                "assets/minecraft/textures/entity/villager/profession_level/" + resourceLocation.getPath() + ".png",
                "assets/friendsandfoes/textures/entity/villager/profession_level/" + resourceLocation.getPath() + ".png"
            );
        });

        for (var model : EntityModels.ALL) {
            model.generateAssets(builder::addData, atlas);
        }

        builder.forEachFile(((string, bytes) -> {
            for (var expandable : EXPANDABLE) {
                if (string.contains(expandable) && string.startsWith("assets/friendsandfoes/models/block/")) {
                    var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));

                        builder.addData(AssetPaths.model(FriendsAndFoesPatch.MOD_ID, parentId.getPath()) + ".json", new ModelAsset(parentAsset.parent(), parentAsset.elements().map(x -> x.stream()
                            .map(element -> new ModelElement(element.from().subtract(EXPANSION), element.to().add(EXPANSION),
                                element.faces(), element.rotation(), element.shade(), element.lightEmission())
                            ).toList()), parentAsset.textures(), parentAsset.display(), parentAsset.guiLight(), parentAsset.ambientOcclusion()).toBytes());


                        if (asset.elements().isPresent()) {
                            builder.addData(string, new ModelAsset(asset.parent(), asset.elements().map(x -> x.stream()
                                .map(element -> new ModelElement(element.from().subtract(EXPANSION), element.to().add(EXPANSION),
                                    element.faces(), element.rotation(), element.shade(), element.lightEmission())
                                ).toList()), asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes());
                        }
                    }
                }
            }
        }));

        for (var entry : BlockStateModelManager.UV_LOCKED_MODELS.entrySet()) {
            var expand = EXPANDABLE.stream().anyMatch(expandable -> entry.getKey().getPath().contains(expandable) && entry.getKey().getPath().startsWith("block/")) ? EXPANSION : Vec3.ZERO;
            for (var v : entry.getValue()) {
                var suffix = "_uvlock_" + v.x() + "_" + v.y();
                var modelId = v.model().withSuffix(suffix);
                var asset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getData(AssetPaths.model(v.model()) + ".json")), StandardCharsets.UTF_8));

                if (asset.parent().isPresent()) {
                    var parentId = asset.parent().get();
                    var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));
                    builder.addData(AssetPaths.model(FriendsAndFoesPatch.MOD_ID, parentId.getPath() + suffix) + ".json",
                        new ModelAsset(parentAsset.parent(), parentAsset.elements().map(x -> x.stream()
                            .map(element -> expandModelElement(element, expand, v)).toList()), parentAsset.textures(), parentAsset.display(), parentAsset.guiLight(), parentAsset.ambientOcclusion()).toBytes());

                    builder.addData(AssetPaths.model(modelId) + ".json",
                        new ModelAsset(Optional.of(ResourceLocation.fromNamespaceAndPath(FriendsAndFoesPatch.MOD_ID, parentId.getPath() + suffix)),
                            asset.elements().map(x -> x.stream()
                                .map(element -> expandModelElement(element, expand, v)).toList()),
                            asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes());
                }
            }
        }

        builder.addWriteConverter(((string, bytes) -> {
            if (!string.contains("_uvlock_")) {
                for (var expandable : EXPANDABLE) {
                    if (string.contains(expandable) && string.startsWith("assets/friendsandfoes/models/block/")) {
                        var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                        return new ModelAsset(asset.parent().map(x -> id(x.getPath())), asset.elements(), asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes();
                    }
                }
            }
            return bytes;
        }));

        builder.addData("assets/minecraft/atlases/blocks.json", atlas.build());
    }

    private static ModelElement expandModelElement(ModelElement element, Vec3 expand, StateModelVariant v) {
        return new ModelElement(element.from().subtract(expand), element.to().add(expand),
            element.faces().entrySet().stream().map(face -> {
                var uv = face.getValue().uv();

                if (uv.isEmpty()) {
                    Vector2f uv1, uv2;
                    int rot;
                    if (face.getKey().getAxis() == Direction.Axis.Y) {
                        uv1 = new Vector2f((float) element.from().x(), (float) element.from().z());
                        uv2 = new Vector2f((float) element.to().x(), (float) element.to().z());
                        rot = v.y();
                    } else {
                        uv1 = new Vector2f((float) element.from().get(face.getKey().getClockWise().getAxis()), 16 - (float) element.to().y());
                        uv2 = new Vector2f((float) element.to().get(face.getKey().getClockWise().getAxis()), 16 - (float) element.from().y());
                        rot = v.x();
                    }

                    if (rot >= 180) {
                        uv1.set(16 - uv1.x, 16 - uv1.y);
                        uv2.set(16 - uv2.x, 16 - uv2.y);
                    }

                    uv = List.of(Math.clamp(Math.min(uv1.x, uv2.x), 0, 16),
                        Math.clamp(Math.min(uv1.y, uv2.y), 0, 16),
                        Math.clamp(Math.max(uv1.x, uv2.x), 0, 16),
                        Math.clamp(Math.max(uv1.y, uv2.y), 0, 16));

                    if (rot == 90 || rot == 270 || v.y() == -90) {
                        uv = List.of(uv.get(1), uv.get(0), uv.get(3), uv.get(2));
                    }

                    return Map.entry(face.getKey(), new ModelElement.Face(uv, face.getValue().texture(), face.getValue().cullface(),
                        (360 + face.getValue().rotation() - rot * face.getKey().getAxisDirection().getStep()) % 360,
                        face.getValue().tintIndex()));
                }

                int xBonus = v.x() == 90 || v.x() == 270 ? 180 : 0;
                int yBonus = 0;//v.y() != 90 && v.y() != 270 ? 180 : 0;

                if (face.getKey().getAxis() == Direction.Axis.Y && v.y() != 0) {
                    if (v.y() == 90 || v.y() == 270 || v.y() == -90) {
                        uv = List.of(uv.get(1), uv.get(0), uv.get(3), uv.get(2));
                    }

                    return Map.entry(face.getKey(), new ModelElement.Face(uv, face.getValue().texture(), face.getValue().cullface(),
                        (360 + face.getValue().rotation() - v.y() * face.getKey().getAxisDirection().getStep() + xBonus) % 360,
                        face.getValue().tintIndex()));
                }
                if (face.getKey().getAxis() != Direction.Axis.Y && v.x() != 0) {
                    if (v.x() == 90 || v.x() == 270 || v.x() == -90) {
                        uv = List.of(uv.get(1), uv.get(0), uv.get(3), uv.get(2));
                    }

                    return Map.entry(face.getKey(), new ModelElement.Face(uv, face.getValue().texture(), face.getValue().cullface(),
                        (360 + face.getValue().rotation() - v.x() * face.getKey().getAxisDirection().getStep() + yBonus) % 360,
                        face.getValue().tintIndex()));
                }

                return face;
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), element.rotation(), element.shade(), element.lightEmission());
    }

    private static void copyVanillaAssets(ResourcePackBuilder builder, String vanillaPath, String outputPath) {
        try {
            builder.addData(outputPath, Files.readAllBytes(PolymerCommonUtils.getClientJarRoot().resolve(vanillaPath)));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}