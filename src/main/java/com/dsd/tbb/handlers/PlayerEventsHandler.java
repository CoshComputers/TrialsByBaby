package com.dsd.tbb.handlers;

import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
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

        //-------COMMENT OUT BEFORE PUBLISHING
        MobEffectInstance nightVisionEffect = new MobEffectInstance(MobEffects.NIGHT_VISION, 99999, 0, false, false);
        event.getEntity().addEffect(nightVisionEffect);
        //-------------------------------------


    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
        UUID pUuid = event.getEntity().getUUID();
        playerManager.playerLeftGame(pUuid);

    }


    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level.isClientSide && event.side == LogicalSide.SERVER) {
            if (ModUtilities.nextDouble() < ConfigManager.getInstance().getGiantConfig().getSpawnFrequency()) {
                BlockPos playerPos = event.player.blockPosition();
                if (SpawningUtilities.getNumberOfNearbyEntities(event.player.level, event.player) <
                        ConfigManager.getInstance().getGiantConfig().getMaxentitiesnearby()) {
                    List<BlockPos> potentialPositions = SpawningUtilities.getSafeSpawnPositions(
                            event.player.level, TrialsByGiantZombie.HEIGHT, playerPos, 1
                    );
                    if (!potentialPositions.isEmpty()) {
                        // Spawn the GIANT
                        BlockPos spawnPos = potentialPositions.get(0);
                        spawnGiant(spawnPos, event.player.level);
                    }
                }
            }
        }
    }

    private void spawnGiant(BlockPos pos, Level world) {
        // Instantiate and spawn the GIANT entity at the given position
        String newName = ConfigManager.getInstance().getRandomName();
        try {
            TrialsByGiantZombie newGiant = ModEventHandlers.TRIALS_BY_GIANT_ZOMBIE.get().create(world);
            newGiant.setMyName(newName);
            newGiant.moveToPosition(pos);
            world.addFreshEntity(newGiant);
        }catch(Exception e){
            TBBLogger.getInstance().error("spawnGiantCommand","Something has gone wrong spawning the Giant.");
            e.printStackTrace();
        }
    }

}
