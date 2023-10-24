package com.dsd.tbb.handlers;

import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.CustomLogger;
import com.dsd.tbb.util.CustomMobTracker;
import com.dsd.tbb.util.SpawningUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

;

@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpawnCreatureHandler{


    @SubscribeEvent
    public static void onEntityEnteringSection(EntityEvent.EnteringSection event) {
        // Get the entity from the event
        Entity entity = event.getEntity();

        // Check if the entity is one of the custom mobs you are tracking
        if (entity instanceof Zombie) {
            // Get the new chunk position
            ChunkPos newChunkPos = new ChunkPos(entity.blockPosition());
            // Update the mob's position in the CustomMobTracker
            CustomMobTracker.getInstance().updateMobPosition(entity.getUUID(), newChunkPos);
            String allMobsInfo = CustomMobTracker.getInstance().getAllMobsInfo();
            // Log the information or do something with it
            //CustomLogger.getInstance().debug(allMobsInfo);
        }
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        List<BlockPos> safeSpawnLocations = new ArrayList<>();
        // Your logic here
        if(event.phase == TickEvent.Phase.END) {
            Player player = event.player;

            if(player.level instanceof ServerLevel && !player.level.isClientSide()) { // Ensure we are on the server side.
                Level world = (ServerLevel) player.level;
                ChunkPos tempChunkPos = new ChunkPos(player.blockPosition());
                //CustomLogger.getInstance().debug(String.format("****Player Chunk Position [%d][%d]",tempChunkPos.getRegionLocalX(),tempChunkPos.getRegionLocalZ()));

                int trackerSize = CustomMobTracker.getInstance().getSize();
                int mobCountThreshold = ConfigManager.getInstance().getTrialsConfig().getMobCountThreshold();


                if(trackerSize < mobCountThreshold) {
                    CustomLogger.getInstance().debug("Tracker size less than Threshold, spawning new Mob");
                    //TODO: Need to get Applicable Rules and Pack Size. Hardcoded Pack Size for now
                    //TODO: Need to get Entity Height without creating an entity. Hardcoded for now
                    int packSize = 4;
                    int eHeight = 2;

                    safeSpawnLocations = SpawningUtilities.getSafeSpawnPositions(player.level,eHeight,player.blockPosition(),packSize);
                    //Checking if we have any safe spawn locations
                    if(safeSpawnLocations.size() > 0) {
                        for (BlockPos pos : safeSpawnLocations) {
                            Zombie zombie = EntityType.ZOMBIE.create(world);
                            if (zombie != null) {
                                zombie.setPos(pos.getX(), pos.getY(), pos.getZ());  // Spawn zombie 2 blocks above the player
                                world.addFreshEntity(zombie);
                                // Add to CustomMobTracker
                                CustomMobTracker.getInstance().addMob(zombie);
                            } else {
                                CustomLogger.getInstance().error("Failed to Create a Zombie - Don't Know Why");
                            }
                        }
                    }else{
                        CustomLogger.getInstance().error("No safe spawning Locations found. Nothing Spawned");
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPotentialSpawnsEvent(LevelEvent.PotentialSpawns event){
        MobCategory mobCategory = event.getMobCategory();
        List<MobSpawnSettings.SpawnerData> spawnerList = event.getSpawnerDataList();
        if (spawnerList != null) {
            if (mobCategory == MobCategory.MONSTER) {
               event.setCanceled(true);
            }
        }
    }


}
