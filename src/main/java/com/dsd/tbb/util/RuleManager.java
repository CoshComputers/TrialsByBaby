package com.dsd.tbb.util;

import net.minecraft.world.level.Level;

public class RuleManager {

    private static RuleManager INSTANCE = null;
    private RuleManager(){

    }

    public static RuleManager getInstance(){
        if(INSTANCE == null){
            synchronized (RuleManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RuleManager();
                }
            }
        }

            return INSTANCE;
    }
    public EnumTypes.ZombieAppearance determineAppearance(Level world) {
        if (world.dimension().equals(Level.NETHER)) {
            return EnumTypes.ZombieAppearance.BLAZE;
        } else if (world.dimension().equals(Level.END)) {
            return EnumTypes.ZombieAppearance.ENDERMAN;
        } else {
            // Implement any other logic for determining appearance
            // such as a random chance for different appearances in the Overworld
            EnumTypes.ZombieAppearance appearance = EnumTypes.ZombieAppearance.UNKNOWN;


            return appearance;
        }
    }

}
