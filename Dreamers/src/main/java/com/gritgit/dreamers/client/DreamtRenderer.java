package com.gritgit.dreamers.client;

import com.gritgit.dreamers.Dreamers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumerProvider;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import com.gritgit.dreamers.entity.DreamtEntity;

public class DreamtRenderer extends EntityRenderer<DreamtEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Dreamers.MODID, "images/man.png");

    public DreamtRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(DreamtEntity entity, float yaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        stack.pushPose();
        // position and face camera
        stack.translate(0.0D, 1.0D, 0.0D);
        float scale = 0.5f;
        stack.scale(scale, scale, scale);
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - this.entityRenderDispatcher.camera.getYRot()));
        stack.mulPose(Vector3f.XP.rotationDegrees(-this.entityRenderDispatcher.camera.getXRot()));

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        // draw a simple quad
        consumer.vertex(stack.last().pose(), -0.5F, 0.0F, 0.0F).uv(0.0F, 1.0F).uv2(packedLight).overlayCoords(OverlayTexture.NO_OVERLAY).endVertex();
        consumer.vertex(stack.last().pose(), 0.5F, 0.0F, 0.0F).uv(1.0F, 1.0F).uv2(packedLight).overlayCoords(OverlayTexture.NO_OVERLAY).endVertex();
        consumer.vertex(stack.last().pose(), 0.5F, 1.0F, 0.0F).uv(1.0F, 0.0F).uv2(packedLight).overlayCoords(OverlayTexture.NO_OVERLAY).endVertex();
        consumer.vertex(stack.last().pose(), -0.5F, 1.0F, 0.0F).uv(0.0F, 0.0F).uv2(packedLight).overlayCoords(OverlayTexture.NO_OVERLAY).endVertex();

        stack.popPose();
        super.render(entity, yaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(DreamtEntity entity) {
        return TEXTURE;
    }
}
