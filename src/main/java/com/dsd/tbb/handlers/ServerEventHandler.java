package com.dsd.tbb.handlers;

import com.dsd.tbb.commands.TrialsCommands;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.CustomLogger;
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

@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {
    private static ConfigManager configManager;


    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        CustomLogger.getInstance().info(("****** INVOKED TRIALS SERVER ABOUT TO START METHOD *********"));
        Path serverDir = event.getServer().getWorldPath(LevelResource.ROOT);
        FileAndDirectoryManager.initialize(serverDir);
        configManager = ConfigManager.getInstance();

        CustomLogger.getInstance().info(String.format("Mod Directory = %s",FileAndDirectoryManager.getModDirectory().toString()));
        CustomLogger.getInstance().info(String.format("Player Directory = %s",FileAndDirectoryManager.getPlayerDataDirectory().toString()));

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
    }



}
