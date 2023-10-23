package com.dsd.tbb.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class SpawningUtilities {

    private static final int MIN_DISTANCE = 20;
    private static final int MAX_DISTANCE = 100;

    private SpawningUtilities(){}

    public static BlockPos getRandomPos(BlockPos playerPos) {
        // Generate a random angle (in radians)
        double angle = ModUtilities.nextDouble() * 2 * Math.PI;

        // Generate a random distance within the range of 20 to 100 blocks
        int distance = ModUtilities.nextInt(MAX_DISTANCE - MIN_DISTANCE + 1) + MIN_DISTANCE;

        // Calculate the x and z offsets using trigonometry
        int offsetX = (int) (Math.cos(angle) * distance);
        int offsetZ = (int) (Math.sin(angle) * distance);

        // Calculate the spawn coordinates
        int spawnX = playerPos.getX() + offsetX;
        int spawnY = playerPos.getY();  // For now, spawn at player's y level
        int spawnZ = playerPos.getZ() + offsetZ;

        return new BlockPos(spawnX, spawnY, spawnZ);
    }

    public static List<BlockPos> getSafeSpawnPositions(Entity entity, BlockPos playerPos, int numPositions) {
        List<BlockPos> safePositions = new ArrayList<>();
        int retries = 0;
        int maxRetries = ConfigManager.getInstance().getTrialsConfig().getSpawnPositionRetry();

        while (safePositions.size() < numPositions && retries < maxRetries) {
            BlockPos randomPos = getRandomPos(playerPos);

            // Assume isSpawnSafe is a method to check if a position is safe for spawning
            if (isSpawnSafe(entity, randomPos)) {
                safePositions.add(randomPos);
            } else {
                retries++;
            }
        }

        return safePositions;
    }

    private static boolean isSpawnSafe(Entity entity, BlockPos pos) {
        // Implement the logic to check if the position is safe for spawning
        return true;  // Placeholder return value
    }

}
