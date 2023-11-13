package com.dsd.tbb.util;

import com.dsd.tbb.customs.entities.TrialsByBabyZombie;
import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.main.TrialsByBaby;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.concurrent.ThreadLocalRandom;

public class ModUtilities {

    // Prevent instantiation
    private ModUtilities() { }

    /**
     * Generates a random integer between 0 (inclusive) and the specified value (exclusive).
     *
     * @param bound the upper bound (exclusive)
     * @return a random integer between 0 (inclusive) and bound (exclusive)
     */
    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    /**
     * Generates a random integer between the specified lower bound (inclusive) and the specified upper bound (exclusive).
     *
     * @param lowerBound the lower bound (inclusive)
     * @param upperBound the upper bound (exclusive)
     * @return a random integer between lowerBound (inclusive) and upperBound (exclusive)
     */
    public static int nextInt(int lowerBound, int upperBound) {
        return ThreadLocalRandom.current().nextInt(lowerBound, upperBound);
    }

    public static double nextDouble(){
        return ThreadLocalRandom.current().nextDouble();
    }

    public static int getValidEnchantmentLevel(Enchantment enchantment, int level) {
        int maxLevel = enchantment.getMaxLevel();
        StringBuilder sb = new StringBuilder();

        if (level > maxLevel) {
            sb.append("Enchantment Level [").append(level).append("] ");
            sb.append("for ").append(enchantment.toString());
            sb.append("Setting level to Max Level [").append(maxLevel).append("]");

           TBBLogger.getInstance().warn("getValidEnchantmentLevel",sb.toString());
            return maxLevel;
        }
        return level;
    }

    public static int countEntities(ServerLevel serverLevel) {
        int count = 0;
        for (Entity entity : serverLevel.getAllEntities()) {
            count++;
        }
        return count;
    }
    public static int countGiants(ServerLevel serverLevel) {
        int count = 0;
        for (Entity entity : serverLevel.getAllEntities()) {
            if(entity instanceof TrialsByGiantZombie) count++;
        }
        return count;
    }
    public static int countBabies(ServerLevel serverLevel) {
        int count = 0;
        for (Entity entity : serverLevel.getAllEntities()) {
            if(entity instanceof TrialsByBabyZombie) count++;
        }
        return count;
    }

    public static void writeGiantLogs(){
        MinecraftServer server = TrialsByBaby.MOD_SERVER;

        for(ServerLevel serverLevel : server.getAllLevels()) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof TrialsByGiantZombie) {
                    //TBBLogger.getInstance().debug("Writing Giant Event Logs", "Writing for Giant " +
                     //       ((TrialsByGiantZombie) entity).getMyName());
                    ((TrialsByGiantZombie) entity).getEventLog().writeToFile();
                    ;
                }
            }
        }

    }
}
