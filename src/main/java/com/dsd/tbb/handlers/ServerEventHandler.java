package com.dsd.tbb.handlers;

import com.dsd.tbb.ZZtesting.MultiPlayerTestScheduler;
import com.dsd.tbb.ZZtesting.loggers.ServerPerformanceMonitor;
import com.dsd.tbb.commands.GiantCommands;
import com.dsd.tbb.commands.TrialsCommands;
import com.dsd.tbb.config.BabyZombieRules;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.managers.BossBarManager;
import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.managers.FileAndDirectoryManager;
import com.dsd.tbb.rulehandling.RuleManager;
import com.dsd.tbb.util.CentralizedLoggerScheduler;
import com.dsd.tbb.util.TBBLogger;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {
    private static ConfigManager configManager;
    private static RuleManager ruleManager;
    private static CentralizedLoggerScheduler cls;

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        //TBBLogger.getInstance().bulkLog("LootTableLoadEvent",String.format("Loaded Loot Table Path-[%s]-Name[%s]",event.getName().getPath(),event.getName()));
    }
    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        TBBLogger.getInstance().info("onServerAboutToStart","****** INVOKED TRIALS SERVER ABOUT TO START METHOD *********");
        TrialsByBaby.scheduler = Executors.newScheduledThreadPool(1);
        TBBLogger.getInstance().info("onServerAboutToStart","Initializing Mod Directories");
        Path serverDir = event.getServer().getWorldPath(LevelResource.ROOT);
        FileAndDirectoryManager.initialize(serverDir);

        TBBLogger.getInstance().info("onServerAboutToStart",String.format("Mod Directory = %s",FileAndDirectoryManager.getModDirectory().toString()));
        TBBLogger.getInstance().info("onServerAboutToStart",String.format("Player Directory = %s",FileAndDirectoryManager.getPlayerDataDirectory().toString()));

        TBBLogger.getInstance().info("onServerAboutToStart","Loading Configurations");
        configManager = ConfigManager.getInstance();
        configManager.prepareConfigs();
        configManager.loadConfigs();

       // TBBLogger.getInstance().debug("ServerAboutToStart",String.format("Some Random Name [%s]",configManager.getNamesConfig().getRandomName()));
       // TBBLogger.getInstance().debug("ServerAboutToStart",String.format("Giant config Loaded\n%s",configManager.getGiantConfig().toString()));

        TBBLogger.getInstance().info("onServerAboutToStart","Loading Rules");
        ruleManager = RuleManager.getInstance();
        ruleManager.prepareRules();
        ruleManager.loadBabyZombieRules();
        //SpawningManager.loadGiantZombies(event.getServer());
        //outputRules();
}
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        TBBLogger.getInstance().info("onServerStarting","****** INVOKED TRIALS SERVER STARTING METHOD *********");
        TBBLogger.getInstance().info("onServierStarting","\n\n");
        TrialsByBaby.setServer(event.getServer());
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getServer().getCommands().getDispatcher();
        TrialsCommands.register(commandDispatcher);
        GiantCommands.register(commandDispatcher);
        cls = new CentralizedLoggerScheduler();
        event.getServer().getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, event.getServer());

        //-------COMMENT OUT BEFORE PUBLISHING--------------------------
        for (ServerLevel world : event.getServer().getAllLevels()) {
            // Set the time to midnight (18000 ticks)
            world.setDayTime(18000);
        }
        //-------------------------------------------------------------
       // makePortals(event);


    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Get all loaded levels from the server and update boss bars for each
            MinecraftServer server = event.getServer(); // Obtain the MinecraftServer instance
            for (ServerLevel level : server.getAllLevels()) {
                BossBarManager.getInstance().updateBossBars(level);
            }
            ServerPerformanceMonitor.onTick(event);
            MultiPlayerTestScheduler.onTick();



        }
    }


    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event){
        TBBLogger.getInstance().info("onServerStopping","********** INVOKED TRIALS SERVER STOPPING METHOD **************");
        ConfigManager.getInstance().saveTrialsConfig();
        BossBarManager.getInstance().removeAllBossBars();
        TrialsByBaby.scheduler.shutdown();
        try {
            if (!TrialsByBaby.scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                TrialsByBaby.scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            TrialsByBaby.scheduler.shutdownNow();
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }


        //SpawningManager.saveGiantZombies();
        TBBLogger.getInstance().bulkLog("onServerStopping","********************************END OF FILE*************************");

        /*ServerPerformanceMonitor.writeToFile();
        TestEventLogger.writeToFile();
        TestResultData.writeToFile();*/
        TBBLogger.getInstance().writeLogToFile();

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
            //TBBLogger.getInstance().debug(sb.toString());
        }
    }

    private static void makePortals(ServerStartingEvent event){
        ServerLevel world = event.getServer().overworld();  // Get the overworld
        BlockPos worldSpawnPos = world.getSharedSpawnPos();

        BlockPos netherPortalPos = worldSpawnPos.offset(10, 0, 0);  // Adjust coordinates as needed
        BlockPos endPortalPos = worldSpawnPos.offset(-10, 0, 0);  // Adjust coordinates as needed

        for (int x = 0; x < 1; x++) {
            for (int z = 0; z < 1; z++) {
                    world.setBlockAndUpdate(endPortalPos.offset(x - 1, 0, z - 1), Blocks.END_PORTAL.defaultBlockState());
                    world.setBlockAndUpdate(netherPortalPos.offset(x - 1, 0, z - 1), Blocks.NETHER_PORTAL.defaultBlockState());

            }
        }

    }

}
