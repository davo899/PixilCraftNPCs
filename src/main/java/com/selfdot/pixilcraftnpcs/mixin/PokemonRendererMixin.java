package com.selfdot.pixilcraftnpcs.mixin;

import com.selfdot.pixilcraftnpcs.client.NPCVisibilityTracker;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonRenderer.class)
public class PokemonRendererMixin {

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void injectRenderLabel(
        PokemonEntity entity,
        Text text,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo info
    ) {
        if (NPCVisibilityTracker.getInstance().notVisible(entity.getUuid())) {
            info.cancel();
        }
    }

}
