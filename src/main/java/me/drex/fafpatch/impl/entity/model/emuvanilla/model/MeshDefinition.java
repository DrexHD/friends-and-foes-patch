package me.drex.fafpatch.impl.entity.model.emuvanilla.model;

import com.google.common.collect.ImmutableList;

import java.util.function.UnaryOperator;

public class MeshDefinition {
    private final PartDefinition root;

    public MeshDefinition() {
        this(new PartDefinition(ImmutableList.of(), PartPose.ZERO));
    }

    private MeshDefinition(PartDefinition partDefinition) {
        this.root = partDefinition;
    }

    public PartDefinition getRoot() {
        return this.root;
    }

    public MeshDefinition transformed(UnaryOperator<PartPose> unaryOperator) {
        return new MeshDefinition(this.root.transformed(unaryOperator));
    }

    public MeshDefinition apply(MeshTransformer meshTransformer) {
        return meshTransformer.apply(this);
    }
}
