package com.dsd.tbb.customs.renderers;

import com.dsd.tbb.customs.entities.endgiant.EndGiant;
import com.dsd.tbb.customs.entities.endgiant.EndGiantPart;
import com.dsd.tbb.customs.models.EndGiantModel;
import com.dsd.tbb.customs.models.ModelUtils;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.TBBLogger;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class EndGiantRenderer extends GeoEntityRenderer<EndGiant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TrialsByBaby.MOD_ID, "textures/endgiant.png");

    public EndGiantRenderer(EntityRendererProvider.Context context) {
        super(context, new EndGiantModel());
    }

    @Override
    public ResourceLocation getTextureLocation(EndGiant entity) {
        return TEXTURE;
    }


    @Override
    public void render(EndGiant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        setPartBB(entity);
        drawBoundingBox(poseStack,bufferSource,entity.getPart(5),partialTicks);
        // Draw bounding boxes for each part
    }



    public void setPartBB(EndGiant entity) {

        for (EndGiantPart part : entity.getEGPParts()) {
            model.getBone(part.getPartName()).ifPresent(geoBoneObject -> {
                if (geoBoneObject instanceof GeoBone) {
                    GeoBone geoBone = geoBoneObject;
                    Vector3d locPosition = geoBoneObject.getWorldPosition();
                    AABB partBB = calculateBoundingBox(geoBone);
                    part.setBoundingBox(partBB);
                    TBBLogger.getInstance().debug(String.format("setPartBB [%s]",part.getPartName()),
                            String.format("World Position [%f][%f][%f] | Min Pos [%f][%f][%f] | Max Pos [%f][%f][%f] ",
                                    locPosition.x,locPosition.y,locPosition.z,
                                    partBB.minX,partBB.minY,partBB.minZ,partBB.maxX,partBB.maxY,partBB.maxZ));
                }
            });
        }
    }

    private AABB calculateBoundingBox(GeoBone bone) {
        StringBuilder sa = new StringBuilder();
        Vector3d[] worldVertices = new Vector3d[8];
        Matrix4f worldTransformationMatrix = bone.getWorldSpaceMatrix();
        Vector3d locPosition = bone.getWorldPosition();
        TBBLogger.getInstance().debug("Calc BB","Part Name " + bone.getName());
        for(GeoCube cube : bone.getCubes()){
            Vector3f[] cubeVertices = ModelUtils.getCubeVertices(cube);
            worldVertices = new Vector3d[cubeVertices.length];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cubeVertices.length; i++) {
                Vector4f localVertex = new Vector4f(cubeVertices[i].x, cubeVertices[i].y, cubeVertices[i].z, 1);
                Vector4f worldVertex = worldTransformationMatrix.transform(localVertex);
                worldVertices[i] = new Vector3d(worldVertex.x, worldVertex.y, worldVertex.z);
                sb.append("[").append(i).append("] ").append(worldVertices[i].x).append(",").append(worldVertices[i].y);
                sb.append(",").append(worldVertices[i].x).append("\n");
            }
            //TBBLogger.getInstance().debug("calc BB",sb.toString());
        }



        return ModelUtils.createAABBFromVertices(worldVertices);
    }

    private Vec3 applyTransformations(Vec3 point, Vec3 position, Vec3 rotation) {
        // Apply rotation (conversion to a rotation matrix or quaternion may be necessary)
        // Then apply position
        // Note: This is a simplified placeholder. Actual implementation depends on rotation handling.
        return point.add(position);
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