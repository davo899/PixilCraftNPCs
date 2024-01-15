package com.selfdot.pixilcraftnpcs.client;

import com.selfdot.pixilcraftnpcs.npc.HumanNPCEntity;
import com.selfdot.pixilcraftnpcs.npc.SkinType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HumanNPCRenderer extends MobEntityRenderer<HumanNPCEntity, EntityModel<HumanNPCEntity>> {

    public HumanNPCRenderer(EntityRendererFactory.Context context, SkinType skinType) {
        super(context, switch (skinType) {
            case CLASSIC -> new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false);
            case SLIM -> new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER_SLIM), false);
        }, 1f);
    }

    @Override
    public Identifier getTexture(HumanNPCEntity entity) {
        return HumanNPCTextureTracker.getInstance().getTexture(entity.getUuid());
    }

    @Override
    public void render(
        HumanNPCEntity mobEntity,
        float f,
        float g,
        MatrixStack matrixStack,
        VertexConsumerProvider vertexConsumerProvider,
        int i
    ) {
        shadowRadius = 0;
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

}
