package me.drex.fafpatch.impl.entity;

import com.faboslav.friendsandfoes.common.entity.MaulerEntity;
import me.drex.fafpatch.impl.entity.model.EntityModels;
import me.drex.fafpatch.impl.entity.model.MaulerEntityModel;

public class MaulerElementHolder extends VanillishElementHolder<MaulerEntity, MaulerEntityModel> {
    public MaulerElementHolder(MaulerEntity entity) {
        super(entity);
        addConditionalLayer(MaulerEntity::getMaulerType, MAIN_LAYER, EntityModels.MAULER::get);
    }
}
