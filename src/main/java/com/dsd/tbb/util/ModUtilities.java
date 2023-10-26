package com.dsd.tbb.util;

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

}
