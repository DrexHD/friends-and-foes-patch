package me.drex.fafpatch.impl.entity.model.entity;

import com.faboslav.friendsandfoes.common.entity.TuffGolemEntity;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.animation.KeyframeAnimation;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.*;
import me.drex.fafpatch.impl.entity.model.VersionedEntityModel;
import me.drex.fafpatch.impl.entity.model.animation.TuffGolemAnimations;

public class TuffGolemEntityModel extends EntityModel<TuffGolemEntity> {
    private static final String MODEL_PART_BODY = "body";
    private static final String MODEL_PART_NOSE = "nose";
    private static final String MODEL_PART_FRONT_CLOTH = "frontCloth";
    private static final String MODEL_PART_BACK_CLOTH = "backCloth";
    private static final String MODEL_PART_CLOTH_STAND = "clothStand";
    private static final String MODEL_PART_LEFT_ARM = "leftArm";
    private static final String MODEL_PART_RIGHT_ARM = "rightArm";
    private static final String MODEL_PART_LEFT_LEG = "leftLeg";
    private static final String MODEL_PART_RIGHT_LEG = "rightLeg";

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart nose;
    private final ModelPart frontCloth;
    private final ModelPart backCloth;
    private final ModelPart clothStand;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final KeyframeAnimation showItemAnimation;
    private final KeyframeAnimation hideItemAnimation;
    private final KeyframeAnimation sleepAnimation;
    private final KeyframeAnimation sleepWithItemAnimation;
    private final KeyframeAnimation wakeAnimation;
    private final KeyframeAnimation wakeWithItemAnimation;
    private final KeyframeAnimation wakeAndShowItemAnimation;
    private final KeyframeAnimation wakeAndHideItemAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation walkWithItemAnimation;

    public TuffGolemEntityModel(ModelPart root) {
        super(root);

        this.root = root;
        this.body = this.root.getChild(MODEL_PART_BODY);
        this.nose = this.body.getChild(MODEL_PART_NOSE);
        this.frontCloth = this.body.getChild(MODEL_PART_FRONT_CLOTH);
        this.backCloth = this.body.getChild(MODEL_PART_BACK_CLOTH);
        this.clothStand = this.body.getChild(MODEL_PART_CLOTH_STAND);
        this.leftArm = this.body.getChild(MODEL_PART_LEFT_ARM);
        this.rightArm = this.body.getChild(MODEL_PART_RIGHT_ARM);
        this.leftLeg = this.root.getChild(MODEL_PART_LEFT_LEG);
        this.rightLeg = this.root.getChild(MODEL_PART_RIGHT_LEG);

        this.showItemAnimation = TuffGolemAnimations.SHOW_ITEM.bake(root);
        this.hideItemAnimation = TuffGolemAnimations.HIDE_ITEM.bake(root);
        this.sleepAnimation = TuffGolemAnimations.SLEEP.bake(root);
        this.sleepWithItemAnimation = TuffGolemAnimations.SLEEP_WITH_ITEM.bake(root);
        this.wakeAnimation = TuffGolemAnimations.WAKE.bake(root);
        this.wakeWithItemAnimation = TuffGolemAnimations.WAKE_WITH_ITEM.bake(root);
        this.wakeAndShowItemAnimation = TuffGolemAnimations.WAKE_AND_SHOW_ITEM.bake(root);
        this.wakeAndHideItemAnimation = TuffGolemAnimations.WAKE_AND_HIDE_ITEM.bake(root);
        this.walkAnimation = TuffGolemAnimations.WALK.bake(root);
        this.walkWithItemAnimation = TuffGolemAnimations.WALK_WITH_ITEM.bake(root);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        root.addOrReplaceChild(MODEL_PART_BODY, CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 13.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, 0.0F));

        PartDefinition body = root.getChild(MODEL_PART_BODY);
        body.addOrReplaceChild(MODEL_PART_NOSE, CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, -4.0F));
        body.addOrReplaceChild(MODEL_PART_FRONT_CLOTH, CubeListBuilder.create().texOffs(36, 4).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, -4.0F, -4.0F));
        body.addOrReplaceChild(MODEL_PART_BACK_CLOTH, CubeListBuilder.create().texOffs(40, 18).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, -4.0F, 0.0F));
        body.addOrReplaceChild(MODEL_PART_CLOTH_STAND, CubeListBuilder.create().texOffs(0, 35).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 5.0F, 7.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -4.0F, 0.0F));
        body.addOrReplaceChild(MODEL_PART_LEFT_ARM, CubeListBuilder.create().texOffs(0, 21).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -4.0F, 0.0F));
        body.addOrReplaceChild(MODEL_PART_RIGHT_ARM, CubeListBuilder.create().texOffs(0, 21).mirror(true).addBox(0.0F, -2.0F, -2.0F, 2.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -4.0F, 0.0F));

        root.addOrReplaceChild(MODEL_PART_LEFT_LEG, CubeListBuilder.create().texOffs(12, 21).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.0005F)), PartPose.offset(-2.0F, 19.0F, 0.0F));
        root.addOrReplaceChild(MODEL_PART_RIGHT_LEG, CubeListBuilder.create().texOffs(12, 21).mirror(true).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.0005F)), PartPose.offset(2.0F, 19.0F, 0.0F));

        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void setupAnim(TuffGolemEntity tuffGolem) {
        super.setupAnim(tuffGolem);
        float limbAngle = tuffGolem.walkAnimation.position(1);
        float limbDistance = tuffGolem.walkAnimation.speed(1);

        this.root().getAllParts().forEach(ModelPart::resetPose);
        VersionedEntityModel.animate(this, this.showItemAnimation, tuffGolem.showItemAnimationState, tuffGolem.tickCount);
        VersionedEntityModel.animate(this, this.hideItemAnimation, tuffGolem.hideItemAnimationState, tuffGolem.tickCount);
        VersionedEntityModel.animate(this, this.sleepAnimation, tuffGolem.sleepAnimationState, tuffGolem.tickCount);
        VersionedEntityModel.animate(this, this.sleepWithItemAnimation, tuffGolem.sleepWithItemAnimationState, tuffGolem.tickCount);
        VersionedEntityModel.animate(this, this.wakeAnimation, tuffGolem.wakeAnimationState, tuffGolem.tickCount);
        VersionedEntityModel.animate(this, this.wakeWithItemAnimation, tuffGolem.wakeWithItemAnimationState, tuffGolem.tickCount);
        VersionedEntityModel.animate(this, this.wakeAndShowItemAnimation, tuffGolem.wakeAndShowItemAnimationState, tuffGolem.tickCount);
        VersionedEntityModel.animate(this, this.wakeAndHideItemAnimation, tuffGolem.wakeAndHideItemAnimationState, tuffGolem.tickCount);
        VersionedEntityModel.animateWalk(this, tuffGolem.isHoldingItem() ? this.walkWithItemAnimation : this.walkAnimation, limbAngle, limbDistance, 4.0F * tuffGolem.getMovementSpeedModifier(), 4.0F * tuffGolem.getMovementSpeedModifier());
    }
}
