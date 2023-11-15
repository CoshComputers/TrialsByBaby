package com.dsd.tbb.ZZtesting;

import com.dsd.tbb.ZZtesting.loggers.TestEventLogger;
import com.dsd.tbb.config.TrialsConfig;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.util.ModUtilities;
import com.dsd.tbb.util.TBBLogger;

public class MultiPlayerTestScheduler {

    private static final int TICKS_PER_SECOND = 20;

    private static final int INCREMENT_INTERVAL = 10 * TICKS_PER_SECOND;
    private static int tickCount = 0;
    private static int thresholdIncrement = 20; // The amount to increase each interval
    private static int maxThreshold = 7000; // Maximum threshold value
    private static boolean isEnabled = false;
    public static void onTick() {
        if(!isEnabled) return;
        ConfigManager configManager = ConfigManager.getInstance();
        TrialsConfig trialsConfig = configManager.getTrialsConfig();
        int currentThreshold = trialsConfig.getMobCountThreshold();  //getSpawnGiants(); //baby spawn
        tickCount++;

        if (tickCount >= INCREMENT_INTERVAL) {
            if (currentThreshold < maxThreshold) {
                int newThreshold = Math.min(currentThreshold + thresholdIncrement, maxThreshold);
                trialsConfig.setMobCountThreshold(newThreshold);

                TestEventLogger.logEvent("380df991-f603-344c-a090-369bad2a924a","Baby Zombie Spawn Increase","TESTTICKER","Increasing to " + newThreshold);
                // Log the change for debugging
                TBBLogger.getInstance().info("Baby Zombie Spawn Incremented", "New threshold: " + newThreshold);
            }
            tickCount = 0; // Reset the tick count
        }

        if(currentThreshold >= maxThreshold){
            TBBLogger.getInstance().warn("Test Ticker","Shutting down server as thresholds hit");
            ModUtilities.writeGiantLogs();
            TrialsByBaby.triggerShutdown();
        }
    }

    public static void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
