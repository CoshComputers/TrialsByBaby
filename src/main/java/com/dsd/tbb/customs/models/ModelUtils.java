package com.dsd.tbb.customs.models;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoCube;

public class ModelUtils {

    /*public static Vector3f[] getCubeVertices(GeoCube cube) {
        Vec3 size = cube.size();
        Vec3 pivot = cube.pivot();
        double inflate = cube.inflate();

        // Define the scale factor (1 block = 16 pixels)
        float scaleFactor = 1.0f / 16.0f;

        // Apply the scale factor to size, pivot, and inflate
        float halfWidth = (float)(size.x() / 2 + inflate) * scaleFactor;
        float halfHeight = (float)(size.y() / 2 + inflate) * scaleFactor;
        float halfDepth = (float)(size.z() / 2 + inflate) * scaleFactor;
        Vector3f pivotScaled = new Vector3f((float)pivot.x() * scaleFactor, (float)pivot.y() * scaleFactor, (float)pivot.z() * scaleFactor);

        Vector3f[] vertices = new Vector3f[8];
        // Calculate each vertex by adding/subtracting half dimensions to/from the scaled pivot
        // Front vertices
        vertices[0] = new Vector3f(pivotScaled.x - halfWidth, pivotScaled.y + halfHeight, pivotScaled.z - halfDepth); // Front Top Left
        vertices[1] = new Vector3f(pivotScaled.x + halfWidth, pivotScaled.y + halfHeight, pivotScaled.z - halfDepth); // Front Top Right
        vertices[2] = new Vector3f(pivotScaled.x - halfWidth, pivotScaled.y - halfHeight, pivotScaled.z - halfDepth); // Front Bottom Left
        vertices[3] = new Vector3f(pivotScaled.x + halfWidth, pivotScaled.y - halfHeight, pivotScaled.z - halfDepth); // Front Bottom Right

        // Back vertices
        vertices[4] = new Vector3f(pivotScaled.x - halfWidth, pivotScaled.y + halfHeight, pivotScaled.z + halfDepth); // Back Top Left
        vertices[5] = new Vector3f(pivotScaled.x + halfWidth, pivotScaled.y + halfHeight, pivotScaled.z + halfDepth); // Back Top Right
        vertices[6] = new Vector3f(pivotScaled.x - halfWidth, pivotScaled.y - halfHeight, pivotScaled.z + halfDepth); // Back Bottom Left
        vertices[7] = new Vector3f(pivotScaled.x + halfWidth, pivotScaled.y - halfHeight, pivotScaled.z + halfDepth); // Back Bottom Right

        return vertices;
    }*/


    public static Vector3f[] getCubeVertices(GeoCube cube) {
        Vec3 size = cube.size();
        Vec3 pivot = cube.pivot();
        double inflate = cube.inflate();

        float halfWidth = (float)(size.x() / 2 + inflate);
        float halfHeight = (float)(size.y() / 2 + inflate);
        float halfDepth = (float)(size.z() / 2 + inflate);

        Vector3f[] vertices = new Vector3f[8];
        // Front vertices
        vertices[0] = new Vector3f((float)pivot.x() - halfWidth, (float)pivot.y() + halfHeight, (float)pivot.z() - halfDepth); // Front Top Left
        vertices[1] = new Vector3f((float)pivot.x() + halfWidth, (float)pivot.y() + halfHeight, (float)pivot.z() - halfDepth); // Front Top Right
        vertices[2] = new Vector3f((float)pivot.x() - halfWidth, (float)pivot.y() - halfHeight, (float)pivot.z() - halfDepth); // Front Bottom Left
        vertices[3] = new Vector3f((float)pivot.x() + halfWidth, (float)pivot.y() - halfHeight, (float)pivot.z() - halfDepth); // Front Bottom Right

        // Back vertices
        vertices[4] = new Vector3f((float)pivot.x() - halfWidth, (float)pivot.y() + halfHeight, (float)pivot.z() + halfDepth); // Back Top Left
        vertices[5] = new Vector3f((float)pivot.x() + halfWidth, (float)pivot.y() + halfHeight, (float)pivot.z() + halfDepth); // Back Top Right
        vertices[6] = new Vector3f((float)pivot.x() - halfWidth, (float)pivot.y() - halfHeight, (float)pivot.z() + halfDepth); // Back Bottom Left
        vertices[7] = new Vector3f((float)pivot.x() + halfWidth, (float)pivot.y() - halfHeight, (float)pivot.z() + halfDepth); // Back Bottom Right


        return vertices;
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

}
