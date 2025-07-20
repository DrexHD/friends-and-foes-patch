package me.drex.fafpatch.impl.entity;

import com.faboslav.friendsandfoes.common.entity.CrabEntity;
import me.drex.fafpatch.impl.entity.model.CrabEntityModel;
import me.drex.fafpatch.impl.entity.model.EntityModels;
import net.minecraft.world.entity.AgeableMob;

public class CrabElementHolder extends VanillishElementHolder<CrabEntity, CrabEntityModel> {
    public CrabElementHolder(CrabEntity entity) {
        super(entity);
        addConditionalLayer(AgeableMob::isBaby, MAIN_LAYER, isBaby -> isBaby ? EntityModels.CRAB_BABY : EntityModels.CRAB);
    }
}
