package com.dsd.tbb.util;

import com.dsd.tbb.config.PlayerConfig;
import com.dsd.tbb.customs.entities.TrialsByBabyZombie;
import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.handlers.ModEventHandlers;
import com.dsd.tbb.rulehandling.RuleCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;


public class SpawningManager {
    private static final int GLOWING_TIMER = 2400;
    public static void spawnManagerTick(Level level, Player player, PlayerConfig playerConfig) {
        // Check and spawn Baby Zombies
        babyZombieSpawnCheck(level, player);

        // Check and spawn Giants
        if(playerConfig.GIANT_COOLDOWN <= 0) {
            giantSpawnCheck(level, player, playerConfig);
        }else{
            playerConfig.GIANT_COOLDOWN --;
        }
    }

    private static void babyZombieSpawnCheck(Level level, Player player) {
        //Generate a list of the rules that match time and dimension
        String tDimension = level.dimension().location().toString();
        long tTime = level.getDayTime();
        List<RuleCache.ApplicableRule> rules = RuleCache.getInstance().getApplicableRules(tDimension,tTime);
        if(rules.isEmpty()) {
            TBBLogger.getInstance().warn("babyZombieSpawnCheck","No Rules Matched, so not spawning");
            return;
        }

        for (RuleCache.ApplicableRule rule : rules) {
            if(!SpawningUtilities.shouldSpawnBaby(level,player,rule.getRarity())){
                continue; //we shouldn't spawn so continuing onto the next rule
            }
            int eHeight = TrialsByBabyZombie.MY_DEFAULT_HEIGHT;
            int packSize = SpawningUtilities.getPackSize(rule.getMinPackSize(),rule.getMaxPackSize());
            List<BlockPos> safeSpawnLocations = SpawningUtilities.getSafeSpawnPositions(level, eHeight, player.blockPosition(), packSize, true);
            if(!safeSpawnLocations.isEmpty()){
                spawnBabies(level,safeSpawnLocations,EnumTypes.ZombieAppearance.valueOf(rule.getMobType()));
            }
        }
    }

    private static void giantSpawnCheck(Level level, Player player, PlayerConfig playerConfig) {

        playerConfig.updateNearbyGiants(level, player);
        // Check for spawning conditions
        if (SpawningUtilities.shouldSpawnGiant(playerConfig)) {
            List<BlockPos> potentialPositions = SpawningUtilities.getSafeSpawnPositions(
                    level, TrialsByGiantZombie.HEIGHT, player.blockPosition(), 1, false);
            if (!potentialPositions.isEmpty()) {
                BlockPos spawnPos = potentialPositions.get(0); //we should only ever have 1 location here.
                if (spawnPos != null) {
                    UUID giantUUID = spawnGiant(spawnPos, level);
                    if (giantUUID != null) {
                        playerConfig.addGiant(giantUUID);
                        playerConfig.GIANT_COOLDOWN = ConfigManager.getInstance().getGiantConfig().getSpawnCooldown();
                    } else {
                        TBBLogger.getInstance().warn("giantSpawnCheck", "Failed to spawn GIANT for some reason");
                    }
                }
            }
        }
    }

    private static UUID spawnGiant(BlockPos pos, Level world) {
        // Instantiate and spawn the GIANT entity at the given position
        String newName = ConfigManager.getInstance().getRandomName();
        TBBLogger.getInstance().debug("SpawnGiant", String.format("Name of Giant [%s] - Y Pos [%d] ",
                newName,pos.getY()));
        try {
            TrialsByGiantZombie newGiant = ModEventHandlers.TRIALS_BY_GIANT_ZOMBIE.get().create(world);
            newGiant.setMyName(newName);
            newGiant.moveToPosition(pos);

            world.addFreshEntity(newGiant);
            // Apply the glowing effect for 1 or 2 minutes (1200 or 2400 ticks)
            MobEffectInstance glowingEffect = new MobEffectInstance(MobEffects.GLOWING, GLOWING_TIMER);
            newGiant.addEffect(glowingEffect);
            return newGiant.getUUID();
        } catch (NullPointerException e) {
            TBBLogger.getInstance().error("SpawnGiant", "Failed to spawn GIANT - null reference: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            TBBLogger.getInstance().error("SpawnGiant", "Failed to spawn GIANT - illegal argument: " + e.getMessage());
        } catch (Exception e) {
            TBBLogger.getInstance().error("SpawnGiant", "An unexpected error occurred while spawning the GIANT: " + e.getMessage());
        }
        return null;
    }

    private static void spawnBabies(Level level, List<BlockPos>safeSpawnLocations,EnumTypes.ZombieAppearance appearance){
        for (BlockPos pos : safeSpawnLocations) {
            TrialsByBabyZombie newBaby = ModEventHandlers.TRIALS_BY_BABY_ZOMBIE.get().create(level);
            newBaby.setAppearance(appearance);
            newBaby.setPos(pos.getX(),pos.getY(),pos.getZ());
            level.addFreshEntity(newBaby);
        }
    }
}
