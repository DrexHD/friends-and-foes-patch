package me.drex.fafpatch.impl.entity;

import com.mojang.math.Axis;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import me.drex.fafpatch.impl.FriendsAndFoesPatch;
import me.drex.fafpatch.impl.entity.model.EntityModelHelper;
import me.drex.fafpatch.impl.entity.model.emuvanilla.PolyModelInstance;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.EntityModel;
import me.drex.fafpatch.impl.entity.model.emuvanilla.model.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.*;
import java.util.function.Function;

public class SimpleElementHolder<T extends Entity, X extends EntityModel<T>> extends ElementHolder {

    public static final ResourceLocation MAIN_LAYER = FriendsAndFoesPatch.id("main_layer");
    static final Matrix4fStack STACK = new Matrix4fStack(64);
    private final Map<PolyModelInstance<X>, Map<ModelPart, ItemDisplayElement>> elements = new IdentityHashMap<>();
    protected final T entity;
    public final InteractionElement interaction;
    public final LeadAttachmentElement leadAttachment = new LeadAttachmentElement();

    final Map<ResourceLocation, PolyModelInstance<X>> layerModels = new HashMap<>();
    final List<ConditionalLayer<?>> conditionalLayers = new ArrayList<>();
    private boolean hurt = false;

    private boolean noTick = true;

    public SimpleElementHolder(T entity) {
        this.entity = entity;
        var interaction = VirtualElement.InteractionHandler.redirect(entity);
        this.interaction = new InteractionElement(interaction);
        this.interaction.setSendPositionUpdates(false);
        this.leadAttachment.setOffset(new Vec3(0, entity.getBbHeight() / 2, 0));
        this.leadAttachment.setInteractionHandler(interaction);
        this.addElement(leadAttachment);
        this.addPassengerElement(this.interaction);
    }

    public <L> void addConditionalLayer(Function<T, L> stateSupplier, ResourceLocation layer, Function<L, PolyModelInstance<X>> modelSupplier) {
        conditionalLayers.add(new ConditionalLayer<>(stateSupplier, layer, modelSupplier));
    }

    public void setMainModel(PolyModelInstance<X> main) {
        setLayer(MAIN_LAYER, main);
    }

    public PolyModelInstance<X> getMainModel() {
        return layerModels.get(MAIN_LAYER);
    }

    public void setLayer(ResourceLocation id, PolyModelInstance<X> layer) {
        if (layerModels.get(id) == layer) {
            return;
        }
        replaceModel(layerModels.get(id), layer);
        if (layer != null) {
            layerModels.put(id, layer);
        } else {
            layerModels.remove(id);
        }
        if (!noTick) {
            this.tick();
        }
    }

    private void replaceModel(PolyModelInstance<X> oldModel, PolyModelInstance<X> newModel) {
        Map<ModelPart, ItemDisplayElement> oldElements;
        if (oldModel == null) {
            oldElements = new IdentityHashMap<>();
        } else {
            oldElements = new IdentityHashMap<>(this.elements.get(oldModel));
        }
        var newElements = new IdentityHashMap<ModelPart, ItemDisplayElement>();
        this.elements.remove(oldModel);

        if (newModel != null) {
            for (var part : newModel.model().allParts()) {
                var stack = newModel.modelParts().apply(part);
                if (stack != null) {
                    var element = oldElements.get(part);
                    if (element == null) {
                        element = EntityModelHelper.createItemDisplay(stack);
                    } else {
                        element.setItem(stack);
                        oldElements.remove(part);
                    }
                    EntityModelHelper.updateDisplayElement(element, this.entity);
                    newElements.put(part, element);
                    this.addElement(element);
                }
            }
            this.elements.put(newModel, newElements);
        }

        for (var old : oldElements.values()) {
            this.removeElement(old);
        }
    }

    private static float getYaw(Direction direction) {
        return switch (direction) {
            case SOUTH -> 90.0F;
            case WEST -> 0.0F;
            case NORTH -> 270.0F;
            case EAST -> 180.0F;
            default -> 0.0F;
        };
    }

    @Override
    public boolean startWatching(ServerGamePacketListenerImpl player) {
        if (noTick) {
            onTick();
        }
        return super.startWatching(player);
    }

    @Override
    protected void onTick() {
        noTick = false;
        this.interaction.setSize(entity.getBbWidth(), entity.getBbHeight());
        this.interaction.setCustomName(this.entity.getCustomName());
        this.interaction.setCustomNameVisible(this.entity.isCustomNameVisible());

        conditionalLayers.forEach(ConditionalLayer::tick);

        for (var layerModel : layerModels.values()) {
            renderModel(layerModel);
        }
        if (entity instanceof LivingEntity livingEntity) {
            this.hurt = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
        }

        super.onTick();
    }

