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

import java.text.NumberFormat;
import java.util.ArrayList;

public class ModelUtils {


    public static Vector3f[] createVerticesForAABB(GeoQuad quad, Matrix4f translationMatrix) {
        TBBLogger.getInstance().debug("createVerticesForAABB","Called");
        ArrayList<Vector3f> transformedVertices = new ArrayList<>();
        for (GeoVertex vertex : quad.vertices()) {
            Vector3f position = vertex.position();
            Vector4f vector4f = translationMatrix.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));
            transformedVertices.add(new Vector3f(vector4f.x(), vector4f.y(), vector4f.z()));
        }

        return transformedVertices.toArray(new Vector3f[0]);
    }

    public static Vector3f[] translateQuadVectors(GeoCube cube, Matrix4f transformationMatrix){
        ArrayList<Vector3f> transformedVertices = new ArrayList<>();
        float scaleFactor = transformationMatrix.m33();
        for (GeoQuad quad : cube.quads()) {
            for (int i = 0; i < quad.vertices().length; i++) {
                GeoVertex vertex = quad.vertices()[i];
                Vector4f localVertex = new Vector4f(vertex.position().x(), vertex.position().y(), vertex.position().z(), 1.0f);
                Vector4f worldVertex = transformationMatrix.transform(localVertex);
                transformedVertices.add(new Vector3f(worldVertex.x()/scaleFactor, worldVertex.y()/scaleFactor, worldVertex.z()/scaleFactor));
            }
            // Now worldSpaceVertices contains the transformed vertices
            // You can check if they fall within the expected range
        }
        return transformedVertices.toArray(new Vector3f[0]);
    }

    public static String minMaxVertString(Vector3f[] vertices) {
        if (vertices == null || vertices.length == 0) {
            // Handle the empty or null array case
            // For example, return a default AABB or throw an exception
            return "Vertices Empty";
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

        StringBuilder sb = new StringBuilder();
        sb.append("\n---------- MIN and MAX Quad Vertices for AABB--------------------");
        sb.append("\nMIN Vert = ")
                .append(String.format("%.2f", minX)).append(",")
                .append(String.format("%.2f", minY)).append(",")
                .append(String.format("%.2f", minZ));

        sb.append("\nMAX Vert = ")
                .append(String.format("%.2f", maxX)).append(",")
                .append(String.format("%.2f", maxY)).append(",")
                .append(String.format("%.2f", maxZ));

        sb.append("\n-----------------------------------------------------------------");
        return sb.toString();
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

    public static void translateToPivotPoint(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(bone.getWorldPosition().x, bone.getWorldPosition().y, bone.getWorldPosition().z);
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
    public static void outputQuadVertices(Vector3f[] quadVertices, String direction){
        StringBuilder sa = new StringBuilder();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        sa.append("\n******Quad Vertices********\n");
        sa.append("Direction: ").append(direction).append("\n");
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

    public static String matrixToString(Matrix4f matrix) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        // Create a string representation of the matrix
        // Format the string as needed for readability
        return matrix.toString(nf);
    }
    public static String vectorToString(Vector3d vector) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        // Create a string representation of the matrix
        // Format the string as needed for readability
        return vector.toString(nf);
    }

}
