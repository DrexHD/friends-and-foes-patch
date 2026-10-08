package me.drex.fafpatch.impl.entity.model;

import eu.pb4.factorytools.api.virtualentity.emuvanilla.animation.KeyframeAnimation;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.EntityModel;
import net.minecraft.world.entity.AnimationState;

public class VersionedEntityModel {
    public static void animate(EntityModel<?> entityModel, KeyframeAnimation animation, AnimationState animationState, float ageInTicks)
    {
        animate(entityModel, animation, animationState, ageInTicks, 1.0F);
    }

    public static void animate(EntityModel<?> entityModel, KeyframeAnimation animation, AnimationState animationState, float ageInTicks, float speedMultiplier)
    {
        animation.apply(animationState, ageInTicks, speedMultiplier);
    }

    public static void animateWalk(EntityModel<?> entityModel, KeyframeAnimation animation, float limbSwing, float limbSwingAmount, float timeMultiplier, float speedMultiplier)
    {
        animation.applyWalk(limbSwing, limbSwingAmount, timeMultiplier, speedMultiplier);
    }
}
