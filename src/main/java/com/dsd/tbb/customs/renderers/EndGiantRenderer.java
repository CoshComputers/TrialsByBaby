package com.dsd.tbb.customs.renderers;

import com.dsd.tbb.customs.entities.endgiant.EndGiant;
import com.dsd.tbb.customs.entities.endgiant.EndGiantPart;
import com.dsd.tbb.customs.models.EndGiantModel;
import com.dsd.tbb.customs.models.ModelUtils;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.TBBLogger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class EndGiantRenderer extends GeoEntityRenderer<EndGiant>{
    private String currentBoneRendering;
    private EndGiant curGiantRendering;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TrialsByBaby.MOD_ID, "textures/endgiant.png");

    public EndGiantRenderer(EntityRendererProvider.Context context) {
        super(context, new EndGiantModel());
    }

    @Override
    public ResourceLocation getTextureLocation(EndGiant entity) {
        return TEXTURE;
    }

    @Override
    public void preRender(PoseStack poseStack, EndGiant animatable, BakedGeoModel model, MultiBufferSource bufferSource,
                          VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue,float alpha) {
        TBBLogger.getInstance().debug("*********preRender","Called");
        super.preRender(poseStack,animatable,model,bufferSource,buffer,isReRender,partialTick,packedLight,packedOverlay,
                red,green,blue,alpha);

    }
    @Override
    public void render(EndGiant entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        TBBLogger.getInstance().debug("---------render","Called");
        super.render(entity,entityYaw,partialTick,poseStack,bufferSource,packedLight);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, EndGiant animatable, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        TBBLogger.getInstance().debug("actuallyRender","Called");
            super.actuallyRender(poseStack,animatable,model,renderType,bufferSource,buffer,isReRender,partialTick,packedLight,
                                packedOverlay,red,green,blue,alpha);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, EndGiant animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource,
                                  VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, float red, float green, float blue, float alpha) {
        TBBLogger.getInstance().debug("renderRecursively","Bone Active = " + bone.getName());
        this.curGiantRendering = animatable;
        super.renderRecursively(poseStack,animatable,bone,renderType,bufferSource,buffer,isReRender,partialTick,packedLight,
                                packedOverlay,red,green,blue,alpha);
    }

    @Override
    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight,
                                  int packedOverlay, float red, float green, float blue, float alpha) {
        TBBLogger.getInstance().debug("renderCubeOfBones","Bone Active = " + bone.getName());
        this.currentBoneRendering = bone.getName();
        super.renderCubesOfBone(poseStack,bone,buffer,packedLight,packedOverlay,red,green,blue,alpha);
    }

    @Override
    public void renderChildBones(PoseStack poseStack, EndGiant animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource,
                                 VertexConsumer buffer,boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                 float red, float green, float blue, float alpha) {
        TBBLogger.getInstance().debug("renderChildBones","Bone Active = " + bone.getName());
        super.renderChildBones(poseStack,animatable,bone,renderType,bufferSource,buffer,isReRender,partialTick,packedLight,
                packedOverlay,red,green,blue,alpha);
    }
    @Override
    public void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight,
                    int packedOverlay, float red, float green, float blue, float alpha) {
        TBBLogger.getInstance().debug("renderCube","Called");
        // Apply transformations to the cube
        RenderUtils.translateToPivotPoint(poseStack, cube);
        RenderUtils.rotateMatrixAroundCube(poseStack, cube);
        RenderUtils.translateAwayFromPivotPoint(poseStack, cube);
        // Get the current transformation matrix
        Matrix4f poseState = poseStack.last().pose();

        for (GeoQuad quad : cube.quads()) {
            if (quad == null) continue;

            Vector3f normal = poseStack.last().normal().transform(new Vector3f(quad.normal()));
            RenderUtils.fixInvertedFlatCube(cube, normal);

            // Create vertices and capture their transformed positions
            Vector3f[] quadVerts = ModelUtils.createAndCaptureVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            AABB boundingBox = ModelUtils.createAABBFromVertices(quadVerts);
            for(EndGiantPart part :  this.curGiantRendering.getEGPParts()){
                if(Objects.equals(part.getPartName(), this.currentBoneRendering)){
                    part.setBoundingBox(boundingBox);
                }
            }

        }
    }


    public void setPartBB(EndGiant entity) {

    }






    private void drawBoundingBox(PoseStack poseStack, MultiBufferSource bufferSource, EndGiantPart part, float partialTicks) {


        DebugRenderer.renderFilledBox(poseStack, bufferSource, part.getBoundingBox(), 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();

    }

}