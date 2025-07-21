package me.drex.fafpatch.impl.entity.holder;

import me.drex.fafpatch.impl.FriendsAndFoesPatch;
import me.drex.fafpatch.impl.entity.SimpleElementHolder;
import me.drex.fafpatch.impl.entity.model.EntityModels;
import me.drex.fafpatch.impl.entity.model.entity.VillagerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;

public class VillagerElementHolder extends SimpleElementHolder<Villager, VillagerModel> {
    public static final ResourceLocation PROFESSION_LEVEL_LAYER = FriendsAndFoesPatch.id("villager_profession_level_layer");
    public static final ResourceLocation PROFESSION_LAYER = FriendsAndFoesPatch.id("villager_profession_layer");
    public static final ResourceLocation TYPE_LAYER = FriendsAndFoesPatch.id("villager_type_layer");


    public VillagerElementHolder(Villager entity) {
        super(entity);
        addConditionalLayer(villager -> villager.getVillagerData().type().unwrapKey().get().location(), TYPE_LAYER, EntityModels.VILLAGER_TYPE::get);
        addConditionalLayer(villager -> villager.getVillagerData().profession().unwrapKey().get().location(), PROFESSION_LAYER, EntityModels.VILLAGER_PROFESSION::get);
        addConditionalLayer(villager -> villager.getVillagerData().level(), PROFESSION_LEVEL_LAYER, EntityModels.VILLAGER_PROFESSION_LEVEL::get);
    }
}
