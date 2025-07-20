package me.drex.fafpatch.impl.entity;

import com.faboslav.friendsandfoes.common.entity.GlareEntity;
import me.drex.fafpatch.impl.FriendsAndFoesPatch;
import me.drex.fafpatch.impl.entity.model.EntityModels;
import me.drex.fafpatch.impl.entity.model.GlareEntityModel;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

public class GlareElementHolder extends VanillishElementHolder<GlareEntity, GlareEntityModel> {

    public static final ResourceLocation FLOWERING_LAYER = FriendsAndFoesPatch.id("flowering_layer");

    public GlareElementHolder(GlareEntity entity) {
        super(entity);
        addConditionalLayer(RenderState::new, MAIN_LAYER, renderState -> EntityModels.GLARE.get(renderState.regular()));
        addConditionalLayer(RenderState::new, FLOWERING_LAYER, renderState -> {
            if (renderState.flowering()) {
                return EntityModels.GLARE.get(renderState);
            }
            return null;
        });
    }

    private static boolean shouldRenderFlowers(GlareEntity entity) {
        String string = ChatFormatting.stripFormatting(entity.getName().getString());

        return "Anna".equals(string)
            || entity.isTame();
    }

    public record RenderState(boolean flowering, boolean baby) {
        public RenderState(GlareEntity entity) {
            this(shouldRenderFlowers(entity), entity.isBaby());
        }

        public RenderState regular() {
            return new RenderState(false, baby);
        }
    }
}
