package me.drex.fafpatch.impl.entity.model.emuvanilla;

import me.drex.fafpatch.impl.entity.model.emuvanilla.model.ModelPart;
import org.joml.Matrix4f;

public interface CubeConsumer {
    void consume(ModelPart part, Matrix4f matrix4f, boolean hidden);
}