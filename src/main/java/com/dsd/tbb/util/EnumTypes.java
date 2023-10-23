package com.dsd.tbb.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumTypes {

    public enum CustomMobTypes{
        BABYZOMBIE,
        GIANTZOMBIE;
    }

    public enum ZombieAppearance{
        BLAZE,
        ENDERMAN,
        REGULAR;
    }
   public enum ModConfigOption {
        OVERRIDE_MOBS("overrideMobs"),
        SPAWN_RETRIES("spawnpositionretry"),
        SPAWN_MOB_CAP("mobcountthreshold"),
       SPAWN_Y_SEARCH_RANGE("spawnYsearchrange"),
        SPAWN_GIANTS("spawnGiants"),
        GIVE_INITIAL_GEAR("giveInitialGear"),
        GIVE_SPECIAL_LOOT("giveSpecialLoot"),
        USE_PLAYER_HEADS("usePlayerHeads"),
        DEBUG_ON("debugOn");

        private final String optionName;

        ModConfigOption(String optionName) {
            this.optionName = optionName;
        }

        public String getOptionName() {
            return optionName;
        }

        public static ModConfigOption fromOptionName(String optionName) {
            for (ModConfigOption option : values()) {
                if (option.getOptionName().equalsIgnoreCase(optionName)) {
                    return option;
                }
            }
            return null;
        }

        public static List<String> getAllOptionNames() {
            return Arrays.stream(values())
                    .map(ModConfigOption::getOptionName)
                    .collect(Collectors.toList());
        }

        public String getName() {
            return optionName;
        }
    }


    // ... other utility enums or methods ...
}