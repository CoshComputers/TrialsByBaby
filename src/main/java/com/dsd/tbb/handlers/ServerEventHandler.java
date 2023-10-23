package com.dsd.tbb.handlers;

import com.dsd.tbb.commands.TrialsCommands;
import com.dsd.tbb.config.BabyZombieRules;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.rulehandling.RuleManager;
import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.CustomLogger;
import com.dsd.tbb.util.CustomMobTracker;
import com.dsd.tbb.util.FileAndDirectoryManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;
import java.util.Map;

@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {
    private static ConfigManager configManager;
    private static RuleManager ruleManager;

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        CustomLogger.getInstance().info(("****** INVOKED TRIALS SERVER ABOUT TO START METHOD *********"));
        CustomLogger.getInstance().info("Initializing Mod Directories");
        Path serverDir = event.getServer().getWorldPath(LevelResource.ROOT);
        FileAndDirectoryManager.initialize(serverDir);

        CustomLogger.getInstance().info(String.format("Mod Directory = %s",FileAndDirectoryManager.getModDirectory().toString()));
        CustomLogger.getInstance().info(String.format("Player Directory = %s",FileAndDirectoryManager.getPlayerDataDirectory().toString()));

        CustomLogger.getInstance().info("Loading Configurations");
        configManager = ConfigManager.getInstance();
        configManager.prepareConfigs();
        configManager.loadConfigs();

        CustomLogger.getInstance().info("Loading Rules");
        ruleManager = RuleManager.getInstance();
        ruleManager.prepareRules();
        ruleManager.loadBabyZombieRules();
        //outputRules();

    }
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        CustomLogger.getInstance().info(("****** INVOKED TRIALS SERVER STARTING METHOD *********"));

        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getServer().getCommands().getDispatcher();
        TrialsCommands.register(commandDispatcher);

        //-------COMMENT OUT BEFORE PUBLISHING--------------------------
        for (ServerLevel world : event.getServer().getAllLevels()) {
            // Set the time to midnight (18000 ticks)
            world.setDayTime(18000);
        }
        //-------------------------------------------------------------

    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event){
        CustomLogger.getInstance().info("********** INVOKED TRIALS SERVER STOPPING METHOD **************");
        ConfigManager.getInstance().saveTrialsConfig();

        CustomLogger.getInstance().debug("MOB TRACKER OUTPUT: ");
        String allMobsInfo = CustomMobTracker.getInstance().getAllMobsInfo();
        // Log the information or do something with it
        CustomLogger.getInstance().debug(allMobsInfo);
    }


    private static void outputRules(){
        // Get the BabyZombieRules instance from the RuleManager
        BabyZombieRules babyZombieRules = ruleManager.getBabyZombieRules();

// Get the map of DimensionRules from the BabyZombieRules instance
        Map<String, BabyZombieRules.DimensionRules> dimensionRulesMap = babyZombieRules.getRules();

// Iterate through the map of DimensionRules
        for (Map.Entry<String, BabyZombieRules.DimensionRules> entry : dimensionRulesMap.entrySet()) {
            // Get the key (dimension name) and value (DimensionRules object) from each entry in the map
            StringBuilder sb = new StringBuilder();
            String dimensionName = entry.getKey();

            sb.append("Dimension ").append(dimensionName).append(" rules:\n");
            BabyZombieRules.DimensionRules dimensionRules = entry.getValue();
            sb.append(dimensionRules.toString()).append("\n");
            CustomLogger.getInstance().debug(sb.toString());
        }
    }

}
