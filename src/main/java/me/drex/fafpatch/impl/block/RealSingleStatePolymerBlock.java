package me.drex.fafpatch.impl.block;

import com.faboslav.friendsandfoes.common.FriendsAndFoes;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.BlockStateAsset;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xyz.nucleoid.packettweaker.PacketContext;

import java.nio.file.Files;
import java.util.Set;

public record RealSingleStatePolymerBlock(BlockState state) implements PolymerTexturedBlock {

    public static RealSingleStatePolymerBlock of(Block block, BlockModelType type) {
        var id = BuiltInRegistries.BLOCK.getKey(block);

        var path = FabricLoader.getInstance().getModContainer(FriendsAndFoes.MOD_ID).get()
                .findPath("assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json").get();
        BlockStateAsset decoded;
        try {
            decoded = BlockStateAsset.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(path))).getOrThrow().getFirst();
            var set = Set.copyOf(decoded.variants().get().values());


            var model = set.size() == 1 ? set.iterator().next() : decoded.variants().orElseThrow().get("");

            return new RealSingleStatePolymerBlock(PolymerBlockResourceUtils.requestBlock(
                    type,
                    model.stream().map(x -> new PolymerBlockModel(x.model(), x.x(), x.y(), x.uvlock(), x.weigth())).toArray(PolymerBlockModel[]::new)));
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return state;
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return true;
    }
}
