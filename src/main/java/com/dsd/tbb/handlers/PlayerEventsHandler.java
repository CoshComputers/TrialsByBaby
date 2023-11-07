package com.dsd.tbb.handlers;

import com.dsd.tbb.config.PlayerConfig;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.PlayerManager;
import com.dsd.tbb.util.SpawningManager;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
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
        TBBLogger.getInstance().info("onPlayerJoin", String.format("Number of Players online is [%d]",
                playerManager.getAllPlayerConfigs().size()));
        event.getEntity().sendSystemMessage(message);

        //-------COMMENT OUT BEFORE PUBLISHING
        MobEffectInstance nightVisionEffect = new MobEffectInstance(MobEffects.NIGHT_VISION, 99999, 0, false, false);
        event.getEntity().addEffect(nightVisionEffect);
        //-------------------------------------
        TBBLogger.getInstance().bulkLog("PlayerJoin",String.format("Player Y Pos [%d] ", event.getEntity().blockPosition().getY()));

    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID pUuid = event.getEntity().getUUID();
        playerManager.playerLeftGame(pUuid);

    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level.isClientSide && event.side == LogicalSide.SERVER) {
            Player thisPlayer = event.player;
            PlayerConfig thisPlayerConfig = PlayerManager.getInstance().getPlayerConfig(thisPlayer.getUUID());
            SpawningManager.spawnManagerTick(thisPlayer.getLevel(), thisPlayer, thisPlayerConfig);
        }
    }


}
