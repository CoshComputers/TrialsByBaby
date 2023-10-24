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
            BlockPos.MutableBlockPos randomPos = getRandomPos(playerPos);
            BlockPos safePos = isSpawnSafe(level, eHeight, randomPos);
            // Assume isSpawnSafe is a method to check if a position is safe for spawning
            if ( safePos != null) {
                safePositions.add(safePos);
            } else {
                retries++;
            }
        }
        TBBLogger.getInstance().bulkLog("getSafeSpawnPositions",String.format("Safe Positions being returned with SIZE [%d]",safePositions.size()));
        return safePositions;
    }
    public static BlockPos.MutableBlockPos getRandomPos(BlockPos playerPos) {
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

        return new BlockPos.MutableBlockPos(spawnX, spawnY, spawnZ);
    }

    private static BlockPos isSpawnSafe(Level level, int eHeight, BlockPos.MutableBlockPos pos) {
        int YRange = ConfigManager.getInstance().getTrialsConfig().getSpawnYsearchrange();
        boolean isAirAbove = false;
        //BlockPos.MutableBlockPos potentialPos = new BlockPos.MutableBlockPos(pos.getX(),pos.getY(),pos.getZ());
        //Checking For Air blocks
        for (int YPos = pos.getY() - YRange; YPos <= pos.getY() + YRange; YPos += (eHeight + 1)) {
            pos.setY(YPos);
            for (int i = 0; i < eHeight; i++) {
                if (!level.isEmptyBlock(pos.above(i))) {
                    isAirAbove = true;
                    break;
                }
            }
           if (isAirAbove && level.getBlockState(pos.below()).getMaterial().isSolid()) {
                TBBLogger.getInstance().bulkLog("isSpawnSafe",String.format("Found Safe Block at X[%d] Y[%d] Z[%d]",
                        pos.getX(),pos.getY(), pos.getZ()));
                return pos.immutable();  // Suitable position found
            }
        }
        TBBLogger.getInstance().bulkLog("isSpawnSafe",String.format("No Safe Block - Finished checks at X[%d] Y[%d] Z[%d]",
                pos.getX(),pos.getY(), pos.getZ()));
        return null;  // No suitable position found within the search range
    }

}
