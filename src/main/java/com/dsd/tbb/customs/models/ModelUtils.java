package com.dsd.tbb.customs.models;

import com.dsd.tbb.util.TBBLogger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;

import java.text.NumberFormat;
import java.util.ArrayList;

public class ModelUtils {


    public static Vector3f[] createAndCaptureVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer,
                                        int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        TBBLogger.getInstance().debug("createAndCapture","Called");
        ArrayList<Vector3f> transformedVertices = new ArrayList<>();
        for (GeoVertex vertex : quad.vertices()) {
            Vector3f position = vertex.position();
            Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));
            transformedVertices.add(new Vector3f(vector4f.x(), vector4f.y(), vector4f.z()));
            buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.texU(),
                    vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
        }

        return transformedVertices.toArray(new Vector3f[0]);
    }


    public static AABB createAABBFromVertices(Vector3f[] vertices) {
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

        for (Vector3f vertex : vertices) {
            minX = Math.min(minX, vertex.x());
            minY = Math.min(minY, vertex.y());
            minZ = Math.min(minZ, vertex.z());
            maxX = Math.max(maxX, vertex.x());
            maxY = Math.max(maxY, vertex.y());
            maxZ = Math.max(maxZ, vertex.z());
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }



    public static void outputQuadVertices(Vector3f[] quadVertices){
        StringBuilder sa = new StringBuilder();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        sa.append("\n******Quad Vertices********\n");

        for(int j=0; j< quadVertices.length; j++){
            sa.append("\t[").append(j).append("] ").append(quadVertices[j].x).append(",");
            sa.append(quadVertices[j].y).append(",").append(quadVertices[j].z).append("\n");
        }
        sa.append("----------------------------\n");

        TBBLogger.getInstance().debug("BONE INFO",sa.toString());
    }
    public static void outputPoseStackState(PoseStack poseStack) {
        if (!poseStack.clear()) {
            Matrix4f currentMatrix = new Matrix4f(poseStack.last().pose());
            // Convert the matrix to a readable string format
            String matrixString = matrixToString(currentMatrix);
            // Output the matrix (you can replace this with your logging mechanism)
            TBBLogger.getInstance().debug ("Output Pose Stack","Current PoseStack State: \n" + matrixString);
        } else {
            TBBLogger.getInstance().debug ("Output Pose Stack","PoseStack is empty.");
        }
    }

    private static String matrixToString(Matrix4f matrix) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        // Create a string representation of the matrix
        // Format the string as needed for readability
        return matrix.toString(nf);
    }

}
