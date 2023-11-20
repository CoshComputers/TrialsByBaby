package com.dsd.tbb.customs.renderers;

import com.dsd.tbb.customs.entities.endgiant.EndGiant;
import com.dsd.tbb.customs.entities.endgiant.EndGiantPart;
import com.dsd.tbb.customs.models.EndGiantModel;
import com.dsd.tbb.customs.models.ModelUtils;
import com.dsd.tbb.main.TrialsByBaby;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class EndGiantRenderer extends GeoEntityRenderer<EndGiant>{

    private static final ResourceLocation TEXTURE = new ResourceLocation(TrialsByBaby.MOD_ID, "textures/endgiant.png");

    public EndGiantRenderer(EntityRendererProvider.Context context) {
        super(context, new EndGiantModel());
    }

    @Override
    public ResourceLocation getTextureLocation(EndGiant entity) {
        return TEXTURE;
    }

    @Override
    public void preRender(PoseStack poseStack, EndGiant animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
                           float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();

        for (EndGiantPart part : animatable.getEGPParts()) {
            GeoBone bone = model.getBone(part.getPartName()).orElse(null);
            if (bone != null) {
                // Create a new copy of the pose stack for each bone
                PoseStack bonePoseStack = ModelUtils.copyPoseStack(poseStack);

                // Apply transformations specific to this bone
                ModelUtils.transformPoseStackForBone(bonePoseStack, bone);

                // Transform vertices and output them
                Vector3f[] transformedVertices = ModelUtils.transformCubeVertices(bonePoseStack, bone);
                ModelUtils.outputQuadVertices(bone, transformedVertices);
            }
        }

        poseStack.popPose();  // Ensure to pop the pose stack after processing all bones

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

    }

    public void setPartBB(EndGiant entity) {

    }

    private AABB calculateBoundingBox(GeoBone bone) {


        return new AABB(0,0,0,0,0,0);
    }

    private Vec3 applyTransformations(Vec3 point, Vec3 position, Vec3 rotation) {

        return new Vec3(0,0,0);
    }




    private void drawBoundingBox(PoseStack poseStack, MultiBufferSource bufferSource, EndGiantPart part, float partialTicks) {
        // Get the part's position relative to the entity
        Vec3 partPosition = part.position();

        poseStack.pushPose();
        poseStack.translate(partPosition.x(), partPosition.y(), partPosition.z());

        DebugRenderer.renderFilledBox(poseStack, bufferSource, part.getBoundingBox(), 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();

    }

}