package com.dsd.tbb.handlers;

import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.TBBLogger;
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

                int trackerSize = CustomMobTracker.getInstance().getMobCountInRegion(world.dimension(),
                        tempChunkPos.getRegionLocalX(), tempChunkPos.getRegionLocalZ());
                int mobCountThreshold = ConfigManager.getInstance().getTrialsConfig().getMobCountThreshold();

                TBBLogger.getInstance().bulkLog("onPlayerTick",String.format("Tracker for region [%d][%d] totals [%d]",
                        tempChunkPos.getRegionLocalX(),tempChunkPos.getRegionLocalZ(),trackerSize));

                if(trackerSize < mobCountThreshold) {
                    //TODO: Need to get Applicable Rules and Pack Size. Hardcoded Pack Size for now
                    int packSize = 4;
                    int eHeight = (int) Math.ceil(Zombie.DEFAULT_BB_HEIGHT); //rounding up the height.
                    int i = 0;

                    safeSpawnLocations = SpawningUtilities.getSafeSpawnPositions(player.level,eHeight,player.blockPosition(),packSize);
                    //Checking if we have any safe spawn locations
                    if(safeSpawnLocations.size() > 0) {
                        for (BlockPos pos : safeSpawnLocations) {
                            TBBLogger.getInstance().bulkLog("onPlayerTick[1]",String.format("Creating Zombie number [%d]",i));
                            Zombie zombie = EntityType.ZOMBIE.create(world);
                            if (zombie != null) {
                                TBBLogger.getInstance().bulkLog("onPlayerTick[2]",String.format("Position X[%d] Y[%f] Z[%d]",
                                        pos.getX(),pos.getY()+1.1,pos.getZ()));
                                zombie.setPos(pos.getX(), pos.getY()+1.1, pos.getZ());  // Spawn zombie 2 blocks above the player

                                world.addFreshEntity(zombie);
                                // Add to CustomMobTracker
                                CustomMobTracker.getInstance().addMob(zombie);
                            } else {
                                TBBLogger.getInstance().bulkLog("PlayerTickEvent[3]","Failed to Create a Zombie - Don't Know Why");
                            }
                            i++;
                        }
                    }else{
                        TBBLogger.getInstance().bulkLog("PlayerTickEvent[3]","No safe spawning Locations found. Nothing Spawned");
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
