package me.drex.fafpatch.impl.entity;

import com.faboslav.friendsandfoes.common.entity.IllusionerEntity;
import com.mojang.math.Axis;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import me.drex.fafpatch.impl.entity.model.IllagerModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fStack;

import java.util.HashMap;
import java.util.Map;


public class IllusionerElementHolder extends VanillishElementHolder<IllusionerEntity, IllagerModel<IllusionerEntity>> {

    private final Map<HumanoidArm, ItemDisplayElement> handItems = new HashMap<>();

    public IllusionerElementHolder(IllusionerEntity entity) {
        super(entity);
        setupHandItem(HumanoidArm.LEFT, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        setupHandItem(HumanoidArm.RIGHT, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
    }

    private void setupHandItem(HumanoidArm humanoidArm, ItemDisplayContext context) {
        var itemElement = ItemDisplayElementUtil.createSimple(ItemStack.EMPTY);
        itemElement.setDisplaySize(this.entity.getBbWidth() * 2, this.entity.getBbHeight() * 2);
        itemElement.setInterpolationDuration(1);
        itemElement.setTeleportDuration(3);
        itemElement.setViewRange(2);
        itemElement.setItemDisplayContext(context);
        itemElement.setOffset(new Vec3(0, 0.1, 0));
        addElement(itemElement);
        handItems.put(humanoidArm, itemElement);
    }

    @Override
    void renderServerSide(Matrix4fStack stack) {
        super.renderServerSide(stack);
        renderHandItem(HumanoidArm.LEFT, stack);
        renderHandItem(HumanoidArm.RIGHT, stack);
    }

    // Based on ItemInHandLayer
    private void renderHandItem(HumanoidArm humanoidArm, Matrix4fStack stack) {
        ItemStack itemHeldByArm = entity.getItemHeldByArm(humanoidArm);
        ItemDisplayElement handItemDisplay = handItems.get(humanoidArm);
        if (!itemHeldByArm.isEmpty()) {
            stack.pushMatrix();
            IllagerModel<IllusionerEntity> entityModel = getMainModel().model();

            // this.getParentModel().translateToHand(humanoidArm, poseStack);
            entityModel.translateToHand(humanoidArm, stack);

            stack.rotate(Axis.XP.rotationDegrees(-90.0F));
            stack.rotate(Axis.YP.rotationDegrees(180.0F));
            boolean bl = humanoidArm == HumanoidArm.LEFT;
            stack.translate((bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);

            handItemDisplay.setTransformation(stack);
            handItemDisplay.setItem(itemHeldByArm);
            stack.popMatrix();
        } else {
            handItemDisplay.setItem(ItemStack.EMPTY);
        }
    }
}
