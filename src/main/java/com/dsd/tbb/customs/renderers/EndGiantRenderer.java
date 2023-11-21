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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class EndGiantRenderer extends GeoEntityRenderer<EndGiant>{
    private String currentBoneRendering = null;
    private EndGiant curGiantRendering = null;
    private float colourchanger = 1.0f;
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
        TBBLogger.getInstance().debug("preRender","Called");
        super.preRender(poseStack,animatable,model,bufferSource,buffer,isReRender,partialTick,packedLight,packedOverlay,
                red,green,blue,alpha);
    }
    @Override
    public void render(EndGiant entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        TBBLogger.getInstance().debug("---------render---------","Called");
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
        super.renderCube(poseStack,cube,buffer,packedLight,packedOverlay,red,green,blue,alpha);

    }

    @Override
    public void postRender(PoseStack poseStack, EndGiant animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                            int packedOverlay, float red, float green, float blue, float alpha) {
        TBBLogger.getInstance().debug("postRender","Called");
        for(EndGiantPart part : animatable.getEGPParts()){
            TBBLogger.getInstance().debug("postRender",String.format("Bone %s", part.getPartName()));
            Optional<GeoBone> bone = model.getBone(part.getPartName());
            bone.ifPresent((b -> {
                TBBLogger.getInstance().debug("translating Quad Vectors","Model Position: " + ModelUtils.vectorToString(b.getWorldPosition()));

                TBBLogger.getInstance().debug("Matrix outputs","World Space Matrix: " + ModelUtils.matrixToString(b.getWorldSpaceMatrix()));

                for(GeoCube cube : b.getCubes()){
                    PoseStack newPoseStack = new PoseStack();
                    newPoseStack.pushPose();
                    newPoseStack.last().pose().set(b.getWorldSpaceMatrix());
                    TBBLogger.getInstance().debug("Matrix outputs","New Pose Pre Transform: " + ModelUtils.matrixToString(newPoseStack.last().pose()));

                    //Apply translation to the pivot point
                    //ModelUtils.translateToPivotPoint(newPoseStack, b);

                    //TBBLogger.getInstance().debug("Matrix outputs","New Pose Post Transform: " + ModelUtils.matrixToString(newPoseStack.last().pose()));
                    // Transform the cube's vertices using the new PoseStack
                    Vector3f[] verticesArray = ModelUtils.translateQuadVectors(cube, newPoseStack.last().pose());
                    ModelUtils.outputQuadVertices(verticesArray);
                    TBBLogger.getInstance().debug("Min Max For AABB", ModelUtils.minMaxVertString(verticesArray));

                    // No need to popPose here since it's a new PoseStack and will be discarded
                }

            }));

        }
    }

    private void drawBoundingBox(PoseStack poseStack, EndGiantPart part, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        float red = (1 * colourchanger) % 1.0f;
        float green = 0;
        float blue = (1 * colourchanger) % 1.0f;
        float alpha = 1;

        DebugRenderer.renderFilledBox(poseStack, bufferSource, part.getBoundingBox(), red,green,blue,alpha);

        colourchanger ++;
        poseStack.popPose();

    }

}