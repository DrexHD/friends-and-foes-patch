package me.drex.fafpatch.impl.entity.model.entity;

import me.drex.fafpatch.impl.entity.model.emuvanilla.model.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.Villager;

public class VillagerModel extends EntityModel<Villager> {
    public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.5F);
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart hatRim;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart arms;

    public VillagerModel(ModelPart modelPart, boolean hatVisible) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.hat = this.head.getChild("hat");
        this.hatRim = this.hat.getChild("hat_rim");
        this.rightLeg = modelPart.getChild("right_leg");
        this.leftLeg = modelPart.getChild("left_leg");
        this.arms = modelPart.getChild("arms");

        this.head.visible = hatVisible;
        this.hat.visible = hatVisible;
        this.hatRim.visible = hatVisible;
    }

    public static MeshDefinition createBodyModel() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition partDefinition2 = partDefinition.addOrReplaceChild(
            "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.ZERO
        );
        PartDefinition partDefinition3 = partDefinition2.addOrReplaceChild(
            "hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.51F)), PartPose.ZERO
        );
        partDefinition3.addOrReplaceChild(
            "hat_rim", CubeListBuilder.create().texOffs(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F), PartPose.rotation((float) (-Math.PI / 2), 0.0F, 0.0F)
        );
        partDefinition2.addOrReplaceChild(
            "nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F)
        );
        PartDefinition partDefinition4 = partDefinition.addOrReplaceChild(
            "body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F), PartPose.ZERO
        );
        partDefinition4.addOrReplaceChild(
            "jacket", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.ZERO
        );
        partDefinition.addOrReplaceChild(
            "arms",
            CubeListBuilder.create()
                .texOffs(44, 22)
                .addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
                .texOffs(44, 22)
                .addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, true)
                .texOffs(40, 38)
                .addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
            PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F)
        );
        partDefinition.addOrReplaceChild(
            "right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-2.0F, 12.0F, 0.0F)
        );
        partDefinition.addOrReplaceChild(
            "left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(2.0F, 12.0F, 0.0F)
        );
        return meshDefinition;
    }

    public void setupAnim(Villager villager) {
        super.setupAnim(villager);
        this.head.yRot = Mth.wrapDegrees(villager.yHeadRot - villager.yBodyRot) * (float) (Math.PI / 180.0);
        this.head.xRot = villager.getXRot() * (float) (Math.PI / 180.0);
        if (villager.getUnhappyCounter() > 0) {
            this.head.zRot = 0.3F * Mth.sin(0.45F * villager.tickCount);
            this.head.xRot = 0.4F;
        } else {
            this.head.zRot = 0.0F;
        }

        this.rightLeg.xRot = Mth.cos(villager.walkAnimation.position(1) * 0.6662F) * 1.4F * villager.walkAnimation.speed(1) * 0.5F;
        this.leftLeg.xRot = Mth.cos(villager.walkAnimation.position(1) * 0.6662F + (float) Math.PI) * 1.4F * villager.walkAnimation.speed(1) * 0.5F;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.yRot = 0.0F;
    }

    public ModelPart getHead() {
        return this.head;
    }
}
