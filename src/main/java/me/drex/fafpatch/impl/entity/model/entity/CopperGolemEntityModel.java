package me.drex.fafpatch.impl.entity.model.entity;

import com.faboslav.friendsandfoes.common.entity.CopperGolemEntity;
import com.faboslav.friendsandfoes.common.entity.animation.AnimationState;
import com.faboslav.friendsandfoes.common.entity.animation.animator.context.AnimationContextTracker;
import com.faboslav.friendsandfoes.common.entity.animation.animator.context.KeyframeAnimationContext;
import com.faboslav.friendsandfoes.common.entity.animation.animator.loader.json.AnimationHolder;
import com.faboslav.friendsandfoes.common.entity.pose.CopperGolemEntityPose;
import me.drex.fafpatch.impl.entity.model.animation.KeyframeModelAnimator;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.*;
import net.minecraft.util.Mth;

public class CopperGolemEntityModel extends EntityModel<CopperGolemEntity> {
    private static final String MODEL_PART_BODY = "body";
    private static final String MODEL_PART_LEFT_ARM = "leftArm";
    private static final String MODEL_PART_RIGHT_ARM = "rightArm";
    private static final String MODEL_PART_LEFT_LEG = "leftLeg";
    private static final String MODEL_PART_RIGHT_LEG = "rightLeg";
    private static final String MODEL_PART_HEAD = "head";
    private static final String MODEL_PART_NOSE = "nose";
    private static final String MODEL_PART_ROD = "rod";

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart rod;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public CopperGolemEntityModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = this.root.getChild(MODEL_PART_BODY);
        this.head = this.body.getChild(MODEL_PART_HEAD);
        this.nose = this.head.getChild(MODEL_PART_NOSE);
        this.rod = this.head.getChild(MODEL_PART_ROD);
        this.leftArm = this.body.getChild(MODEL_PART_LEFT_ARM);
        this.rightArm = this.body.getChild(MODEL_PART_RIGHT_ARM);
        this.leftLeg = this.root.getChild(MODEL_PART_LEFT_LEG);
        this.rightLeg = this.root.getChild(MODEL_PART_RIGHT_LEG);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        root.addOrReplaceChild(MODEL_PART_BODY, CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, -5.0F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, 0.0F));
        PartDefinition body = root.getChild(MODEL_PART_BODY);

        body.addOrReplaceChild(MODEL_PART_HEAD, CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-4.0F, -5.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.001F)).mirror(false), PartPose.offset(0.0F, -5.0F, 0.0F));
        PartDefinition head = body.getChild(MODEL_PART_HEAD);

        head.addOrReplaceChild(MODEL_PART_ROD, CubeListBuilder.create().texOffs(56, 10).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(40, 9).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));
        head.addOrReplaceChild(MODEL_PART_NOSE, CubeListBuilder.create().texOffs(56, 15).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));

        body.addOrReplaceChild(MODEL_PART_LEFT_ARM, CubeListBuilder.create().texOffs(10, 17).addBox(0.0F, -1.0F, -1.5F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -5.0F, 0.0F));
        body.addOrReplaceChild(MODEL_PART_RIGHT_ARM, CubeListBuilder.create().texOffs(0, 17).addBox(-2.0F, -1.0F, -1.5F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -5.0F, 0.0F));

        root.addOrReplaceChild(MODEL_PART_LEFT_LEG, CubeListBuilder.create().texOffs(34, 17).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 19.0F, 0.0F));
        root.addOrReplaceChild(MODEL_PART_RIGHT_LEG, CubeListBuilder.create().texOffs(20, 17).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 19.0F, 0.0F));

        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void setupAnim(CopperGolemEntity copperGolem) {
        super.setupAnim(copperGolem);
//        var copperGolem = renderState.copperGolem;
        float limbAngle = copperGolem.walkAnimation.position(1);
        float limbDistance = copperGolem.walkAnimation.speed(1);

        var animationProgress = copperGolem.tickCount;
        var headYaw = Mth.wrapDegrees(copperGolem.yHeadRot - copperGolem.yBodyRot);

        if (copperGolem.isOxidized()) {
            animationProgress = copperGolem.tickCount;
        }

        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.setHeadAngle(headYaw);

        if (copperGolem.isOxidized()) {
            this.updateStatueAnimations(copperGolem);
        }

        this.updateAnimations(copperGolem, limbAngle, limbDistance, animationProgress);
    }


    private void setHeadAngle(float yaw) {
        this.head.yRot = yaw * ((float) Math.PI / 180);
    }

    private void updateStatueAnimations(CopperGolemEntity copperGolem) {
        if (copperGolem.isInPose(CopperGolemEntityPose.IDLE)) {
            return;
        }

        AnimationHolder animation = copperGolem.getAnimationByPose();

        if (animation == null) {
            return;
        }

        int initialTick = copperGolem.tickCount - copperGolem.getCurrentKeyframeAnimationTick();
        int currentTick = copperGolem.tickCount;

        AnimationContextTracker animationContextTracker = copperGolem.getAnimationContextTracker();
        KeyframeAnimationContext keyframeAnimationContext = animationContextTracker.get(animation);
        keyframeAnimationContext.setInitialTick(initialTick);
        keyframeAnimationContext.setCurrentTick(currentTick);
        AnimationState animationState = new AnimationState();
        keyframeAnimationContext.setAnimationState(animationState);
        animationState.start(initialTick);
    }

    public void updateAnimations(
        CopperGolemEntity copperGolem,
        float limbAngle,
        float limbDistance,
        float animationProgress
    ) {
        var movementAnimation = copperGolem.getMovementAnimation();
        var animations = copperGolem.getTrackedAnimations();
        var animationContextTracker = copperGolem.getAnimationContextTracker();
        var currentTick = copperGolem.tickCount;
        var animationSpeedModifier = copperGolem.getAnimationSpeedModifier();

        KeyframeModelAnimator.updateMovementKeyframeAnimations(this, movementAnimation, limbAngle, limbDistance, 2.5F * copperGolem.getMovementSpeedModifier(), 3.5F * copperGolem.getMovementSpeedModifier(), animationSpeedModifier);
        KeyframeModelAnimator.updateKeyframeAnimations(this, animationContextTracker, animations, currentTick, animationProgress, animationSpeedModifier);
    }
}