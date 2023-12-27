package com.selfdot.pixilcraftnpcs.mixin;

import com.selfdot.pixilcraftnpcs.client.NPCVisibilityTracker;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntityRenderer.class)
public abstract class MobEntityRendererMixin extends EntityRenderer<MobEntity> {

    protected MobEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void injectRender(
        MobEntity entity,
        float entityYaw,
        float partialTicks,
        MatrixStack poseMatrix,
        VertexConsumerProvider buffer,
        int packedLight,
        CallbackInfo info
    ) {
        if (NPCVisibilityTracker.getInstance().notVisible(entity.getUuid())) {
            info.cancel();
        }
    }

}
