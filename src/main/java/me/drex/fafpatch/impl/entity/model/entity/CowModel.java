package me.drex.fafpatch.impl.entity.model.entity;

import me.drex.fafpatch.impl.entity.model.BabyModelTransform;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.*;
import net.minecraft.world.entity.animal.AbstractCow;

import java.util.Set;

public class CowModel<T extends AbstractCow> extends QuadrupedModel<T> {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(false, 8.0F, 6.0F, Set.of("head"));
    private static final int LEG_SIZE = 12;

    public CowModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = createBaseCowModel();
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    static MeshDefinition createBaseCowModel() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F)
                .texOffs(1, 33)
                .addBox(-3.0F, 1.0F, -7.0F, 6.0F, 3.0F, 1.0F)
                .texOffs(22, 0)
                .addBox("right_horn", -5.0F, -5.0F, -5.0F, 1.0F, 3.0F, 1.0F)
                .texOffs(22, 0)
                .addBox("left_horn", 4.0F, -5.0F, -5.0F, 1.0F, 3.0F, 1.0F),
            PartPose.offset(0.0F, 4.0F, -8.0F)
        );
        partDefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(18, 4).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F).texOffs(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F),
            PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );
        CubeListBuilder cubeListBuilder = CubeListBuilder.create().mirror().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        CubeListBuilder cubeListBuilder2 = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        partDefinition.addOrReplaceChild("right_hind_leg", cubeListBuilder2, PartPose.offset(-4.0F, 12.0F, 7.0F));
        partDefinition.addOrReplaceChild("left_hind_leg", cubeListBuilder, PartPose.offset(4.0F, 12.0F, 7.0F));
        partDefinition.addOrReplaceChild("right_front_leg", cubeListBuilder2, PartPose.offset(-4.0F, 12.0F, -5.0F));
        partDefinition.addOrReplaceChild("left_front_leg", cubeListBuilder, PartPose.offset(4.0F, 12.0F, -5.0F));
        return meshDefinition;
    }

    public ModelPart getHead() {
        return this.head;
    }
}
