package me.drex.fafpatch.impl.entity.model.entity;

import com.faboslav.friendsandfoes.common.entity.MaulerEntity;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.animation.KeyframeAnimation;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.*;
import me.drex.fafpatch.impl.entity.model.VersionedEntityModel;
import me.drex.fafpatch.impl.entity.model.animation.MaulerAnimations;
import net.minecraft.world.entity.AnimationState;

public final class MaulerEntityModel extends EntityModel<MaulerEntity> {
    private static final String MODEL_PART_MAIN = "main";
    private static final String MODEL_PART_HEAD = "head";
    private static final String MODEL_PART_UPPER_JAW = "upperJaw";
    private static final String MODEL_PART_LOWER_JAW = "lowerJaw";
    private static final String MODEL_PART_BODY = "body";
    private static final String MODEL_PART_FRONT_LEFT_LEG = "frontLeftLeg";
    private static final String MODEL_PART_FRONT_RIGHT_LEG = "frontRightLeg";
    private static final String MODEL_PART_BACK_LEFT_LEG = "backLeftLeg";
    private static final String MODEL_PART_BACK_RIGHT_LEG = "backRightLeg";

    private final ModelPart root;
    private final ModelPart main;
    private final ModelPart head;
    private final ModelPart upperJaw;
    private final ModelPart lowerJaw;
    private final ModelPart body;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart backRightLeg;

    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation snapAnimation;
    private final KeyframeAnimation runAnimation;
    private final KeyframeAnimation burrowDownAnimation;
    private final KeyframeAnimation burrowUpAnimation;

    public MaulerEntityModel(ModelPart root) {
        super(root);

        this.root = root;
        this.main = this.root.getChild(MODEL_PART_MAIN);
        this.head = this.main.getChild(MODEL_PART_HEAD);
        this.upperJaw = this.head.getChild(MODEL_PART_UPPER_JAW);
        this.lowerJaw = this.head.getChild(MODEL_PART_LOWER_JAW);
        this.body = this.main.getChild(MODEL_PART_BODY);
        this.frontLeftLeg = this.main.getChild(MODEL_PART_FRONT_LEFT_LEG);
        this.frontRightLeg = this.main.getChild(MODEL_PART_FRONT_RIGHT_LEG);
        this.backLeftLeg = this.main.getChild(MODEL_PART_BACK_LEFT_LEG);
        this.backRightLeg = this.main.getChild(MODEL_PART_BACK_RIGHT_LEG);

        this.idleAnimation = MaulerAnimations.IDLE.bake(root);
        this.snapAnimation = MaulerAnimations.SNAP.bake(root);
        this.runAnimation = MaulerAnimations.RUN.bake(root);
        this.burrowDownAnimation = MaulerAnimations.BURROW_DOWN.bake(root);
        this.burrowUpAnimation = MaulerAnimations.BURROW_UP.bake(root);
    }

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition root = modelData.getRoot();

		root.addOrReplaceChild(MODEL_PART_MAIN, CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition main = root.getChild(MODEL_PART_MAIN);

		main.addOrReplaceChild(MODEL_PART_HEAD, CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 4.0F));

		PartDefinition head = main.getChild(MODEL_PART_HEAD);
		head.addOrReplaceChild(MODEL_PART_UPPER_JAW, CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -3.0F, -10.0F, 9.0F, 3.0F, 10.0F), PartPose.offset(0.0F, -2.0F, 1.0F));
		head.addOrReplaceChild(MODEL_PART_LOWER_JAW, CubeListBuilder.create().texOffs(0, 13).addBox(-4.5F, 0.0F, -10.0F, 9.0F, 3.0F, 10.0F), PartPose.offset(0.0F, -2.0F, 1.0F));

		main.addOrReplaceChild(MODEL_PART_BODY, CubeListBuilder.create().texOffs(0, 26).addBox(-3.5F, 0.0F, -3.0F, 7.0F, 2.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -4.0F, 1.0F));
		main.addOrReplaceChild(MODEL_PART_FRONT_LEFT_LEG, CubeListBuilder.create().texOffs(0, 5).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(2.5F, -3.0F, -1.0F));
		main.addOrReplaceChild(MODEL_PART_FRONT_RIGHT_LEG, CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(-2.5F, -3.0F, -1.0F));
		main.addOrReplaceChild(MODEL_PART_BACK_LEFT_LEG, CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(2.5F, -3.0F, 3.0F));
		main.addOrReplaceChild(MODEL_PART_BACK_RIGHT_LEG, CubeListBuilder.create().texOffs(0, 13).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(-2.5F, -3.0F, 3.0F));

		return LayerDefinition.create(modelData, 64, 64);
	}

    @Override
    public void setupAnim(MaulerEntity mauler) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.updateKeyframeAnimations(mauler.idleAnimationState, mauler.snapAnimationState, mauler.burrowDownAnimationState, mauler.burrowUpAnimationState, mauler.walkAnimation.position(1), mauler.walkAnimation.speed(1), mauler.tickCount);
    }

    private void updateKeyframeAnimations(
        AnimationState idleAnimationState,
        AnimationState snapAnimationState,
        AnimationState burrowDownAnimationState,
        AnimationState burrowUpAnimationState,
        float limbSwing,
        float limbSwingAmount,
        float ageInTicks
    ) {
        VersionedEntityModel.animate(this, this.idleAnimation, idleAnimationState, ageInTicks);
        VersionedEntityModel.animate(this, this.snapAnimation, snapAnimationState, ageInTicks);
        VersionedEntityModel.animate(this, this.burrowDownAnimation, burrowDownAnimationState, ageInTicks);
        VersionedEntityModel.animate(this, this.burrowUpAnimation, burrowUpAnimationState, ageInTicks);
        VersionedEntityModel.animateWalk(this, this.runAnimation, limbSwing, limbSwingAmount, 2.5F, 3.5F);
    }
}
