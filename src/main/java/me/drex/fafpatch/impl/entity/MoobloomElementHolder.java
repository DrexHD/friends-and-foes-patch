package me.drex.fafpatch.impl.entity;

import com.faboslav.friendsandfoes.common.entity.MoobloomEntity;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import me.drex.fafpatch.impl.entity.model.CowModel;
import me.drex.fafpatch.impl.entity.model.EntityModels;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fStack;

public class MoobloomElementHolder extends VanillishElementHolder<MoobloomEntity, CowModel<MoobloomEntity>> {

    private final BlockDisplayElement[] flowerModels = new BlockDisplayElement[4];

    public MoobloomElementHolder(MoobloomEntity entity) {
        super(entity);
        for (int i = 0; i < 4; i++) {
            var element = new BlockDisplayElement();
            element.setInterpolationDuration(1);
            element.setTeleportDuration(3);
            element.setViewRange(2);
            element.setOffset(new Vec3(0, 0.1, 0));
            addElement(element);
            flowerModels[i] = element;
        }
        addConditionalLayer(RenderState::new, MAIN_LAYER, EntityModels.MOOBLOOM::get);
    }

    @Override
    void renderServerSide(Matrix4fStack stack) {
        super.renderServerSide(stack);
        renderFlowers(stack);
    }

    private void renderFlowers(Matrix4fStack stack) {
        if (!entity.isBaby() && !entity.isInvisible()) {
            var flower = entity.getVariant().getFlower();
            BlockState blockState = entity.getVariant().getFlower().defaultBlockState();

            if (flower instanceof DoublePlantBlock) {
                blockState = entity.getVariant().getFlower().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
            }

            float scaleFactor = 0.8F;
            float yOffset = -0.5F;

            if (flower instanceof DoublePlantBlock) {
                scaleFactor = 0.6F;
                yOffset = -0.666F;
            }

            // Head
            stack.pushMatrix();
            getMainModel().model().getHead().applyTransform(stack);
            stack.translate(0.09f, -0.6f, -0.185f);
            stack.scale(-scaleFactor, -scaleFactor, scaleFactor);
            stack.translate(-0.5f, yOffset, -0.5f);
            this.renderFlower(stack, blockState, 0);
            stack.popMatrix();

            // Body
            stack.pushMatrix();
            stack.translate(0.22f, -0.28f, -0.06f);
            stack.scale(-scaleFactor, -scaleFactor, scaleFactor);
            stack.translate(-0.5f, yOffset, -0.5f);
            this.renderFlower(stack, blockState, 1);
            stack.popMatrix();

            stack.pushMatrix();
            stack.translate(-0.2f, -0.22f, 0.01f);
            stack.scale(-scaleFactor, -scaleFactor, scaleFactor);
            stack.translate(-0.5f, yOffset, -0.5f);
            this.renderFlower(stack, blockState, 2);
            stack.popMatrix();

            stack.pushMatrix();
            stack.translate(0.03f, -0.28f, 0.47f);
            stack.scale(-scaleFactor, -scaleFactor, scaleFactor);
            stack.translate(-0.5f, yOffset, -0.5f);
            this.renderFlower(stack, blockState, 3);
            stack.popMatrix();
        }
    }

    private void renderFlower(Matrix4fStack stack, BlockState flowerState, int index) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(flowerState.getBlock());
        if (!id.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
            // rendering modded blocks is annoying
            flowerState = Blocks.DANDELION.defaultBlockState();
        }

        flowerModels[index].setBlockState(flowerState);
        flowerModels[index].setTransformation(stack);

    }

    public record RenderState(String variant, boolean baby) {
        public RenderState(MoobloomEntity moobloomEntity) {
            this(moobloomEntity.getVariant().getName(), moobloomEntity.isBaby());
        }
    }
}