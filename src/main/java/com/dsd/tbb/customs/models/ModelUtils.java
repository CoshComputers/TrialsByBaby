package com.dsd.tbb.customs.models;

import com.dsd.tbb.util.TBBLogger;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.util.RenderUtils;

import java.text.NumberFormat;
import java.util.ArrayList;

public class ModelUtils {


    public static void transformPoseStackForBone(PoseStack bonePoseStack, GeoBone bone){
        RenderUtils.translateMatrixToBone(bonePoseStack,bone);
        RenderUtils.translateToPivotPoint(bonePoseStack,bone);
        RenderUtils.rotateMatrixAroundBone(bonePoseStack,bone);
        RenderUtils.scaleMatrixForBone(bonePoseStack,bone);
        bonePoseStack.popPose();
    }

    public static Vector3f[] transformCubeVertices(PoseStack poseStack, GeoBone bone) {
        ArrayList<Vector3f> transformedVertices = new ArrayList<>();

        Matrix4f poseState = new Matrix4f(poseStack.last().pose());
        for (GeoCube cube : bone.getCubes()) {
            for(GeoQuad quad : cube.quads()) {
                for (GeoVertex vertex : quad.vertices()) {
                    Vector3f position = vertex.position();
                    Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));
                    transformedVertices.add(new Vector3f(vector4f.x(), vector4f.y(), vector4f.z()));
                }
            }
        }
        return transformedVertices.toArray(new Vector3f[0]);
    }


    public static PoseStack copyPoseStack(PoseStack original) {
        PoseStack newStack = new PoseStack();

        // This assumes you're interested in copying the current (top) transformation state
        if (!original.clear()) {
            // Get the current top matrix from the original stack
            Matrix4f currentMatrix = original.last().pose();

            // Apply a copy of this matrix to the new stack
            newStack.pushPose();
            newStack.last().pose().set(currentMatrix);
        }

        return newStack;
    }

    public static AABB createAABBFromVertices(Vector3d[] vertices) {
        if (vertices == null || vertices.length == 0) {
            // Handle the empty or null array case
            // For example, return a default AABB or throw an exception
            return new AABB(0, 0, 0, 0, 0, 0); // Returning a default AABB
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;

        for (Vector3d vertex : vertices) {
            minX = Math.min(minX, vertex.x());
            minY = Math.min(minY, vertex.y());
            minZ = Math.min(minZ, vertex.z());
            maxX = Math.max(maxX, vertex.x());
            maxY = Math.max(maxY, vertex.y());
            maxZ = Math.max(maxZ, vertex.z());
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }



    public static void outputQuadVertices(GeoBone bone, Vector3f[] quadVertices){
        StringBuilder sa = new StringBuilder();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        sa.append("\nBone ").append(bone.getName()).append(" Quad Vertices:\n");

        for(int j=0; j< quadVertices.length; j++){
            sa.append("\t[").append(j).append("] ").append(quadVertices[j].x).append(",");
            sa.append(quadVertices[j].y).append(",").append(quadVertices[j].z).append("\n");
        }
        TBBLogger.getInstance().debug("BONE INFO",sa.toString());
    }

    /*public static void outputBoneInfo(PoseStack poseStack, GeoBone bone){
        StringBuilder sa = new StringBuilder();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        sa.append("Bone ").append(bone.getName()).append(" |\n");
        sa.append("World Transform\n").append(bone.getWorldSpaceMatrix().toString(nf)).append("\n");
        sa.append("Local Space Matrix\n").append(bone.getLocalSpaceMatrix().toString(nf)).append("\n");
        sa.append("World Space Norm Matrix\n").append(bone.getWorldSpaceNormal().toString(nf)).append("\n");

        sa.append("Position Vector = ").append(bone.getPositionVector().toString(nf)).append("\n");
        sa.append("Rotation Vector = ").append(bone.getRotationVector().toString(nf)).append("\n");
        sa.append("Scale Vector = ").append(bone.getScaleVector().toString(nf)).append("\n");

        sa.append("Local Position = ").append(bone.getLocalPosition().toString(nf)).append("\n");
        sa.append("Relative to World Position =").append(bone.getWorldPosition().toString(nf)).append("\n");
        sa.append("Relative to Model Position =").append(bone.getModelPosition().toString(nf)).append("\n");

        sa.append("Cube Information\n");
        int i = 0;
        for(GeoCube cube : bone.getCubes()){
            Vector3f[] tQuadVertices = transformCubeVertices(poseStack,cube);
            sa.append("\t[").append(i).append("] Inflate = ").append(cube.inflate()).append("\n");
            sa.append("\t[").append(i).append("] Size = ").append(cube.size().toString()).append("\n");
            sa.append("\t[").append(i).append("] Pivots = ").append(cube.pivot().toString()).append("\n");
            sa.append("\tGeo Quad Vertices\n");
            int j = 0;
            for(GeoQuad quad : cube.quads()){
                int k = 0;
                sa.append("\t\t[").append(j).append("] Direction - ").append(quad.direction().toString()).append("\n");
                for(GeoVertex vertex : quad.vertices()){
                    sa.append("\t\t\t[").append(k).append("] ").append(vertex.position().toString(nf)).append("\n");
                    k++;
                }
                j++;
            }
            sa.append("\tQUAD Transformed Vertices\n");
            for(j=0; j< tQuadVertices.length; j++){
                sa.append("\t\t\t[").append(j).append("] ").append(tQuadVertices[j].x).append(",");
                sa.append(tQuadVertices[j].y).append(",").append(tQuadVertices[j].z).append("\n");
            }
            i++;
        }


        TBBLogger.getInstance().debug("BONE INFO",sa.toString());

    }*/

}
