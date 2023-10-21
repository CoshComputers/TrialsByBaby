package com.dsd.tbb.config;

import com.dsd.tbb.util.EnumTypes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BabyZombieRules {

    private static  BabyZombieRules INSTANCE = null;
    @SerializedName("babyZombieRules")
    private static List<BabyZombieRule> babyZombieRules;


    private BabyZombieRules(){
        setDefaults();
    }

    public static BabyZombieRules getInstance(){
        if (INSTANCE == null){
            synchronized (BabyZombieRules.class){
                INSTANCE = new BabyZombieRules();
            }
        }
        return INSTANCE;
    }

    public static void setDefaults() {
        babyZombieRules = new ArrayList<>();

        // Default rule for Blaze Baby Zombie
        BabyZombieRule blazeRule = new BabyZombieRule();
        blazeRule.setMobType(EnumTypes.ZombieAppearance.BLAZE);
        blazeRule.setConditions(new Conditions("nether", "night", new PlayerProximity(0, 50), new PackSize(2, 10)));
        babyZombieRules.add(blazeRule);

        // Default rule for Regular Baby Zombie
        BabyZombieRule regularRule = new BabyZombieRule();
        regularRule.setMobType(EnumTypes.ZombieAppearance.REGULAR);
        regularRule.setConditions(new Conditions("overworld", "night", new PlayerProximity(0, 50), new PackSize(2, 10)));
        babyZombieRules.add(regularRule);

        // Default rule for Ender Baby Zombie
        BabyZombieRule enderRule = new BabyZombieRule();
        enderRule.setMobType(EnumTypes.ZombieAppearance.ENDERMAN);
        enderRule.setConditions(new Conditions("end", "night", new PlayerProximity(0, 50), new PackSize(2, 10)));

        AdditionalCondition overworldCondition = new AdditionalCondition("overworld", 0.1);
        AdditionalCondition netherCondition = new AdditionalCondition("nether", 0.1);
        enderRule.setAdditionalConditions(Arrays.asList(overworldCondition, netherCondition));

        babyZombieRules.add(enderRule);
    }


    public List<BabyZombieRule> getBabyZombieRules() {
        return babyZombieRules;
    }

    public void setBabyZombieRules(List<BabyZombieRule> babyZombieRules) {
        this.babyZombieRules = babyZombieRules;
    }

    public static class BabyZombieRule {
        @SerializedName("mob_type")
        private EnumTypes.ZombieAppearance mobType;
        private Conditions conditions;
        @SerializedName("additional_conditions")
        private List<AdditionalCondition> additionalConditions;

        @Override
        public String toString() {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
        public EnumTypes.ZombieAppearance getMobType() {
            return mobType;
        }

        public void setMobType(EnumTypes.ZombieAppearance mobType) {
            this.mobType = mobType;
        }

        public Conditions getConditions() {
            return conditions;
        }

        public void setConditions(Conditions conditions) {
            this.conditions = conditions;
        }

        public List<AdditionalCondition> getAdditionalConditions() {
            return additionalConditions;
        }

        public void setAdditionalConditions(List<AdditionalCondition> additionalConditions) {
            this.additionalConditions = additionalConditions;
        }
    }

    public static class Conditions {
        private String dimension;
        private String time;
        @SerializedName("player_proximity")
        private PlayerProximity playerProximity;
        @SerializedName("pack_size")
        private PackSize packSize;

        public Conditions(String dimension, String time, PlayerProximity playerProximity, PackSize packSize) {
            this.dimension = dimension;
            this.time = time;
            this.playerProximity = playerProximity;
            this.packSize = packSize;
        }

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public PlayerProximity getPlayerProximity() {
            return playerProximity;
        }

        public void setPlayerProximity(PlayerProximity playerProximity) {
            this.playerProximity = playerProximity;
        }

        public PackSize getPackSize() {
            return packSize;
        }

        public void setPackSize(PackSize packSize) {
            this.packSize = packSize;
        }

    }

    public static class AdditionalCondition {
        private String dimension;
        private double rarity;

        public AdditionalCondition(String dimension, double rarity) {
            this.dimension = dimension;
            this.rarity = rarity;
        }

        // getters and setters...

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }

        public double getRarity() {
            return rarity;
        }

        public void setRarity(double rarity) {
            this.rarity = rarity;
        }
    }

    public static class PlayerProximity {
        @SerializedName("min_distance")
        private int minDistance;
        @SerializedName("max_distance")
        private int maxDistance;

        public PlayerProximity(int minDistance, int maxDistance) {
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
        }

        // getters and setters...

        public int getMinDistance() {
            return minDistance;
        }

        public void setMinDistance(int minDistance) {
            this.minDistance = minDistance;
        }

        public int getMaxDistance() {
            return maxDistance;
        }

        public void setMaxDistance(int maxDistance) {
            this.maxDistance = maxDistance;
        }
    }

    public static class PackSize {
        private int min;
        private int max;

        public PackSize(int min, int max) {
            this.min = min;
            this.max = max;
        }

        // getters and setters...

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }



}
