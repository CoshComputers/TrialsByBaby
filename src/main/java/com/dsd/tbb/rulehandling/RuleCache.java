package com.dsd.tbb.rulehandling;

import com.dsd.tbb.config.BabyZombieRules;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RuleCache {

    // The single instance of RuleCache
    private static final RuleCache INSTANCE = new RuleCache();

    // The actual cache data
    private final Map<RuleKey, List<ApplicableRule>> ruleCache = new ConcurrentHashMap<>();

    // Private constructor to prevent outside instantiation
    private RuleCache() {
    }

    // Method to get the single instance of RuleCache
    public static RuleCache getInstance() {
        return INSTANCE;
    }

    // Method to get the cache data
    public Map<RuleKey, List<ApplicableRule>> getCache() {
        return ruleCache;
    }

    public void clearCache(){
        ruleCache.clear();
    }
    public List<ApplicableRule> getEntry(RuleKey ruleKey) {
        return ruleCache.getOrDefault(ruleKey, Collections.emptyList());
    }

    public int getCacheSize() {
        return ruleCache.size();
    }

    public List<ApplicableRule> getApplicableRules(String dimension, int time) {
        List<ApplicableRule> applicableRules = new ArrayList<>();
        for (Map.Entry<RuleKey, List<ApplicableRule>> entry : ruleCache.entrySet()) {
            RuleKey key = entry.getKey();
            if (key.getDimension().equals(dimension) && time >= key.getStartTime() && time <= key.getEndTime()) {
                applicableRules.addAll(entry.getValue());
            }
        }
        return applicableRules;
    }


    // A method to populate the cache from BabyZombieRules.
    public void populateCache(BabyZombieRules babyZombieRules) {
        // Iterate through each dimension in BabyZombieRules
        for (Map.Entry<String, BabyZombieRules.DimensionRules> dimensionEntry : babyZombieRules.getRules().entrySet()) {
            String dimension = dimensionEntry.getKey();
            BabyZombieRules.DimensionRules dimensionRules = dimensionEntry.getValue();

            // Iterate through each time range in the dimension
            for (BabyZombieRules.TimeRange timeRange : dimensionRules.getTimeRanges()) {
                int startTime = timeRange.getStart();
                int endTime = timeRange.getEnd();

                // Iterate through each mob type in the time range
                for (Map.Entry<String, BabyZombieRules.MobType> typeEntry : timeRange.getTypes().entrySet()) {
                    String mobType = typeEntry.getKey();
                    BabyZombieRules.MobType mobTypeRules = typeEntry.getValue();

                    // Create a key for this rule
                    RuleKey ruleKey = new RuleKey(dimension, startTime, endTime);  // Assuming RuleKey has a suitable constructor

                    // Create an applicable rule object
                    ApplicableRule applicableRule = new ApplicableRule(mobType, mobTypeRules.getMinPackSize(), mobTypeRules.getMaxPackSize(), mobTypeRules.getRarity());

                    // Add the applicable rule to the cache
                    ruleCache.computeIfAbsent(ruleKey, k -> new ArrayList<>()).add(applicableRule);
                }
            }
        }
    }

    // Other methods to access and manage the cache...
    public static class RuleKey {
        private String dimension;
        private int startTime;
        private int endTime;

        public RuleKey(String dimension, int startTime, int endTime) {
            this.dimension = dimension;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public String getDimension() {
            return dimension;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        @Override
        public String toString() {
            return "RuleKey{" +
                    "dimension='" + dimension + '\'' +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    '}';
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RuleKey ruleKey = (RuleKey) o;
            return startTime == ruleKey.startTime && endTime == ruleKey.endTime && Objects.equals(dimension, ruleKey.dimension);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dimension, startTime, endTime);
        }
    }

    public static class ApplicableRule {
        private String mobType;
        private int minPackSize;
        private int maxPackSize;
        private double rarity;

        public ApplicableRule(String mobType, int minPackSize, int maxPackSize, double rarity) {
            this.mobType = mobType;
            this.minPackSize = minPackSize;
            this.maxPackSize = maxPackSize;
            this.rarity = rarity;
        }

        public String getMobType() {
            return mobType;
        }

        public int getMinPackSize() {
            return minPackSize;
        }

        public int getMaxPackSize() {
            return maxPackSize;
        }

        public double getRarity() {
            return rarity;
        }

        @Override
        public String toString() {
            return "ApplicableRule{" +
                    "mobType='" + mobType + '\'' +
                    ", minPackSize=" + minPackSize +
                    ", maxPackSize=" + maxPackSize +
                    ", rarity=" + rarity +
                    '}';
        }
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<RuleKey, List<ApplicableRule>> entry : ruleCache.entrySet()) {
            RuleKey key = entry.getKey();
            List<ApplicableRule> rules = entry.getValue();
            builder.append(key.toString()).append(": ").append(rules.toString()).append("\n");
        }
        return builder.toString();
    }

}
