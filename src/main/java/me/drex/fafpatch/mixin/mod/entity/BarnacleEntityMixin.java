package me.drex.fafpatch.mixin.mod.entity;

import com.faboslav.friendsandfoes.common.entity.BarnacleEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BarnacleEntity.class)
public abstract class BarnacleEntityMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z", ordinal = 1))
    public boolean serverSideAnimation(Level instance, Operation<Boolean> original) {
        return true;
    }
}
