package me.drex.fafpatch.impl.entity.model.entity;

import com.faboslav.friendsandfoes.common.entity.BarnacleEntity;
import me.drex.fafpatch.impl.entity.model.animation.BarnacleAnimations;
import me.drex.fafpatch.impl.entity.model.VersionedEntityModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.animation.KeyframeAnimation;
public final class BarnacleEntityModel extends EntityModel<BarnacleEntity>
{
	public static final float SCALE = 1.5F;

	private static final String MODEL_PART_BODY = "body";
	private static final String MODEL_PART_HEAD = "head";
	private static final String MODEL_PART_TENTACLE = "tentacle";
	private static final String MODEL_PART_TOP_LEFT_MOUTH = "topLeftMouth";
	private static final String MODEL_PART_TOP_RIGHT_MOUTH = "topRightMouth";
	private static final String MODEL_PART_BOTTOM_LEFT_MOUTH = "bottomLeftMouth";
	private static final String MODEL_PART_BOTTOM_RIGHT_MOUTH = "bottomRightMouth";
	private static final String MODEL_PART_KELP = "kelp";
	private static final float TENTACLE_LENGTH = 26.0F;
	private static final float MODEL_Y_OFFSET = 1.501F;
	private static final float PIXELS_PER_BLOCK = 16.0F;
	private static final float MAX_SWIMMING_PITCH = 45.0F;
	private static final int TENTACLE_ATTACK_ANIMATION_DELAY = Math.max(0, BarnacleEntity.TENTACLE_GRAB_DURATION - Math.round(BarnacleAnimations.TENTACLE_ATTACK.lengthInSeconds() * 20.0F));

	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart tentacle;
	private final ModelPart topLeftMouth;
	private final ModelPart topRightMouth;
	private final ModelPart bottomLeftMouth;
	private final ModelPart bottomRightMouth;
	private final ModelPart kelp;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation swimAnimation;
	private final KeyframeAnimation tentacleAttackAnimation;
	private final KeyframeAnimation attackAnimation;