    private void renderModel(PolyModelInstance<X> model) {
        STACK.pushMatrix();
        // ensure the element doesn't clip into nearby blocks
        EntityDimensions dimensions = entity.getDimensions(entity.getPose());
        STACK.translate(0.0F, -dimensions.height() / 2, 0.0F);
        if (entity instanceof LivingEntity livingEntity) {
            var hurt = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
            if (this.hurt != hurt) {

                var map = hurt ? model.damagedModelParts() : model.modelParts();

                for (var entry : elements.get(model).entrySet()) {
                    entry.getValue().setItem(map.apply(entry.getKey()));
                }
            }

            if (entity.hasPose(Pose.SLEEPING)) {
                Direction direction = livingEntity.getBedOrientation();
                if (direction != null) {
                    float f = livingEntity.getEyeHeight() - 0.1F;
                    STACK.translate((float) (-direction.getStepX()) * f, 0.0F, (float) (-direction.getStepZ()) * f);
                }
            }

            float g = livingEntity.getScale();
            STACK.scale(g);
            this.setupTransforms(livingEntity, STACK, livingEntity.getVisualRotationYInDegrees(), g);
            STACK.scale(-1.0F, -1.0F, 1.0F);

            STACK.scale(getEntityScale());
            STACK.translate(0.0F, -1.501F, 0.0F);
        }

        model.model().setupAnim(this.entity);
        model.model().renderServerSide(STACK, (part, matrix4f, hidden) -> updateElement(model, part, matrix4f, hidden));
        renderServerSide(STACK);

        STACK.popMatrix();
    }

    protected void renderServerSide(Matrix4fStack stack) {
    }

    public float getEntityScale() {
        return 1;
    }

    private void updateElement(PolyModelInstance<X> model, ModelPart part, Matrix4f matrix4f, boolean hidden) {
        var element = this.elements.get(model).get(part);
        if (element == null) {
            return;
        }

        if (hidden) {
            this.removeElement(element);
        } else {
            element.setTransformation(matrix4f);
            EntityModelHelper.updateDisplayElement(element, this.entity);
            element.startInterpolationIfDirty();
            this.addElement(element);
        }
    }

    protected void setupTransforms(LivingEntity entity, Matrix4fStack matrices, float bodyYaw, float baseHeight) {
        if (entity.isFullyFrozen()) {
            bodyYaw += (float) (Math.cos((float) Mth.floor(entity.tickCount) * 3.25F) * Math.PI * 0.4000000059604645);
        }

        if (!entity.hasPose(Pose.SLEEPING)) {
            matrices.rotate(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        }

        if (entity.deathTime > 0.0F) {
            float f = (entity.deathTime - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrices.rotate(Axis.ZP.rotationDegrees(f * 90));
        } else if (entity.isAutoSpinAttack()) {
            matrices.rotate(Axis.XP.rotationDegrees(-90.0F - entity.getXRot()));
            matrices.rotate(Axis.YP.rotationDegrees(entity.tickCount * -75.0F));
        } else if (entity.hasPose(Pose.SLEEPING)) {
            Direction direction = entity.getBedOrientation();
            float g = direction != null ? getYaw(direction) : bodyYaw;
            matrices.rotate(Axis.YP.rotationDegrees(g));
            matrices.rotate(Axis.ZP.rotationDegrees(90));
            matrices.rotate(Axis.YP.rotationDegrees(270.0F));
        } else {
            var name = entity.getDisplayName().getString();
            if ("Dinnerbone".equals(name) || "Grumm".equals(name)) {
                matrices.translate(0.0F, (entity.getBbHeight() + 0.1F) / baseHeight, 0.0F);
                matrices.rotate(Axis.ZP.rotationDegrees(180.0F));
            }
        }
    }

    private class ConditionalLayer<S> {
        private final Function<T, S> stateSupplier;
        private final ResourceLocation layer;
        private final Function<S, PolyModelInstance<X>> modelSupplier;
        S previous;

        private ConditionalLayer(Function<T, S> stateSupplier, ResourceLocation layer, Function<S, PolyModelInstance<X>> modelSupplier) {
            this.stateSupplier = stateSupplier;
            this.layer = layer;
            this.modelSupplier = modelSupplier;
        }

        void tick() {
            S current = stateSupplier.apply(entity);
            if (!Objects.equals(current, previous)) {
                // update
                setLayer(layer, modelSupplier.apply(current));
            }

            previous = current;
        }

    }
}
