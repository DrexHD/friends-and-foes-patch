package me.drex.fafpatch.impl.res;


import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.resourcepack.ModelModifiers;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.drex.fafpatch.impl.FriendsAndFoesPatch;
import me.drex.fafpatch.impl.entity.model.EntityModels;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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

        for (var entry : BlockStateModelManager.UV_LOCKED_MODELS.get("friendsandfoes").entrySet()) {
            var expand = EXPANDABLE.stream().anyMatch(expandable -> entry.getKey().contains(expandable) && entry.getKey().startsWith("block/")) ? EXPANSION : Vec3.ZERO;
            for (var v : entry.getValue()) {
                var suffix = "_uvlock_" + v.x() + "_" + v.y();
                var modelId = v.model().withSuffix(suffix);
                var asset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getData(AssetPaths.model(v.model()) + ".json")), StandardCharsets.UTF_8));

                if (asset.parent().isPresent()) {
                    var parentId = asset.parent().get();
                    var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));
                    builder.addData(AssetPaths.model(FriendsAndFoesPatch.MOD_ID, parentId.getPath() + suffix) + ".json",
                        ModelModifiers.expandModelAndRotateUVLocked(parentAsset, expand, v.x(), v.y()));
                    builder.addData(AssetPaths.model(modelId) + ".json",
                        new ModelAsset(Optional.of(ResourceLocation.fromNamespaceAndPath(FriendsAndFoesPatch.MOD_ID, parentId.getPath() + suffix)), asset.elements(),
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

    private static void copyVanillaAssets(ResourcePackBuilder builder, String vanillaPath, String outputPath) {
        try {
            builder.addData(outputPath, Files.readAllBytes(PolymerCommonUtils.getClientJarRoot().resolve(vanillaPath)));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}