	public BarnacleEntityModel(ModelPart root) {
		super(root);

		this.root = root;
		this.body = this.root.getChild(MODEL_PART_BODY);
		this.head = this.body.getChild(MODEL_PART_HEAD);
		this.tentacle = this.body.getChild(MODEL_PART_TENTACLE);
		this.topLeftMouth = this.body.getChild(MODEL_PART_TOP_LEFT_MOUTH);
		this.topRightMouth = this.body.getChild(MODEL_PART_TOP_RIGHT_MOUTH);
		this.bottomLeftMouth = this.body.getChild(MODEL_PART_BOTTOM_LEFT_MOUTH);
		this.bottomRightMouth = this.body.getChild(MODEL_PART_BOTTOM_RIGHT_MOUTH);
		this.kelp = this.body.getChild(MODEL_PART_KELP);
		this.idleAnimation = BarnacleAnimations.IDLE.bake(root);
		this.swimAnimation = BarnacleAnimations.SWIM.bake(root);
		this.tentacleAttackAnimation = BarnacleAnimations.TENTACLE_ATTACK.bake(root);
		this.attackAnimation = BarnacleAnimations.ATTACK.bake(root);
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition root = modelData.getRoot();

		root.addOrReplaceChild(MODEL_PART_BODY, CubeListBuilder.create(), PartPose.offset(0.0F, 18.0F, 0.0F));

		PartDefinition body = root.getChild(MODEL_PART_BODY);
		body.addOrReplaceChild(MODEL_PART_HEAD, CubeListBuilder.create().texOffs(0, 28).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 9.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 11.0F));
		body.addOrReplaceChild(MODEL_PART_TENTACLE, CubeListBuilder.create().texOffs(10, 37).addBox(-0.5F, -0.5F, -26.0F, 1.0F, 1.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 11.0F));
		body.addOrReplaceChild(MODEL_PART_TOP_LEFT_MOUTH, CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, -4.0F, -22.0F, 6.0F, 6.0F, 22.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, 0.0F, 11.0F, 0.0F, 0.0F, -1.5708F));
		body.addOrReplaceChild(MODEL_PART_TOP_RIGHT_MOUTH, CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -4.0F, -22.0F, 6.0F, 6.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 11.0F, 0.0F, 0.0F, 1.5708F));
		body.addOrReplaceChild(MODEL_PART_BOTTOM_LEFT_MOUTH, CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-4.0F, 0.0F, -22.0F, 6.0F, 6.0F, 22.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 0.0F, 11.0F));
		body.addOrReplaceChild(MODEL_PART_BOTTOM_RIGHT_MOUTH, CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -22.0F, 6.0F, 6.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.0F, 11.0F));
		body.addOrReplaceChild(MODEL_PART_KELP, CubeListBuilder.create().texOffs(0, 45).addBox(-4.0F, -4.0F, 1.0F, 8.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 19.0F));

		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	public void setupAnim(BarnacleEntity barnacle) {
		this.root.getAllParts().forEach(ModelPart::resetPose);
		this.updateKeyframeAnimations(barnacle.idleAnimationState, barnacle.tentacleAttackAnimationState, barnacle.attackAnimationState, barnacle.walkAnimation.position(1), barnacle.walkAnimation.speed(1), barnacle.tickCount);
		Entity target = barnacle.getTentacleTarget();
		boolean aimsBody = this.updateTentacle(barnacle.getX(), barnacle.getY(), barnacle.getZ(), barnacle.yBodyRot,
			target == null ? null : BarnacleEntity.getTentacleAttachPosition(target, 1), barnacle.getTentacleExtension(1));
		if (!aimsBody) {
			this.updateSwimmingPitch(barnacle.getXRot(), barnacle.isInWater());
		}
	}

	public void updateKeyframeAnimations(
		AnimationState idleAnimationState,
		AnimationState tentacleAttackAnimationState,
		AnimationState attackAnimationState,
		float limbSwing,
		float limbSwingAmount,
		float ageInTicks
	) {
		VersionedEntityModel.animate(this, this.idleAnimation, idleAnimationState, ageInTicks);
		VersionedEntityModel.animateWalk(this, this.swimAnimation, limbSwing, limbSwingAmount, 2.5F, 4.0F);
		VersionedEntityModel.animate(this, this.tentacleAttackAnimation, tentacleAttackAnimationState, ageInTicks - TENTACLE_ATTACK_ANIMATION_DELAY);
		VersionedEntityModel.animate(this, this.attackAnimation, attackAnimationState, ageInTicks);
	}

	private void updateSwimmingPitch(float xRot, boolean isInWater) {
		if (!isInWater) {
			return;
		}

		this.body.xRot += Mth.clamp(xRot, -MAX_SWIMMING_PITCH, MAX_SWIMMING_PITCH) * Mth.DEG_TO_RAD;
	}

	private boolean updateTentacle(
		double x,
		double y,
		double z,
		float bodyRot,
		@Nullable Vec3 targetPosition,
		float extension
	) {
		if (targetPosition == null || extension <= 0.0F) {
			this.tentacle.visible = false;
			return false;
		}

		this.tentacle.visible = true;
		this.tentacle.resetPose();

		Vec3 relativePosition = targetPosition.subtract(x, y, z).yRot((bodyRot - 180.0F) * Mth.DEG_TO_RAD);
		Vec3 targetFromBody = new Vec3(
			-relativePosition.x / SCALE * PIXELS_PER_BLOCK - this.body.x,
			(MODEL_Y_OFFSET - relativePosition.y / SCALE) * PIXELS_PER_BLOCK - this.body.y,
			relativePosition.z / SCALE * PIXELS_PER_BLOCK - this.body.z
		);

		this.body.yRot = getYaw(targetFromBody) * extension;
		this.body.xRot = getPitch(targetFromBody) * extension;

		Vec3 targetFromTentacle = targetFromBody
			.yRot(-this.body.yRot)
			.xRot(this.body.xRot)
			.subtract(this.tentacle.x, this.tentacle.y, this.tentacle.z);

		this.tentacle.yRot = getYaw(targetFromTentacle);
		this.tentacle.xRot = getPitch(targetFromTentacle);
		this.tentacle.zScale = (float) (targetFromTentacle.length() / TENTACLE_LENGTH) * extension;

		return true;
	}

	private static float getYaw(Vec3 direction) {
		return (float) Mth.atan2(-direction.x, -direction.z);
	}

	private static float getPitch(Vec3 direction) {
		return (float) Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));
	}
}
