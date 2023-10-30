package com.dsd.tbb.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class SpawningUtilities {

    private static final int MIN_DISTANCE = 10;
    private static final int MAX_DISTANCE = 50;

    private SpawningUtilities(){}

    public static int getNumberOfNearbyEntities(Level level, Player player){

        double minX = player.blockPosition().getX();
        double minY = player.blockPosition().getY() - (double)ConfigManager.getInstance().getTrialsConfig().getSpawnYsearchrange() / 2;
        double minZ = player.blockPosition().getZ();
        double maxX = player.blockPosition().getX() + MAX_DISTANCE / 2;
        double maxY = player.blockPosition().getY() + 5;
        double maxZ = player.blockPosition().getZ() + MAX_DISTANCE / 2;

        List<LivingEntity> nearbyEntities = level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT,player,
                new AABB(minX,minY,minZ,maxX,maxY,maxZ));
        return nearbyEntities.size();

    }
    public static List<BlockPos> getSafeSpawnPositions(Level level, int eHeight, BlockPos playerPos, int numPositions) {
        List<BlockPos> safePositions = new ArrayList<>();
        int retries = 0;
        int maxRetries = ConfigManager.getInstance().getTrialsConfig().getSpawnPositionRetry();
        while (safePositions.size() < numPositions && retries < maxRetries) {
            BlockPos.MutableBlockPos randomPos = getRandomPos(playerPos);
            BlockPos safePos = findSafeSpawnLoc(level, eHeight, randomPos);
            // Assume isSpawnSafe is a method to check if a position is safe for spawning
            if ( safePos != null) {
                safePositions.add(safePos);
            } else {
                retries++;
            }
        }
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
        int newYPos = playerPos.getY();
        if(newYPos > 200){
            newYPos = 200;
        }

        // Calculate the spawn coordinates
        int spawnX = playerPos.getX() + offsetX;
        int spawnY = newYPos;  // For now, spawn at player's y level
        int spawnZ = playerPos.getZ() + offsetZ;

        return new BlockPos.MutableBlockPos(spawnX, spawnY, spawnZ);
    }

    private static BlockPos findSafeSpawnLoc(Level level, int eHeight, BlockPos.MutableBlockPos pos) {
        int YRange = ConfigManager.getInstance().getTrialsConfig().getSpawnYsearchrange();
        BlockPos.MutableBlockPos tempPos = pos;
        boolean isAirAbove = false;
        //TBBLogger.getInstance().bulkLog("findSafeSpawnLoc[1]",String.format("Before Loops Y Position [%d] and YRange [%d]",
        //        pos.getY(), YRange));
        //Checking For Air blocks
        for (int YPos = pos.getY() - YRange; YPos <= tempPos.getY() + YRange; YPos += (eHeight)) {
            pos.setY(YPos);
            //TBBLogger.getInstance().bulkLog("findSafeSpawnLoc[2]",String.format("Y Pos [%d]",pos.getY()));
            for (int i = 0; i < eHeight; i++) {
                if (level.isEmptyBlock(pos.above(i))) {
                    isAirAbove = true;
                    break;
                }
            }
            boolean isSolidBelow = level.getBlockState(pos.below()).getMaterial().isSolid();
            //TBBLogger.getInstance().bulkLog("findSafeSpawn[3]",String.format("Air & Solid check Y value [%d], isAirAbove [%s] and isSolid below [%s]",
            //        YPos, isAirAbove? "TRUE":"FALSE", isSolidBelow? "TRUE":"FALSE"));
           if (isAirAbove && isSolidBelow) {
                pos.setY(YPos);
                return pos.immutable();  // Suitable position found
            }
           if(YPos > 200){
               //TBBLogger.getInstance().error("findSafeSpawnLoc","Y Value has exceeded the limit for some reason - no safe spawn found");
               break;
           }
        }
        //TBBLogger.getInstance().debug("findSafeSpawn[4]","No Safe Spawns found, returning null");
        return null;  // No suitable position found within the search range
    }

    public static int getPackSize(int minPackSize, int maxPackSize) {
        return ModUtilities.nextInt(minPackSize,maxPackSize);
    }
}
