package me.drex.fafpatch.impl.entity;

import com.faboslav.friendsandfoes.common.entity.CopperGolemEntity;
import me.drex.fafpatch.impl.entity.model.CopperGolemEntityModel;
import me.drex.fafpatch.impl.entity.model.EntityModels;

public class CopperGolemElementHolder extends VanillishElementHolder<CopperGolemEntity, CopperGolemEntityModel> {
    public CopperGolemElementHolder(CopperGolemEntity entity) {
        super(entity);
        addConditionalLayer(CopperGolemEntity::getOxidationLevel, MAIN_LAYER, EntityModels.COPPER_GOLEM::get);
    }
}
