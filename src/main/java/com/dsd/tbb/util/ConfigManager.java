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
    private TrialsConfig trialsConfig;
        private ConfigManager(Path configDir) {

        this.trialsConfig = new TrialsConfig();
        loadConfigs(configDir);
    }

    public static ConfigManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ConfigManager.class) {
                configDir = FileAndDirectoryManager.getModDirectory().resolve("config");
                prepareConfigs();
                INSTANCE = new ConfigManager(configDir);
            }
        }
        return INSTANCE;
    }

    private static void prepareConfigs() {
        try {
            FileAndDirectoryManager.createDirectory(configDir);  // Update method call
            String[] configFiles = {"TrialsConfig.json", "GearConfig.json"};
            for (String configFile : configFiles) {
                Path configFilePath = configDir.resolve(configFile);
                if (!FileAndDirectoryManager.fileExists(configFilePath)) {  // Update method call
                    FileAndDirectoryManager.copyFileFromResources("config", configFile, configFilePath);  // Update method call
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions as appropriate for your use case
        }
    }

    private void loadConfigs(Path configDir) {
        Gson gson = new Gson();
        try {
            // Load TrialsConfig
            CustomLogger.getInstance().debug("Loading Trials Config");
            Path trialsConfigPath = configDir.resolve("TrialsConfig.json");
            trialsConfig = gson.fromJson(new FileReader(trialsConfigPath.toFile()), TrialsConfig.class);
            CustomLogger.getInstance().debug(String.format("***TrialsConfig Loaded: [%s]",trialsConfig.toString()));

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
            CustomLogger.getInstance().error(String.format("Failed to save configuration - %s", e));
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

}
