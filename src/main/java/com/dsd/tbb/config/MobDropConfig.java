package com.dsd.tbb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MobDropConfig {

    @SerializedName("mobDropConfig")
    private List<AppearanceConfig> mobDropConfig;

    public List<AppearanceConfig> getMobDropConfig() {
        return mobDropConfig;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static class AppearanceConfig {
        @SerializedName("appearance")
        private String appearance;

        @SerializedName("drops")
        private List<DropConfig> drops;

        public synchronized String getAppearance() {
            return appearance;
        }

        public synchronized List<DropConfig> getDrops() {
            return drops;
        }

        @Override
        public String toString() {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
    }

    public static class DropConfig {
        @SerializedName("item")
        private String item;

        @SerializedName("min")
        private int min;

        @SerializedName("max")
        private int max;

        @SerializedName("rarity")
        private float rarity;

        public synchronized String getItem() {
            return item;
        }

        public synchronized int getMin() {
            return min;
        }

        public synchronized int getMax() {
            return max;
        }

        public synchronized float getRarity() {
            return rarity;
        }

        @Override
        public String toString() {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
    }
}
