package com.dsd.tbb.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SpawningUtilities {

    private static final int MIN_DISTANCE = 20;
    private static final int MAX_DISTANCE = 100;

    private SpawningUtilities(){}

    public static List<BlockPos> getSafeSpawnPositions(Level level, int eHeight, BlockPos playerPos, int numPositions) {
        List<BlockPos> safePositions = new ArrayList<>();
        int retries = 0;
        int maxRetries = ConfigManager.getInstance().getTrialsConfig().getSpawnPositionRetry();

        while (safePositions.size() < numPositions && retries < maxRetries) {
            BlockPos randomPos = getRandomPos(playerPos);

            // Assume isSpawnSafe is a method to check if a position is safe for spawning
            if (isSpawnSafe(level, eHeight, randomPos)) {
                safePositions.add(randomPos);
            } else {
                retries++;
            }
        }

        return safePositions;
    }
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
        int spawnY = playerPos.getY() - (ConfigManager.getInstance().getTrialsConfig().getSpawnYsearchrange()/2);  // For now, spawn at player's y level
        int spawnZ = playerPos.getZ() + offsetZ;

        return new BlockPos(spawnX, spawnY, spawnZ);
    }

    private static boolean isSpawnSafe(Level level, int eHeight, BlockPos pos) {
        int YRange = ConfigManager.getInstance().getTrialsConfig().getSpawnYsearchrange();
        boolean isAirAbove = true;

        //Checking For Air blocks
        for (int YPos = pos.getY() - YRange; YPos <= pos.getY() + YRange; YPos += (eHeight + 1)) {
            BlockPos potentialPos = new BlockPos(pos.getX(),YPos,pos.getZ());

            for (int i = 0; i < eHeight; i++) {
                if (!level.isEmptyBlock(potentialPos.above(i))) {
                    isAirAbove = false;
                    break;
                }
            }

            if (isAirAbove && level.getBlockState(potentialPos.below()).getMaterial().isSolid()) {
                return true;  // Suitable position found
            }
        }

        return false;  // No suitable position found within the search range
    }

}
