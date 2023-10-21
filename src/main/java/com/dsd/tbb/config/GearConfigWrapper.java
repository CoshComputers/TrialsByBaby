package com.dsd.tbb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

public class GearConfigWrapper {
    private GearConfig initialGear;

    public GearConfig getInitialGear() {
        return initialGear;
    }

    public void setInitialGear(GearConfig initialGear) {
        this.initialGear = initialGear;
    }
    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public class GearConfig {
        private List<GearItem> gearItems;

        public List<GearItem> getGearItems() {
            return gearItems;
        }

        public void setGearItems(List<GearItem> gearItems) {
            this.gearItems = gearItems;
        }


    }

    public static class GearItem {
        private String item;
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
    }
}
