package com.dsd.tbb.util;

import com.dsd.tbb.config.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfigManager {

    private static final String TRIALS = "Trials";
    private static final String GEAR = "InitialGear";
    private static final String BABY = "Baby";
    private static final String NAMES = "Names";
    private static final String GIANT = "Giant";
    // Map to hold file name to class mapping
    private final Map<String, Class<?>> configClassesMap = new HashMap<>();
    // Map to hold the loaded config objects
    private final Map<String, Object> configObjectsMap = new HashMap<>();
    private static ConfigManager INSTANCE = null;
    private static Path configDir = null;

    private ConfigManager() {
        configClassesMap.put(TRIALS, TrialsConfig.class);
        configClassesMap.put(BABY, MobDropConfig.class);
        configClassesMap.put(GEAR, InitialGearConfig.class);
        configClassesMap.put(NAMES, NamesConfig.class);
        configClassesMap.put(GIANT, GiantConfig.class);

        TrialsConfig trialsConfig = TrialsConfig.getInstance();
        configObjectsMap.put(TRIALS,trialsConfig);

        NamesConfig namesConfig = NamesConfig.getInstance();
        configObjectsMap.put(NAMES,namesConfig);

        GiantConfig giantConfig = GiantConfig.getInstance();
        configObjectsMap.put(GIANT,giantConfig);
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
                for (String configKey : configClassesMap.keySet()) {
                    String configFile = configKey + "Config.json";  // Add "Config.json" to the key value
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
        //TBBLogger.getInstance().bulkLog("LoadConfigs","------------------------ Loading Configurations -------------------------");
        try {
            for (Map.Entry<String, Class<?>> entry : configClassesMap.entrySet()) {
                String configFileName = entry.getKey() + "Config.json";
                Path configFilePath = configDir.resolve(configFileName);
                //TBBLogger.getInstance().debug("loadConfigs",String.format("About to read [%s] and load",configFilePath));
                Object configObject = gson.fromJson(new FileReader(configFilePath.toFile()), entry.getValue());
                storeConfigObject(entry.getKey(), configObject);
                //TBBLogger.getInstance().debug("loadConfigs",String.format("Config Loaded: [%s]", configObject.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions as appropriate for your use case
        }
        //TBBLogger.getInstance().bulkLog("LoadConfigs","---------------------- END Loading Configurations -------------------------");
    }

    /**********************************SAVE METHODS*******************************************/
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

    /*************************** UTILITIES ***************************************************/

    public void storeConfigObject(String key, Object configObject) {
        configObjectsMap.put(key, configObject);
    }

    // Method to get a config object by class
    public <T> T getConfigObject(String key, Class<T> configClass) {
        Object configObject = configObjectsMap.get(key);
        if (configClass.isInstance(configObject)) {
            return configClass.cast(configObject);
        }
        return null;  // or throw an exception if the config object is not found or of the wrong type
    }



    /**********************GET METHODS**********************************/

    public TrialsConfig getTrialsConfig() {
        return getConfigObject(TRIALS, TrialsConfig.class);
    }
    public MobDropConfig getBabyConfig() {
        return getConfigObject(BABY,MobDropConfig.class);
    }
    public InitialGearConfig getGearConfig() { return getConfigObject(GEAR,InitialGearConfig.class);}
    public GiantConfig getGiantConfig(){ return getConfigObject(GIANT, GiantConfig.class);}
    public NamesConfig getNamesConfig() { return getConfigObject(NAMES, NamesConfig.class);}
    public String getRandomName() { return getConfigObject(NAMES, NamesConfig.class).getRandomName();}

    /*************************Command Handling Methods*********************************/

    public String toggleMainConfigOption(EnumTypes.ModConfigOption option) {
        TrialsConfig config = this.getTrialsConfig();
        boolean newValue;
        switch (option) {
            case OVERRIDE_MOBS:
                newValue = !config.isOverrideMobs();
                config.setOverrideMobs(newValue);
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
            case SPAWN_GIANTS:
                config.setSpawnGiants(newValue);
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

    //TODO: Create this
    public PlayerConfig getPlayerConfig(UUID playerUUID) {

        return null;
    }

    //TODO: Create this
    public void savePlayerConfig(UUID playerUUID, PlayerConfig playerConfig) {
    }
}
