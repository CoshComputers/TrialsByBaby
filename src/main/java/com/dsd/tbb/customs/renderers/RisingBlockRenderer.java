package com.dsd.tbb.customs.renderers;

import com.dsd.tbb.customs.entities.general.RisingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class RisingBlockRenderer extends EntityRenderer<RisingBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public RisingBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(RisingBlockEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        BlockState blockState = entity.getMimickedBlockState();
        if (blockState != null) {
            poseStack.pushPose(); // Save the current transformation matrix

            // Translate to the entity's position
            BlockPos pos = entity.blockPosition();
            poseStack.translate(pos.getX() - entity.getX(), pos.getY() - entity.getY(), pos.getZ() - entity.getZ());

            // Render the block
            blockRenderer.renderSingleBlock(blockState, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);

            poseStack.popPose(); // Restore the previous transformation matrix
        }
    }

    @Override
    public ResourceLocation getTextureLocation(RisingBlockEntity entity) {
        // You may return a dummy texture, as the actual texture will be determined by the block state
        return new ResourceLocation("minecraft", "textures/block/stone.png");
    }
}
