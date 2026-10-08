package me.drex.fafpatch.impl.entity.holder;

import com.faboslav.friendsandfoes.common.entity.PenguinEntity;
import me.drex.fafpatch.impl.entity.SimpleElementHolder;
import me.drex.fafpatch.impl.entity.model.entity.PenguinEntityModel;

public class PenguinElementHolder extends SimpleElementHolder<PenguinEntity, PenguinEntityModel> {
    public PenguinElementHolder(PenguinEntity entity) {
        super(entity);
    }

    @Override
    public float getEntityScale() {
        return entity.isBaby() ? PenguinEntity.BABY_SCALE : 1;
    }
}
