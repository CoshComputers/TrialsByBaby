package com.dsd.tbb.util;

import com.dsd.tbb.config.PlayerConfig;
import com.dsd.tbb.customs.entities.general.TrialsByBabyZombie;
import com.dsd.tbb.managers.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class SpawningUtilities {

    private static final int MIN_DISTANCE = 10;
    private static final int MAX_DISTANCE = 50;
    private static final int MIN_LIGHT_LEVEL = 7;

    private SpawningUtilities(){}

    public static int getNumberOfNearbyBabies(Level level, Player player) {
        // Determine the vertical search range
        int verticalSearchRange = ConfigManager.getInstance().getTrialsConfig().getSpawnYsearchrange();
        // Create the AABB
        AABB searchArea = new AABB(
                player.blockPosition().getX() - MAX_DISTANCE,
                player.blockPosition().getY() - verticalSearchRange / 2,
                player.blockPosition().getZ() - MAX_DISTANCE,
                player.blockPosition().getX() + MAX_DISTANCE,
                player.blockPosition().getY() + verticalSearchRange / 2,
                player.blockPosition().getZ() + MAX_DISTANCE
        );

        // Get all entities of type TrialsByBabyZombie within the AABB
        List<TrialsByBabyZombie> nearbyBabies = level.getEntitiesOfClass(
                TrialsByBabyZombie.class, searchArea, e -> true); // Use a predicate that always returns true to get all entities of this type

        // Return the count
        return nearbyBabies.size();
    }

    public static List<BlockPos> getSafeSpawnPositions(Level level, float eHeight, BlockPos playerPos, int numPositions, boolean checkLightLevel) {
        List<BlockPos> safePositions = new ArrayList<>();
        int retries = 0;
        int maxRetries = ConfigManager.getInstance().getTrialsConfig().getSpawnPositionRetry();
        while (safePositions.size() < numPositions && retries < maxRetries) {
            BlockPos.MutableBlockPos randomPos = getRandomPos(playerPos, level.getMaxBuildHeight());
            BlockPos safePos = findSafeSpawnLoc(level, eHeight, randomPos, checkLightLevel);
            // Assume isSpawnSafe is a method to check if a position is safe for spawning
            if ( safePos != null) {
                safePositions.add(safePos);
            } else {
                retries++;
            }
        }
       return safePositions;
    }
    public static BlockPos.MutableBlockPos getRandomPos(BlockPos playerPos,int max_build_height) {
        // Generate a random angle (in radians)
        double angle = ModUtilities.nextDouble() * 2 * Math.PI;

        // Generate a random distance within the range of 20 to 100 blocks
        int distance = ModUtilities.nextInt(MAX_DISTANCE - MIN_DISTANCE + 1) + MIN_DISTANCE;

        // Calculate the x and z offsets using trigonometry
        int offsetX = (int) (Math.cos(angle) * distance);
        int offsetZ = (int) (Math.sin(angle) * distance);
        int newYPos = playerPos.getY();
        if(newYPos > max_build_height){
            newYPos = max_build_height;
        }

        // Calculate the spawn coordinates
        int spawnX = playerPos.getX() + offsetX;
        int spawnY = newYPos;  // For now, spawn at player's y level
        int spawnZ = playerPos.getZ() + offsetZ;

        return new BlockPos.MutableBlockPos(spawnX, spawnY, spawnZ);
    }

    private static BlockPos findSafeSpawnLoc(Level level, float eHeight, BlockPos.MutableBlockPos pos,boolean checkLightLevel) {
        int YRange = ConfigManager.getInstance().getTrialsConfig().getSpawnYsearchrange();
        int lightLevel = 0;
        BlockPos.MutableBlockPos tempPos = pos;
        boolean isAirAbove;
        //Checking For Air blocks
        for (int YPos = pos.getY() - YRange; YPos <= tempPos.getY() + YRange; YPos ++) {
            isAirAbove = true;
            pos.setY(YPos);
            //TBBLogger.getInstance().bulkLog("findSafeSpawnLoc[2]",String.format("Scanning Y Pos [%d]",pos.getY()));
            for (int i = 0; i < eHeight; i++) {
                if (!level.isEmptyBlock(pos.above(i))) {
                    isAirAbove = false;
                    break;
                }
            }
            if(!isAirAbove) continue; //can skip the rest, as we don't have clear air above the current block.
            boolean isSolidBelow = level.getBlockState(pos.below()).getMaterial().isSolid();
            if (isAirAbove && isSolidBelow) {
                pos.setY(YPos);
                //TBBLogger.getInstance().bulkLog("findSafeSpawnLoc[2]",String.format("SAFE AT Y Pos [%d]",pos.getY()));
                if(checkLightLevel) lightLevel = level.getRawBrightness(pos,level.getSkyDarken());
                if(lightLevel <= MIN_LIGHT_LEVEL) return pos.immutable();  // Suitable position found
            }
            if(YPos > level.getMaxBuildHeight()){
               //TBBLogger.getInstance().error("findSafeSpawnLoc","Y Value has exceeded the limit for some reason - no safe spawn found");
               break;
            }
        }
        //TBBLogger.getInstance().debug("findSafeSpawn[4]","No Safe Spawns found, returning null");
        return null;  // No suitable position found within the search range
    }

    public static int getPackSize(int minPackSize, int maxPackSize) {
        //If min and max are the same, no need for a random number
        if(minPackSize == maxPackSize){
            return minPackSize;
        }else { //otherwise pick a random number between the two.
            return ModUtilities.nextInt(minPackSize,maxPackSize);
        }
    }

    public static boolean shouldSpawnGiant(PlayerConfig playerConfig) {
        // Determine if conditions are met to spawn a giant
        return playerConfig.numberOfNearbyGiants() < ConfigManager.getInstance().getTrialsConfig().getSpawnGiants()
               && ModUtilities.nextDouble() < ConfigManager.getInstance().getGiantConfig().getSpawnFrequency();
    }

    public static int shouldSpawnBaby(Level level, Player player, double rarity){
        boolean shouldSpawn = false;
        int mobCountThreshold = ConfigManager.getInstance().getTrialsConfig().getMobCountThreshold();
       // TBBLogger.getInstance().debug("shouldSpawnBaby",String.format("Current Threshold = [%d]",mobCountThreshold));
        int nearbyEntityCount = SpawningUtilities.getNumberOfNearbyBabies(level, player);
        //TBBLogger.getInstance().debug("shouldSpawnBaby",String.format("Near player = [%d]",nearbyEntityCount));

        //Check if we are within the mobCountThreshold around the player
        if (nearbyEntityCount < mobCountThreshold) {
            double randSpawnCheck = ModUtilities.nextDouble();
            if(randSpawnCheck < rarity){
                return mobCountThreshold - nearbyEntityCount;
            }
        }

        return 0;
    }
}
