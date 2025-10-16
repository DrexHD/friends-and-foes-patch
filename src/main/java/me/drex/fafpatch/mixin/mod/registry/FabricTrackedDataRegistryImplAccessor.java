package me.drex.fafpatch.mixin.mod.registry;

import net.fabricmc.fabric.impl.object.builder.FabricTrackedDataRegistryImpl;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FabricTrackedDataRegistryImpl.class)
public interface FabricTrackedDataRegistryImplAccessor {
    @Accessor
    static Registry<EntityDataSerializer<?>> getHandlerRegistry() {
        throw new AssertionError();
    };
}
