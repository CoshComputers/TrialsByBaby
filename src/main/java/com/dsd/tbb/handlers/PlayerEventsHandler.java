package com.dsd.tbb.handlers;

import com.dsd.tbb.ZZtesting.MultiPlayerTest;
import com.dsd.tbb.config.PlayerConfig;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.managers.BossBarManager;
import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.managers.PlayerManager;
import com.dsd.tbb.managers.SpawningManager;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEventsHandler {
    private static final PlayerManager playerManager = PlayerManager.getInstance();
    private static final int INITIAL_COOLDOWN = 200;
    private static int spawnGiantTickCooldown = INITIAL_COOLDOWN;


    @SubscribeEvent
    public static void onPlayerJoin(PlayerLoggedInEvent event) {
        Component message = Component.literal("Welcome to the Server");
        UUID pUuid = event.getEntity().getUUID();
        String pName = event.getEntity().getName().getString();
        ServerPlayer player = (ServerPlayer) event.getEntity();
        // Your code here
        playerManager.playerJoinedGame(event.getEntity(), pName);
        TBBLogger.getInstance().info("onPlayerJoin", String.format("Number of Players online is [%d]",
                 playerManager.getAllPlayerConfigs().size()));
        event.getEntity().sendSystemMessage(message);
        ServerLevel tLevel = ((ServerPlayer) event.getEntity()).getLevel();

        if (TrialsByBaby.MOD_IS_IN_TESTING) {
            MobEffectInstance pEffect = new MobEffectInstance(MobEffects.NIGHT_VISION, 99999, 0, false, false);
            event.getEntity().addEffect(pEffect);
            pEffect = new MobEffectInstance(MobEffects.SATURATION,99999);
            event.getEntity().addEffect(pEffect);
            TBBLogger.getInstance().debug("Player Joined",String.format("Level - %s",
                        tLevel.dimension().location()));
            player.setGameMode(GameType.SPECTATOR);
            MultiPlayerTest.logTestStart(player);

        }

        BossBarManager bossBarManager = BossBarManager.getInstance();
        TrialsByBaby.scheduler.schedule(()-> {
            bossBarManager.ensureBossBarsExist(tLevel);
            bossBarManager.addPlayerToBossBar(player, event.getEntity().getUUID());
        },4, TimeUnit.SECONDS);

    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID pUuid = event.getEntity().getUUID();
        playerManager.playerLeftGame(pUuid);
        MultiPlayerTest.logTestEnd();

    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!player.level.isClientSide) {
           ResourceLocation playerDimension = player.level.dimension().location();
            ServerLevel tLevel = ((ServerPlayer) event.getEntity()).getLevel();

            BossBarManager bossBarManager = BossBarManager.getInstance();
            //TBBLogger.getInstance().debug("onDimensionChange", String.format("Player changed to [%s] Dimension - scheduling Update", playerDimension));
            TrialsByBaby.scheduler.schedule(() -> {
                bossBarManager.ensureBossBarsExist(tLevel);
                bossBarManager.addPlayerToBossBar(player, event.getEntity().getUUID());
                ConfigManager.getInstance().getPlayerConfig(player.getUUID()).updateNearbyGiants(tLevel, player);
            }, 4, TimeUnit.SECONDS);

            //TBBLogger.getInstance().debug("onDimensionChange", String.format("Scheduled = Nearby Giant Count [%d]",
            //        PlayerManager.getInstance().getPlayerConfig(player.getUUID()).numberOfNearbyGiants()));
        }
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(spawnGiantTickCooldown <=0) {
            //TBBLogger.getInstance().bulkLog("Ticking Player","Cooldown done - checking Spawn");
            spawnGiantTickCooldown = ConfigManager.getInstance().getGiantConfig().getSpawnCooldown();
            if (!event.player.level.isClientSide && event.side == LogicalSide.SERVER) {
                Player thisPlayer = event.player;
                PlayerConfig thisPlayerConfig = PlayerManager.getInstance().getPlayerConfig(thisPlayer.getUUID());
                SpawningManager.spawnManagerTick(thisPlayer.getLevel(), thisPlayer, thisPlayerConfig);

            }
        }else{
            spawnGiantTickCooldown --;
        }
    }


}
