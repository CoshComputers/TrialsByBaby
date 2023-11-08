package com.dsd.tbb.util;


import com.dsd.tbb.config.PlayerConfig;
import com.dsd.tbb.customs.entities.PlayerEntityHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class PlayerManager {

    private static final PlayerManager instance = new PlayerManager();
    private final Map<UUID, PlayerConfig> playerConfigs = new HashMap<>();
    private static List<UUID> playerUuids = new ArrayList<>();

    private PlayerManager() {
    }

    public static PlayerManager getInstance() {
        return instance;
    }

    public synchronized void playerJoinedGame(Player player, String pName) {
        //Create the player config in memory.
        boolean isGiveGear = ConfigManager.getInstance().getTrialsConfig().isGiveInitialGear();
        UUID pUuid = player.getUUID();
        Path playerConfigFilePath = FileAndDirectoryManager.getPlayerDataDirectory().resolve(pUuid + ".json");
        boolean firstLogin = !FileAndDirectoryManager.fileExists(playerConfigFilePath);
        PlayerConfig pConfig = new PlayerConfig(player,player.getLevel());
        playerConfigs.put(pUuid, pConfig);
        playerUuids.add(pUuid);

        //Now save the config. This will overwrite existing files or create new.
        savePlayerConfig(pUuid);
        //TBBLogger.getInstance().debug("playerJoinGame",String.format("First Login = [%s] - Give Initial Gear = [%s]",
        //        firstLogin ? "TRUE":"FALSE", isGiveGear ? "TRUE":"FALSE"));
        if(firstLogin && isGiveGear){
            //do some stuff, like giving gear
            PlayerEntityHelper.givePlayerGear(player);
        }
    }

    public synchronized void playerLeftGame(UUID pUuid) {
        playerConfigs.get(pUuid).updatePlayerTimeStamp();
        savePlayerConfig(pUuid);
        playerConfigs.remove(pUuid);
        playerUuids.remove(pUuid);
    }

    private synchronized void savePlayerConfig(UUID pUuid) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        PlayerConfig config = playerConfigs.get(pUuid);
        if (config != null) {
            String jsonConfig = gson.toJson(config);
            Path playerFile = FileAndDirectoryManager.getPlayerDataDirectory().resolve(pUuid + ".json");

            try {
                Files.write(playerFile, jsonConfig.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                TBBLogger.getInstance().info("savePlayerConfig", String.format("Successfully save player config for %s", pUuid.toString()));
            } catch (JsonSyntaxException e) {
                // Handle JSON syntax exception
                TBBLogger.getInstance().error("savePlayerConfig", String.format("JSON Syntax Error: Failed to parse  player config json due to malformed JSON. [%s]", e));
            } catch (JsonIOException e) {
                // Handle JSON I/O exception
                TBBLogger.getInstance().error("savePlayerConfig", String.format("JSON I/O Error: Failed to do something with player configjson due to an I/O error. [%s]", e));
            } catch (IOException e) {
                TBBLogger.getInstance().error("savePlayerConfig", String.format("Failed to save player config - %s", e));
                // Handle exception as needed for your use case
            }
        } else {
            TBBLogger.getInstance().error("savePlayerConfig", String.format("Unable to save player config for UUID %s", pUuid.toString()));
        }
    }

    public synchronized PlayerConfig getRandomPlayer() {
        if (!playerUuids.isEmpty()) {
            UUID randomPlayerUuid = playerUuids.get(ModUtilities.nextInt(playerUuids.size()));
            return playerConfigs.get(randomPlayerUuid);
        }
        return null;  // Return null or throw an exception if no players are available
    }

    public synchronized PlayerConfig getPlayerConfig(UUID playerUuid) {
        return playerConfigs.get(playerUuid);
    }

    public synchronized Map<UUID, PlayerConfig> getAllPlayerConfigs() {
        return Collections.unmodifiableMap(playerConfigs);
    }

}

