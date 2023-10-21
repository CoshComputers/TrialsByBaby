package com.dsd.tbb.util;

import com.dsd.tbb.config.BabyZombieRules;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.level.Level;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class RuleManager {

    private static RuleManager INSTANCE = null;
    private static Path rulesDir = null;
    private static BabyZombieRules babyZombieRules;
    private RuleManager(){
        this.babyZombieRules = BabyZombieRules.getInstance();
    }

    public static RuleManager getInstance(){
        if(INSTANCE == null){
            synchronized (RuleManager.class){
                if(INSTANCE == null){
                    INSTANCE = new RuleManager();
                    rulesDir = FileAndDirectoryManager.getModDirectory().resolve("rules");
                    prepareRules();
                }
            }

        }

            return INSTANCE;
    }

    private static void prepareRules(){
        try {
            FileAndDirectoryManager.createDirectory(rulesDir);  // Update method call
            String[] ruleFiles = {"BabyZombieRules.json"};
            for (String rulesFile : ruleFiles) {
                Path configFilePath = rulesDir.resolve(rulesFile);
                if (!FileAndDirectoryManager.fileExists(configFilePath)) {  // Update method call
                    FileAndDirectoryManager.copyFileFromResources("rules", rulesFile, configFilePath);  // Update method call
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions as appropriate for your use case
        }
    }

    public static void loadBabyZombieRules(){
        Gson gson = new Gson();
        Path rulesFilePath = rulesDir.resolve("BabyZombieRules.json");
        try (FileReader reader = new FileReader(rulesFilePath.toFile())) {
            babyZombieRules = gson.fromJson(reader, BabyZombieRules.class);
        } catch (JsonSyntaxException e) {
            // Handle JSON syntax exception
            CustomLogger.getInstance().error(String.format("JSON Syntax Error: Failed to parse BabyZombieRules.json due to malformed JSON. [%s]", e));
        } catch (JsonIOException e) {
            // Handle JSON I/O exception
            CustomLogger.getInstance().error(String.format("JSON I/O Error: Failed to read BabyZombieRules.json due to an I/O error. [%s]", e));
        } catch (IOException e) {
            // Handle general I/O exception
            CustomLogger.getInstance().error(String.format("I/O Error: Failed to read BabyZombieRules.json due to an I/O error. [%s]", e));
        }
    }


    public BabyZombieRules getBabyZombieRules(){
        return this.babyZombieRules;
    }

    public EnumTypes.ZombieAppearance determineAppearance(Level world) {
        if (world.dimension().equals(Level.NETHER)) {
            return EnumTypes.ZombieAppearance.BLAZE;
        } else if (world.dimension().equals(Level.END)) {
            return EnumTypes.ZombieAppearance.ENDERMAN;
        } else {
            // Implement any other logic for determining appearance
            // such as a random chance for different appearances in the Overworld
            EnumTypes.ZombieAppearance appearance = EnumTypes.ZombieAppearance.REGULAR;


            return appearance;
        }
    }

}
