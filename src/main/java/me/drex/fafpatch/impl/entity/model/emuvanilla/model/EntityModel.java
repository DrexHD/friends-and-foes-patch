package me.drex.fafpatch.impl.entity.model.emuvanilla.model;

import net.minecraft.world.entity.Entity;

public abstract class EntityModel<T extends Entity> extends Model {
    protected EntityModel(ModelPart root) {
        super(root);
    }

    public void setupAnim(T state) {
        this.resetPose();
    }
}