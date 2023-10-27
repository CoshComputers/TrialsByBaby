package com.dsd.tbb.handlers;

import com.dsd.tbb.entities.TrialsByBabyZombie;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.rulehandling.RuleCache;
import com.dsd.tbb.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobEventHandler {

    @SubscribeEvent
    public static void onLivingAttackEvent(LivingAttackEvent event){
        //TBBLogger.getInstance().bulkLog("onLivingAttack",String.format("For mob: [%s]",
        //        event.getEntity().getClass()));
    }
    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event){
        //TBBLogger.getInstance().bulkLog("onLivingHurt",String.format("For mob: [%s]",
        //        event.getEntity().getClass()));
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event){
       // TBBLogger.getInstance().bulkLog("onLivingDeath",String.format("For mob: [%s]",
       //         event.getEntity().getClass()));
    }

    @SubscribeEvent
    public static void onLivingDropsEvent(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof TrialsByBabyZombie) {
            TrialsByBabyZombie babyZombie = (TrialsByBabyZombie) entity;
            ItemStack customDrop = MobDropUtilities.getDropForBabyZombie(babyZombie);
            if(!customDrop.isEmpty()) {
                event.getDrops().clear();  // Clear the existing drops
                event.getDrops().add(new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), customDrop));
            }else{
                TBBLogger.getInstance().warn("onLivingDropEvent","No custom drops returned so dropping default item");
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        List<BlockPos> safeSpawnLocations;
        // Your logic here
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            Level level = player.getLevel();
            if (player.level instanceof ServerLevel && !player.level.isClientSide()) { // Ensure we are on the server side.
                int mobCountThreshold = ConfigManager.getInstance().getTrialsConfig().getMobCountThreshold();
                int nearbyEntityCount = SpawningUtilities.getNumberOfNearbyEntities(level, player);

                if (nearbyEntityCount < mobCountThreshold) {
                    int i = 0;
                    boolean shouldSpawn = false;
                    int packSize;
                    int eHeight = (int) Math.ceil(TrialsByBabyZombie.MY_DEFAULT_HEIGHT); //rounding up the height.
                    String tDimension = level.dimension().location().toString();
                    long tTime = level.getDayTime();
                    List<RuleCache.ApplicableRule> rules = RuleCache.getInstance().getApplicableRules(tDimension,tTime);

                    //TBBLogger.getInstance().bulkLog("onPlayerTick-Rules",String.format("tDimension [%s], tTime [%d], Number of Rules matched [%d]",
                    //        tDimension,tTime, rules.size()));

                    for (RuleCache.ApplicableRule rule : rules) {

                        packSize = SpawningUtilities.getPackSize(rule.getMinPackSize(),rule.getMaxPackSize());
                        double randToCheck = Math.random();
                        shouldSpawn = randToCheck < rule.getRarity();
                        //TBBLogger.getInstance().bulkLog("onPlayerTick-Rules",String.format("Rand [%f] - Rarity [%f]",randToCheck,rule.getRarity()));
                        if(!shouldSpawn){
                            //TBBLogger.getInstance().bulkLog("onPlayerTick-Rules","Should Spawn is False");
                            continue; //checking the rarity in the rule to decide whether to spawn or not.
                        }

                        //TBBLogger.getInstance().bulkLog("onPlayerTick-Rules", String.format("Rule [%d] - %s", i, rule.toString()));
                        i++;
                        safeSpawnLocations = SpawningUtilities.getSafeSpawnPositions(player.level, eHeight, player.blockPosition(), packSize);
                        //Checking if we have any safe spawn locations
                        if (!safeSpawnLocations.isEmpty()) {
                            for (BlockPos pos : safeSpawnLocations) {
                                //TBBLogger.getInstance().bulkLog("Spawning Mob",String.format("Location = [%d][%d][%d]",
                                //        pos.getX(),pos.getY(),pos.getZ()));
                                TrialsByBabyZombie zombie = new TrialsByBabyZombie(level);
                                if (zombie != null) {
                                    EnumTypes.ZombieAppearance appearance = EnumTypes.ZombieAppearance.valueOf(rule.getMobType());
                                    zombie.setAppearance(appearance);
                                    zombie.setPos(pos.getX(), pos.getY(), pos.getZ());  // Spawn zombie 2 blocks above the player
                                    level.addFreshEntity(zombie);
                                } else {
                                    //TBBLogger.getInstance().bulkLog("onPlayerTick", "Failed to Create a Zombie - Don't Know Why");
                                }
                            }
                        } else {
                            TBBLogger.getInstance().debug("onPlayerTick", "No safe spawning Locations found. Nothing Spawned");
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPotentialSpawnsEvent(LevelEvent.PotentialSpawns event) {
        MobCategory mobCategory = event.getMobCategory();
        List<MobSpawnSettings.SpawnerData> spawnerList = event.getSpawnerDataList();
        if (spawnerList != null) {
            if (mobCategory == MobCategory.MONSTER) {
                event.setCanceled(true);
            }
        }
    }


}
