package com.dsd.tbb.handlers;

import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.PlayerManager;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEventsHandler {
    private static final PlayerManager playerManager = PlayerManager.getInstance();
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Component message = Component.literal("Welcome to the Server");
        UUID pUuid = event.getEntity().getUUID();
        String pName = event.getEntity().getName().getString();
        // Your code here
        playerManager.playerJoinedGame(event.getEntity(), pName);
        TBBLogger.getInstance().info("onPlayerJoin",String.format("Number of Players online is [%d]",
                playerManager.getAllPlayerConfigs().size()));
        event.getEntity().sendSystemMessage(message);
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
        UUID pUuid = event.getEntity().getUUID();
        playerManager.playerLeftGame(pUuid);

    }
}
