package me.drex.fafpatch.impl.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import me.drex.fafpatch.impl.model.generic.BSMMParticleBlock;
import me.drex.fafpatch.impl.model.generic.ShiftyBlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Function;

public record StateCopyFactoryBlock(Block clientBlock, Function<BlockState, BlockModel> modelFunction) implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    public static final StateCopyFactoryBlock EGG = new StateCopyFactoryBlock(Blocks.TURTLE_EGG, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock BUTTON = new StateCopyFactoryBlock(Blocks.STONE_BUTTON, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock POT = new StateCopyFactoryBlock(Blocks.FLOWER_POT, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock LIGHTNING_ROD = new StateCopyFactoryBlock(Blocks.LIGHTNING_ROD, ShiftyBlockStateModel::longRange);

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return clientBlock.withPropertiesOf(state);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return this.modelFunction.apply(initialBlockState);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return true;
    }
}
