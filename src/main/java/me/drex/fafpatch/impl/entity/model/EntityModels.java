package me.drex.fafpatch.impl.entity.model;

import com.faboslav.friendsandfoes.common.FriendsAndFoes;
import com.faboslav.friendsandfoes.common.entity.IllusionerEntity;
import com.faboslav.friendsandfoes.common.entity.MaulerEntity;
import com.faboslav.friendsandfoes.common.entity.MoobloomEntity;
import com.faboslav.friendsandfoes.common.entity.TuffGolemEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.drex.fafpatch.impl.entity.GlareElementHolder;
import me.drex.fafpatch.impl.entity.model.emuvanilla.PolyModelInstance;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.EntityModel;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.LayerDefinition;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.MeshTransformer;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.ModelPart;
import me.drex.fafpatch.impl.res.ResourcePackGenerator;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.*;
import java.util.function.Function;

public interface EntityModels {
    List<PolyModelInstance<?>> ALL = new ArrayList<>();
    EnumMap<WeatheringCopper.WeatherState, PolyModelInstance<CopperGolemEntityModel>> COPPER_GOLEM = Util.make(new EnumMap<>(WeatheringCopper.WeatherState.class), m -> {
        for (var state : WeatheringCopper.WeatherState.values()) {
            String prefix = "";
            if (state != WeatheringCopper.WeatherState.UNAFFECTED) {
                prefix = state.getSerializedName() + "_";
            }
            m.put(state, create(CopperGolemEntityModel::new, CopperGolemEntityModel.getTexturedModelData(), FriendsAndFoes.makeID("entity/copper_golem/" + prefix + "copper_golem")));
        }
    });
    PolyModelInstance<CowModel<MoobloomEntity>> MOOBLOOM = create(CowModel::new, CowModel.createBodyLayer(), FriendsAndFoes.makeID("entity/moobloom/buttercup_moobloom"));
    PolyModelInstance<CowModel<MoobloomEntity>> MOOBLOOM_BABY = create(CowModel::new, CowModel.createBodyLayer().apply(CowModel.BABY_TRANSFORMER), FriendsAndFoes.makeID("entity/moobloom/buttercup_moobloom"));

    PolyModelInstance<CrabEntityModel> CRAB = create(CrabEntityModel::new, CrabEntityModel.getTexturedModelData(), FriendsAndFoes.makeID("entity/crab/crab"));
    PolyModelInstance<CrabEntityModel> CRAB_BABY = create(CrabEntityModel::new, CrabEntityModel.getTexturedModelData().apply(CrabEntityModel.BABY_TRANSFORMER), FriendsAndFoes.makeID("entity/crab/crab"));

//    PolyModelInstance<GlareEntityModel> GLARE = create(GlareEntityModel::new, GlareEntityModel.getTexturedModelData().apply(GlareEntityModel.ADULT_TRANSFORMER), FriendsAndFoes.makeID("entity/glare/glare"));
//    PolyModelInstance<GlareEntityModel> GLARE_BABY = create(GlareEntityModel::new, GlareEntityModel.getTexturedModelData().apply(GlareEntityModel.BABY_TRANSFORMER), FriendsAndFoes.makeID("entity/glare/glare"));

//    PolyModelInstance<GlareEntityModel> FLOWERING_GLARE = create(GlareEntityModel::new, GlareEntityModel.getTexturedModelData(), FriendsAndFoes.makeID("entity/glare/flowering_glare"));

    Map<GlareElementHolder.RenderState, PolyModelInstance<GlareEntityModel>> GLARE = new HashMap<>() {{
        boolean[] bools = {false, true};
        for (boolean isBaby : bools) {
            for (boolean isFlowering : bools) {
                String texture = isFlowering ? "flowering_glare" : "glare";
                LayerDefinition modelData = GlareEntityModel.getTexturedModelData();
                if (isBaby) {
                    modelData = modelData.apply(GlareEntityModel.BABY_TRANSFORMER);
                }

                put(new GlareElementHolder.RenderState(isFlowering, isBaby), create(GlareEntityModel::new, modelData, FriendsAndFoes.makeID("entity/glare/" + texture)));
            }
        }
    }};

    PolyModelInstance<IceologerIceChunkModel> ICE_CHUNK = create(IceologerIceChunkModel::new, IceologerIceChunkModel.getTexturedModelData(), FriendsAndFoes.makeID("entity/illager/ice_chunk"));
    PolyModelInstance<RascalEntityModel> RASCAL = create(RascalEntityModel::new, RascalEntityModel.getTexturedModelData(), FriendsAndFoes.makeID("entity/rascal/rascal"));

