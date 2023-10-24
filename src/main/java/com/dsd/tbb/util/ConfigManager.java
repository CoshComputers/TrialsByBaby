package com.dsd.tbb.util;

import com.dsd.tbb.config.TrialsConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private static ConfigManager INSTANCE = null;
    private static Path configDir = null;
    private static TrialsConfig trialsConfig;
    private ConfigManager() {
            this.trialsConfig = TrialsConfig.getInstance();
    }

    public static ConfigManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ConfigManager.class) {
               INSTANCE = new ConfigManager();
            }
        }
        return INSTANCE;
    }

    public void prepareConfigs() {
        try {
            configDir = FileAndDirectoryManager.getModDirectory().resolve("config");
            if(configDir != null){
                FileAndDirectoryManager.createDirectory(configDir);  // Update method call
                String[] configFiles = {"TrialsConfig.json"};
                for (String configFile : configFiles) {
                    Path configFilePath = configDir.resolve(configFile);
                    if (!FileAndDirectoryManager.fileExists(configFilePath)) {  // Update method call
                        FileAndDirectoryManager.copyFileFromResources("config", configFile, configFilePath);  // Update method call
                    }
                }
            }else{
                TBBLogger.getInstance().error("prepareConfigs","Something has gone wrong Preparing the Config Files. Using Default Values");
            }

        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions as appropriate for your use case
        }
    }

    public void loadConfigs() {
        Gson gson = new Gson();
        try {
            // Load TrialsConfig
            String[] configFiles = {"TrialsConfig.json"};
            for (String configFile : configFiles) {
                Path configFilePath = configDir.resolve("TrialsConfig.json");
                TBBLogger.getInstance().debug("loadConfigs",String.format("About to read [%s] and load",configFilePath));
                trialsConfig = gson.fromJson(new FileReader(configFilePath.toFile()), TrialsConfig.class);
                TBBLogger.getInstance().debug("loadConfigs",String.format("Config Loaded: [%s]", trialsConfig.toString()));
            }

        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions as appropriate for your use case
        }
    }

    public boolean saveTrialsConfig() {
        boolean didSave = false;
        // Serialize the updated configuration to JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonConfig = gson.toJson(this.getTrialsConfig());
        // Get the path to the configuration file
       Path configFilePath = configDir.resolve("TrialsConfig.json");

        // Write the updated configuration to disk
        try (BufferedWriter writer = Files.newBufferedWriter(configFilePath)) {
            writer.write(jsonConfig);
            didSave = true;
        } catch (IOException e) {
            // Log the exception and notify command runner if necessary
            TBBLogger.getInstance().error("saveTrialsConfig",String.format("Failed to save configuration - %s", e));
            // The method to notify the command runner can be placed here
            // ModConfigCommand.notifyCommandRunner("The command was successful, but the config file has not been updated.");
        }
        return didSave;
    }

    public TrialsConfig getTrialsConfig() {
        return trialsConfig;
    }

    public String toggleMainConfigOption(EnumTypes.ModConfigOption option) {
        TrialsConfig config = this.getTrialsConfig();
        boolean newValue;
        switch (option) {
            case OVERRIDE_MOBS:
                newValue = !config.isOverrideMobs();
                config.setOverrideMobs(newValue);
                break;
            case SPAWN_GIANTS:
                newValue = !config.isSpawnGiants();
                config.setSpawnGiants(newValue);
                break;
            case GIVE_INITIAL_GEAR:
                newValue = !config.isGiveInitialGear();
                config.setGiveInitialGear(newValue);
                break;
            case GIVE_SPECIAL_LOOT:
                newValue = !config.isGiveSpecialLoot();
                config.setGiveSpecialLoot(newValue);
                break;
            case USE_PLAYER_HEADS:
                newValue = !config.isUsePlayerHeads();
                config.setUsePlayerHeads(newValue);
                break;
            case DEBUG_ON:
                newValue = !config.isDebugOn();
                config.setDebugOn(newValue);
                break;
            default:
                return null;
        }
        return "Toggled option " + option + " from " + !newValue + " to " + newValue;
    }

    public String setIntConfigOption(EnumTypes.ModConfigOption option, int newValue){
        TrialsConfig config = this.getTrialsConfig();

        switch (option) {
            case SPAWN_RETRIES:
                config.setSpawnPositionRetry(newValue);
                break;
            case SPAWN_MOB_CAP:
                config.setMobCountThreshold(newValue);
                break;
            case SPAWN_Y_SEARCH_RANGE:
                config.setSpawnYsearchange(newValue);
                break;
            default:
                return null;
        }
        return "Set option " + option + " to " + newValue;
    }

}
