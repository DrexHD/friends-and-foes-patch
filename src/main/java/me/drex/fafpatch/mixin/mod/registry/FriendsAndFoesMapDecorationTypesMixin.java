package me.drex.fafpatch.mixin.mod.registry;

import com.faboslav.friendsandfoes.common.init.FriendsAndFoesMapDecorationTypes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FriendsAndFoesMapDecorationTypes.class)
public abstract class FriendsAndFoesMapDecorationTypesMixin {
    @WrapOperation(
        method = "lambda$static$0",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/resources/Identifier;ZZ)Lnet/minecraft/world/level/saveddata/maps/MapDecorationType;"
        )
    )
    private static MapDecorationType polymerify(Identifier assetId, boolean showOnItemFrame, boolean trackCount, Operation<MapDecorationType> original) {
        var registryEntry = original.call(assetId, showOnItemFrame, trackCount);
        RegistrySyncUtils.setServerEntry(BuiltInRegistries.MAP_DECORATION_TYPE, registryEntry);
        return registryEntry;
    }
}
