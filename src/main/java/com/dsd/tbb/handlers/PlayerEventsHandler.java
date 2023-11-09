package com.dsd.tbb.handlers;

import com.dsd.tbb.config.PlayerConfig;
import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.managers.BossBarManager;
import com.dsd.tbb.managers.PlayerManager;
import com.dsd.tbb.managers.SpawningManager;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEventsHandler {
    private static final PlayerManager playerManager = PlayerManager.getInstance();

    @SubscribeEvent
    public static void onPlayerJoin(PlayerLoggedInEvent event) {
        //TODO: Why is my gear being deleted when I join a server the second time
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

        //TODO: Need to refresh the boss bar list
        Level tLevel = event.getEntity().getLevel();
            TBBLogger.getInstance().debug("Player Joined",String.format("Level - %s",
                    tLevel.dimension().location()));
        BossBarManager bossBarManager = BossBarManager.getInstance();
        tLevel.getServer().getAllLevels().forEach(level -> {
            level.getAllEntities().forEach(entity -> {
                if(entity instanceof TrialsByGiantZombie){
                    bossBarManager.createBossBar(entity.getUUID(),entity.getName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
                }
                //TODO: This is nearly there. Need to get the player added to the boss bars too.
                // Print out entity information, including type and dimension
                //TBBLogger.getInstance().debug("Server start entity dump", String.format("Entity: [%s], UUID: [%s], " +
                //                "Type: [%s], World: [%s]", entity.getName().getString(),
                //        entity.getUUID(), entity.getType().toShortString(),level.dimension().location()));
            });

        });

    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID pUuid = event.getEntity().getUUID();
        playerManager.playerLeftGame(pUuid);

    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        ResourceLocation playerDimension = player.level.dimension().location();

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
