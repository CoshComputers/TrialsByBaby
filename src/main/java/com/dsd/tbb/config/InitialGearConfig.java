package com.dsd.tbb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InitialGearConfig {
    @SerializedName("initialGear")
    private List<GearItem> initialGear;
    public InitialGearConfig() {
        initialGear = new ArrayList<>();
    }
    public List<GearItem> getInitialGear() {
        return initialGear;
    }

    public void setInitialGear(List<GearItem> initialGear) {
        this.initialGear = initialGear;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static class GearItem {
        @SerializedName("item")
        private String item;
        @SerializedName("enchantments")
        private Map<String, Integer> enchantments;

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public Map<String, Integer> getEnchantments() {
            return enchantments;
        }

        public void setEnchantments(Map<String, Integer> enchantments) {
            this.enchantments = enchantments;
        }

        @Override
        public String toString() {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
    }
}
