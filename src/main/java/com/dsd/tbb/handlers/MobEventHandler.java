package com.dsd.tbb.handlers;

import com.dsd.tbb.customs.entities.general.TrialsByBabyZombie;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.MobDropUtilities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
