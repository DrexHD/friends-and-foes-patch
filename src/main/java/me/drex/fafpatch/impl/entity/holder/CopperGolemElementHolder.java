package me.drex.fafpatch.impl.entity.holder;

import com.faboslav.friendsandfoes.common.entity.CopperGolemEntity;
import me.drex.fafpatch.impl.entity.SimpleElementHolder;
import me.drex.fafpatch.impl.entity.model.entity.CopperGolemEntityModel;
import me.drex.fafpatch.impl.entity.model.EntityModels;

public class CopperGolemElementHolder extends SimpleElementHolder<CopperGolemEntity, CopperGolemEntityModel> {
    public CopperGolemElementHolder(CopperGolemEntity entity) {
        super(entity);
        addConditionalLayer(CopperGolemEntity::getOxidationLevel, MAIN_LAYER, EntityModels.COPPER_GOLEM::get);
    }
}
