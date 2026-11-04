package me.drex.fafpatch.mixin.mod.registry;

import com.faboslav.friendsandfoes.common.init.FriendsAndFoesMapDecorationTypes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
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
        PolymerSyncedObject.setSyncedObject(BuiltInRegistries.MAP_DECORATION_TYPE, registryEntry, (_, _) -> MapDecorationTypes.RED_X.value());
        return registryEntry;
    }
}
