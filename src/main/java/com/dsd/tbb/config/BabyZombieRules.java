package com.dsd.tbb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BabyZombieRules {

    private static volatile BabyZombieRules INSTANCE = null;
    @SerializedName("rules")
    private Map<String, DimensionRules> rules;

    private BabyZombieRules() {
        setDefaults();
    }

    public static BabyZombieRules getInstance() {
        if (INSTANCE == null) {
            synchronized (BabyZombieRules.class) {
                INSTANCE = new BabyZombieRules();
            }
        }
        return INSTANCE;
    }

    public void setDefaults() {
        this.rules = new HashMap<>();

        // Overworld Rules
        DimensionRules overworldRules = new DimensionRules();
        TimeRange overworldTimeRange = new TimeRange(13000, 23500);
        MobType overworldRegular = new MobType(1, 1, 1.0);
        overworldTimeRange.getTypes().put("REGULAR", overworldRegular);
        MobType overworldEnderman = new MobType(1, 1, 0.1);
        overworldTimeRange.getTypes().put("ENDERMAN", overworldEnderman);
        overworldRules.getTimeRanges().add(overworldTimeRange);

        // Nether Rules
        DimensionRules netherRules = new DimensionRules();
        TimeRange netherTimeRange = new TimeRange(0, 24000);
        MobType netherBlaze = new MobType(1, 1, 1.0);
        netherTimeRange.getTypes().put("BLAZE", netherBlaze);
        netherRules.getTimeRanges().add(netherTimeRange);

        // End Rules
        DimensionRules endRules = new DimensionRules();
        TimeRange endTimeRange = new TimeRange(0, 24000);
        MobType endEnderman = new MobType(1, 1, 1.0);
        endTimeRange.getTypes().put("ENDERMAN", endEnderman);
        endRules.getTimeRanges().add(endTimeRange);

        // Populate Rules Map
        rules.put("overworld", overworldRules);
        rules.put("nether", netherRules);
        rules.put("end", endRules);
    }

    public Map<String, DimensionRules> getRules() {
        return rules;
    }

    public void setRules(Map<String, DimensionRules> rules) {
        this.rules = rules;
    }

    public static class DimensionRules {
        @SerializedName("timeRanges")
        private List<TimeRange> timeRanges = new ArrayList<>();

        public List<TimeRange> getTimeRanges() {
            return timeRanges;
        }

        public void setTimeRanges(List<TimeRange> timeRanges) {
            this.timeRanges = timeRanges;
        }

        @Override
        public String toString() {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
    }

    public static class TimeRange {
        private int start;
        private int end;
        @SerializedName("types")
        private Map<String, MobType> types = new HashMap<>();

        public TimeRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public Map<String, MobType> getTypes() {
            return types;
        }

        public void setTypes(Map<String, MobType> types) {
            this.types = types;
        }
    }

    public static class MobType {
        @SerializedName("minPackSize")
        private int minPackSize;
        @SerializedName("maxPackSize")
        private int maxPackSize;
        private double rarity;

        public MobType(int minPackSize, int maxPackSize, double rarity) {
            this.minPackSize = minPackSize;
            this.maxPackSize = maxPackSize;
            this.rarity = rarity;
        }

        public int getMinPackSize() {
            return minPackSize;
        }

        public void setMinPackSize(int minPackSize) {
            this.minPackSize = minPackSize;
        }

        public int getMaxPackSize() {
            return maxPackSize;
        }

        public void setMaxPackSize(int maxPackSize) {
            this.maxPackSize = maxPackSize;
        }

        public double getRarity() {
            return rarity;
        }

        public void setRarity(double rarity) {
            this.rarity = rarity;
        }
    }
}
