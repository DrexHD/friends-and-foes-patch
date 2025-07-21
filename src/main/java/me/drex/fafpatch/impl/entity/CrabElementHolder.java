package me.drex.fafpatch.impl.entity;

import com.faboslav.friendsandfoes.common.entity.CrabEntity;
import me.drex.fafpatch.impl.entity.model.CrabEntityModel;
import me.drex.fafpatch.impl.entity.model.EntityModels;
import net.minecraft.world.entity.AgeableMob;
import org.joml.Matrix4fStack;

public class CrabElementHolder extends VanillishElementHolder<CrabEntity, CrabEntityModel> {
    public CrabElementHolder(CrabEntity entity) {
        super(entity);
    }

    @Override
    float getEntityScale() {
        return entity.isBaby() ? CrabEntity.BABY_SCALE : 1;
    }
}
