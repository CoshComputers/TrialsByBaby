package com.dsd.tbb.util;

import com.dsd.tbb.config.GearConfigWrapper;
import com.dsd.tbb.config.TrialsConfig;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager {

    private static ConfigManager INSTANCE = null;

    private TrialsConfig trialsConfig;
    private GearConfigWrapper gearConfigWrapper;

    private ConfigManager(Path configDir) {
        loadConfigs(configDir);
    }

    public static ConfigManager getInstance() {
        if (INSTANCE == null) {
            Path configDir = FileAndDirectoryManager.getModDirectory().resolve("config");
            prepareConfigs(configDir);
            INSTANCE = new ConfigManager(configDir);
        }
        return INSTANCE;
    }

    private static void prepareConfigs(Path configDir) {
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
            Path trialsConfigPath = configDir.resolve("TrialsConfig.json");
            trialsConfig = gson.fromJson(new FileReader(trialsConfigPath.toFile()), TrialsConfig.class);
            CustomLogger.getInstance().debug(String.format("TrialsConfig Loaded: [%s]",trialsConfig.toString()));


            // Load GearConfig
            //Path gearConfigPath = configDir.resolve("GearConfig.json");
            //gearConfigWrapper = gson.fromJson(new FileReader(gearConfigPath.toFile()), GearConfigWrapper.class);

// Optionally, you may now store gearConfig or gearItems in your ConfigManager for later access.


            //CustomLogger.getInstance().debug(String.format("Gear Config Loaded: [%s]",gearConfigWrapper.toString()));

        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions as appropriate for your use case
        }
    }

    public TrialsConfig getTrialsConfig() {
        return trialsConfig;
    }

    public GearConfigWrapper.GearConfig getGearConfig() {
        return gearConfigWrapper.getInitialGear();
    }


}
