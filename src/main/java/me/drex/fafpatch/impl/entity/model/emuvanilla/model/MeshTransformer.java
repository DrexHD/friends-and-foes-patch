package me.drex.fafpatch.impl.entity.model.emuvanilla.model;

@FunctionalInterface
public interface MeshTransformer {
    MeshTransformer IDENTITY = meshDefinition -> meshDefinition;

    static MeshTransformer scaling(float f) {
        float g = 24.016F * (1.0F - f);
        return meshDefinition -> meshDefinition.transformed(partPose -> partPose.scaled(f).translated(0.0F, g, 0.0F));
    }

    MeshDefinition apply(MeshDefinition meshDefinition);
}
