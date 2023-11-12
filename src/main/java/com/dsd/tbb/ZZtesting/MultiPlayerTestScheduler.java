package com.dsd.tbb.ZZtesting;

import com.dsd.tbb.ZZtesting.loggers.TestEventLogger;
import com.dsd.tbb.config.TrialsConfig;
import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.util.TBBLogger;

public class MultiPlayerTestScheduler {

    private static final int TICKS_PER_SECOND = 20;

    private static final int INCREMENT_INTERVAL = 30 * TICKS_PER_SECOND;
    private static int tickCount = 0;
    private static int thresholdIncrement = 5; // The amount to increase each interval
    private static int maxThreshold = 520; // Maximum threshold value
    private static boolean isEnabled = false;
    public static void onTick() {
        if(!isEnabled) return;


        tickCount++;

        if (tickCount >= INCREMENT_INTERVAL) {
            ConfigManager configManager = ConfigManager.getInstance();
            TrialsConfig trialsConfig = configManager.getTrialsConfig();

            int currentThreshold = trialsConfig.getMobCountThreshold(); //baby spawn
            int giantSpawnMax = trialsConfig.getSpawnGiants(); //giant spawn

            if (giantSpawnMax < maxThreshold) {
                int newThreshold = Math.min(giantSpawnMax + thresholdIncrement, maxThreshold);
                trialsConfig.setMobCountThreshold(newThreshold);

                TestEventLogger.logEvent("380df991-f603-344c-a090-369bad2a924a","Giant Zombie Spawn Increase","Increasing to " + newThreshold);

                // Log the change for debugging
                TBBLogger.getInstance().info("Giant Zombie Spawn Incremented", "New threshold: " + newThreshold);
            }

            tickCount = 0; // Reset the tick count
        }
    }



    public static void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