    PolyModelInstance<TuffGolemEntityModel> TUFF_GOLEM = create(TuffGolemEntityModel::new, TuffGolemEntityModel.getTexturedModelData(), FriendsAndFoes.makeID("entity/tuff_golem/tuff_golem"));
    PolyModelInstance<TuffGolemEntityModel> TUFF_GOLEM_CLOSED_EYES = create(TuffGolemEntityModel::new, TuffGolemEntityModel.getTexturedModelData(), FriendsAndFoes.makeID("entity/tuff_golem/closed_eyes"));
    EnumMap<TuffGolemEntity.Color, PolyModelInstance<TuffGolemEntityModel>> TUFF_GOLEM_CLOTH = Util.make(new EnumMap<>(TuffGolemEntity.Color.class), m -> {
        for (var color : TuffGolemEntity.Color.values()) {
            var instance = create(TuffGolemEntityModel::new, TuffGolemEntityModel.getTexturedModelData(), FriendsAndFoes.makeID("entity/tuff_golem/" + color.getName()));
            m.put(color, instance);
        }
    });

    MeshTransformer humanLikeScaling = MeshTransformer.scaling(0.9375F);
    LayerDefinition villagerData = LayerDefinition.create(VillagerModel.createBodyModel(), 64, 64).apply(humanLikeScaling);
    PolyModelInstance<VillagerModel> VILLAGER = create(modelPart -> new VillagerModel(modelPart, true), villagerData, FriendsAndFoes.makeID("entity/villager/villager"));
    Map<ResourceLocation, PolyModelInstance<VillagerModel>> VILLAGER_PROFESSION = Util.make(new HashMap<>(), m -> {
        var instance = create(modelPart -> new VillagerModel(modelPart, true), villagerData, FriendsAndFoes.makeID("entity/villager/profession/beekeeper"));
        m.put(FriendsAndFoes.makeID("beekeeper"), instance);
    });

    Int2ObjectMap<PolyModelInstance<VillagerModel>> VILLAGER_PROFESSION_LEVEL = Util.make(new Int2ObjectOpenHashMap<>(), m -> {
        ResourcePackGenerator.LEVEL_LOCATIONS.forEach((level, resourceLocation) -> {
            var instance = create(modelPart -> new VillagerModel(modelPart, false), villagerData, FriendsAndFoes.makeID("entity/villager/profession_level/" + resourceLocation.getPath()));
            m.put(level, instance);
        });
    });
    Map<ResourceLocation, PolyModelInstance<VillagerModel>> VILLAGER_TYPE = Util.make(new HashMap<>(), m -> {
        for (ResourceLocation resourceLocation : BuiltInRegistries.VILLAGER_TYPE.keySet()) {
            var instance = create(modelPart -> new VillagerModel(modelPart, false), villagerData, FriendsAndFoes.makeID("entity/villager/type/" + resourceLocation.getPath()));
            m.put(resourceLocation, instance);
        }
    });


    PolyModelInstance<WildfireEntityModel> WILDFIRE = create(WildfireEntityModel::new, WildfireEntityModel.getTexturedModelData(), FriendsAndFoes.makeID("entity/wildfire/wildfire"));
    PolyModelInstance<IllagerModel<IllusionerEntity>> ILLUSIONER = create(IllagerModel::new, IllagerModel.createBodyLayer(), FriendsAndFoes.makeID("entity/illusioner/illusioner"));
    PolyModelInstance<IllagerModel<IllusionerEntity>> ICELOGER = create(IllagerModel::new, IllagerModel.createBodyLayer(), FriendsAndFoes.makeID("entity/illager/iceologer"));
    EnumMap<MaulerEntity.Type, PolyModelInstance<MaulerEntityModel>> MAULER = Util.make(new EnumMap<>(MaulerEntity.Type.class), m -> {
        for (var type : MaulerEntity.Type.values()) {
            m.put(type, create(MaulerEntityModel::new, MaulerEntityModel.getTexturedModelData(), FriendsAndFoes.makeID("entity/mauler/mauler_" + type.getName())));
        }
    });

    static <T extends EntityModel<?>> PolyModelInstance<T> create(Function<ModelPart, T> modelCreator, LayerDefinition data, ResourceLocation texture) {
        var instance = PolyModelInstance.create(modelCreator, data, texture);
        ALL.add(instance);
        return instance;
    }

}
