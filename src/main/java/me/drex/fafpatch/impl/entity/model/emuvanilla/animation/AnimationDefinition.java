package me.drex.fafpatch.impl.entity.model.emuvanilla.animation;

import com.google.common.collect.Maps;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.ModelPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations) {
    public KeyframeAnimation bake(ModelPart modelPart) {
        return KeyframeAnimation.bake(modelPart, this);
    }

    public static class Builder {
        private final float length;
        private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
        private boolean looping;

        public static Builder withLength(float f) {
            return new Builder(f);
        }

        private Builder(float f) {
            this.length = f;
        }

        public Builder looping() {
            this.looping = true;
            return this;
        }

        public Builder addAnimation(String string, AnimationChannel animationChannel) {
            this.animationByBone.computeIfAbsent(string, (stringx) -> new ArrayList<>()).add(animationChannel);
            return this;
        }

        public AnimationDefinition build() {
            return new AnimationDefinition(this.length, this.looping, this.animationByBone);
        }
    }
}