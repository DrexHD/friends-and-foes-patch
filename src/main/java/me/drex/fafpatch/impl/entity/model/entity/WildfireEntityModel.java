package me.drex.fafpatch.impl.entity.model.entity;

import com.faboslav.friendsandfoes.common.entity.WildfireEntity;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.EntityValueExtraction;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.animation.KeyframeAnimation;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.*;
import me.drex.fafpatch.impl.entity.model.VersionedEntityModel;
import me.drex.fafpatch.impl.entity.model.animation.WildfireAnimations;

import java.util.List;

public final class WildfireEntityModel extends EntityModel<WildfireEntity> {
    private static final String MODEL_PART_BODY = "body";
    private static final String MODEL_PART_HEAD = "head";
    private static final String MODEL_PART_HELMET = "helmet";
    private static final String MODEL_PART_SHIELDS = "shields";
    private static final String MODEL_PART_FRONT_SHIELD = "frontShield";
    private static final String MODEL_PART_RIGHT_SHIELD = "rightShield";
    private static final String MODEL_PART_BACK_SHIELD = "backShield";
    private static final String MODEL_PART_LEFT_SHIELD = "leftShield";

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart helmet;
    private final ModelPart shields;
    private final ModelPart frontShield;
    private final ModelPart rightShield;
    private final ModelPart backShield;
    private final ModelPart leftShield;

    private final List<ModelPart> shieldsModelParts;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation shieldRotationAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation shockwaveAnimation;

    public WildfireEntityModel(ModelPart root) {
        super(root);

        this.root = root;
        this.body = this.root.getChild(MODEL_PART_BODY);
        this.head = this.body.getChild(MODEL_PART_HEAD);
        this.helmet = this.head.getChild(MODEL_PART_HELMET);
        this.shields = this.root.getChild(MODEL_PART_SHIELDS);
        this.frontShield = this.shields.getChild(MODEL_PART_FRONT_SHIELD);
        this.rightShield = this.shields.getChild(MODEL_PART_RIGHT_SHIELD);
        this.backShield = this.shields.getChild(MODEL_PART_BACK_SHIELD);
        this.leftShield = this.shields.getChild(MODEL_PART_LEFT_SHIELD);

        this.shieldsModelParts = this.shields.getAllParts();
        this.idleAnimation = WildfireAnimations.IDLE.bake(root);
        this.shieldRotationAnimation = WildfireAnimations.SHIELD_ROTATION.bake(root);
        this.walkAnimation = WildfireAnimations.WALK.bake(root);
        this.shockwaveAnimation = WildfireAnimations.SHOCKWAVE.bake(root);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        PartDefinition body = root.addOrReplaceChild(MODEL_PART_BODY, CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -21.0F, -2.0F, 4.0F, 21.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild(MODEL_PART_HEAD, CubeListBuilder.create().texOffs(0, 26).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

        head.addOrReplaceChild(MODEL_PART_HELMET, CubeListBuilder.create().texOffs(0, 43).addBox(-4.0F, -7.5F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 1.5F, 0.0F));

        PartDefinition shields = root.addOrReplaceChild(MODEL_PART_SHIELDS, CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        shields.addOrReplaceChild(MODEL_PART_FRONT_SHIELD, CubeListBuilder.create().texOffs(17, 0).addBox(-5.0F, 3.5F, -9.5F, 10.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -22.0F, 0.0F, -0.2618F, 0.0F, 0.0F));
        shields.addOrReplaceChild(MODEL_PART_RIGHT_SHIELD, CubeListBuilder.create().texOffs(17, 0).addBox(-5.0F, 3.5F, -9.5F, 10.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -22.0F, 0.0F, -0.2618F, 1.5708F, 0.0F));
        shields.addOrReplaceChild(MODEL_PART_BACK_SHIELD, CubeListBuilder.create().texOffs(17, 0).addBox(-5.0F, 3.5F, -9.5F, 10.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -22.0F, 0.0F, -0.2618F, 3.1416F, 0.0F));
        shields.addOrReplaceChild(MODEL_PART_LEFT_SHIELD, CubeListBuilder.create().texOffs(17, 0).addBox(-5.0F, 3.5F, -9.5F, 10.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -22.0F, 0.0F, -0.2618F, -1.5708F, 0.0F));

        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void setupAnim(WildfireEntity wildfire) {
        float limbAngle = wildfire.walkAnimation.position(1);
        float limbDistance = wildfire.walkAnimation.speed(1);
        var headYaw = EntityValueExtraction.getRelativeHeadYaw(wildfire) * ((float)Math.PI / 180);
        var headPitch = wildfire.getXRot();

        int activeShieldsCount = wildfire.getActiveShieldsCount();

        for (int i = 0; i < WildfireEntity.DEFAULT_ACTIVE_SHIELDS_COUNT; ++i) {
            this.shieldsModelParts.get(i).skipDraw = i > activeShieldsCount;
        }

        this.root().getAllParts().forEach(ModelPart::resetPose);
        VersionedEntityModel.animate(this, this.idleAnimation, wildfire.idleAnimationState, wildfire.tickCount);
        VersionedEntityModel.animate(this, this.shieldRotationAnimation, wildfire.shieldRotationAnimationState, wildfire.tickCount);
        VersionedEntityModel.animate(this, this.shockwaveAnimation, wildfire.shockwaveAnimationState, wildfire.tickCount);
        VersionedEntityModel.animateWalk(this, this.walkAnimation, limbAngle, limbDistance, 1.0F, 1.0F);

        this.head.yRot = headYaw * 0.017453292F;
        this.head.xRot = headPitch * 0.017453292F;
    }
}
