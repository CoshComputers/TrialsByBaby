package com.dsd.tbb.managers;

import com.dsd.tbb.ZZtesting.loggers.TestEventLogger;
import com.dsd.tbb.config.PlayerConfig;
import com.dsd.tbb.customs.entities.general.TrialsByBabyZombie;
import com.dsd.tbb.customs.entities.general.TrialsByGiantZombie;
import com.dsd.tbb.handlers.ModEventHandlers;
import com.dsd.tbb.rulehandling.RuleCache;
import com.dsd.tbb.util.EnumTypes;
import com.dsd.tbb.util.SpawningUtilities;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;


public class SpawningManager {
   //private static final Map<UUID, TrialsByGiantZombie> giantZombieMap = new ConcurrentHashMap<>();

    private static final int GLOWING_TIMER = 1 * (20 * 60); // n * (20*60) where n is the number of minutes to glow

    public static void spawnManagerTick(Level level, Player player, PlayerConfig playerConfig) {
        // Check and spawn Baby Zombies
        babyZombieSpawnCheck(level, player);
        giantSpawnCheck(level, player, playerConfig);

    }

    private static void babyZombieSpawnCheck(Level level, Player player) {
        //Generate a list of the rules that match time and dimension
        String tDimension = level.dimension().location().toString();
        long tTime = level.getDayTime();
        List<RuleCache.ApplicableRule> rules = RuleCache.getInstance().getApplicableRules(tDimension, tTime);
        if (rules.isEmpty()) {
           return;
        }

        for (RuleCache.ApplicableRule rule : rules) {
            int numToSpawn = SpawningUtilities.shouldSpawnBaby(level, player, rule.getRarity());
            //TBBLogger.getInstance().debug("Baby Spawn Check","Number to Spawn = " + numToSpawn);
            //TestEventLogger.logEvent(player.getStringUUID(), "Number to Spawn","Number to spawn = " + numToSpawn);
            if (numToSpawn <= 0) {
                continue; //we shouldn't spawn so continuing onto the next rule
            }
            int eHeight = TrialsByBabyZombie.MY_DEFAULT_HEIGHT;
            //int packSize = SpawningUtilities.getPackSize(rule.getMinPackSize(), rule.getMaxPackSize());
            List<BlockPos> safeSpawnLocations = SpawningUtilities.getSafeSpawnPositions(level, eHeight, player.blockPosition(), numToSpawn, true);
            if (!safeSpawnLocations.isEmpty()) {
                spawnBabies(level, safeSpawnLocations, EnumTypes.ZombieAppearance.valueOf(rule.getMobType()));
            }
        }
    }

    private static void giantSpawnCheck(Level level, Player player, PlayerConfig playerConfig) {
         playerConfig.updateNearbyGiants(level, player);
         //TBBLogger.getInstance().debug("giantSpawnCheck",String.format("Number of Nearby Giants: [%d]",
         //        playerConfig.numberOfNearbyGiants()));
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
        try {
            TrialsByGiantZombie newGiant = ModEventHandlers.TRIALS_BY_GIANT_ZOMBIE.get().create(world);
            newGiant.setMyName(newName);
            newGiant.moveToPosition(pos);
            newGiant.setRandomDrops();

            world.addFreshEntity(newGiant);
            TestEventLogger.logEvent(newGiant.getUUID().toString(),"Giant Spawning",String.valueOf(newGiant.getId()),"New Giant has been spawned");

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

    private static void spawnBabies(Level level, List<BlockPos> safeSpawnLocations, EnumTypes.ZombieAppearance appearance) {
        for (BlockPos pos : safeSpawnLocations) {
            TrialsByBabyZombie newBaby = ModEventHandlers.TRIALS_BY_BABY_ZOMBIE.get().create(level);
            newBaby.setAppearance(appearance);
            newBaby.setPos(pos.getX(), pos.getY(), pos.getZ());
            level.addFreshEntity(newBaby);
            //TestEventLogger.logEvent(newBaby.getUUID().toString(),"Baby Spawning","New Baby has been spawned");
        }
    }

    /*public static void saveGiantZombies() {
        List<String> uuidList = giantZombieMap.keySet().stream()
                .map(UUID::toString)
                .collect(Collectors.toList());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(uuidList);

        Path file = FileAndDirectoryManager.getWorldDirectory().resolve("giant_zombies.json");
        try {
            Files.write(file, jsonString.getBytes(),StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadGiantZombies(MinecraftServer server) {
        Path file = FileAndDirectoryManager.getWorldDirectory().resolve("giant_zombies.json");
        if (Files.exists(file)) {
            try {
                String jsonString = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                List<String> uuidList = gson.fromJson(jsonString, new TypeToken<List<String>>() {
                }.getType());

                for (ServerLevel level : server.getAllLevels()) { // This iterates through all dimensions
                    for (String uuidStr : uuidList) {
                        UUID uuid = UUID.fromString(uuidStr);
                        for (Entity entity : level.getAllEntities()) { // Adjust this method to your version
                            if (entity.getUUID().equals(uuid) && entity instanceof TrialsByGiantZombie) {
                                TBBLogger.getInstance().debug("LoadGiants",String.format("Putting [%s] in the list with UUID [%s]",
                                        ((TrialsByGiantZombie) entity).getMyName(),entity.getUUID()));
                                giantZombieMap.put(uuid, (TrialsByGiantZombie) entity);
                                break;
                            }
                        }
                    }
                }
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }*/

    /*public static TrialsByGiantZombie getGiantZombieByUUID(UUID uuid) {
        return giantZombieMap.get(uuid);
    }

    public static void addGiantZombie(TrialsByGiantZombie giantZombie) {
        giantZombieMap.put(giantZombie.getUUID(), giantZombie);
    }

    public static void removeGiantZombieByUUID(UUID uuid) {
        giantZombieMap.remove(uuid);
    }

    public static Collection<TrialsByGiantZombie> getAllGiants() {
        return giantZombieMap.values();
    }

    public static int numberOfGiantsInAllDimensions() {
        return giantZombieMap.size();
    }*/

}
