package com.dsd.tbb.rulehandling;

import com.dsd.tbb.config.BabyZombieRules;
import com.dsd.tbb.util.TBBLogger;
import com.dsd.tbb.util.EnumTypes;
import com.dsd.tbb.util.FileAndDirectoryManager;
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
    private final RuleCache ruleCache = RuleCache.getInstance();
    private RuleManager(){

        this.babyZombieRules = BabyZombieRules.getInstance();
    }

    public static RuleManager getInstance(){
        if(INSTANCE == null){
            synchronized (RuleManager.class){
                if(INSTANCE == null){
                    INSTANCE = new RuleManager();
                }
            }
        }
            return INSTANCE;
    }

    public void prepareRules(){
        try {
            rulesDir = FileAndDirectoryManager.getModDirectory().resolve("rules");
            if(rulesDir != null){
                FileAndDirectoryManager.createDirectory(rulesDir);  // Update method call
                String[] ruleFiles = {"BabyZombieRules.json"};
                for (String rulesFile : ruleFiles) {
                    Path ruleFilePath = rulesDir.resolve(rulesFile);
                    if (!FileAndDirectoryManager.fileExists(ruleFilePath)) {  // Update method call
                        FileAndDirectoryManager.copyFileFromResources("rules", rulesFile, ruleFilePath);  // Update method call
                        TBBLogger.getInstance().info("prepareRules",String.format("Created Rules File [%s]", ruleFilePath));
                    }
                }
            }else{
                TBBLogger.getInstance().error("prepareRules","Something has gone wrong preparing Rules Files, using Defaults");
            }

        } catch (IOException e) {
            e.printStackTrace();  // Handle exceptions as appropriate for your use case
        }
    }

    public void loadBabyZombieRules(){
        Gson gson = new Gson();
        Path rulesFilePath = rulesDir.resolve("BabyZombieRules.json");
        try {
            String[] ruleFiles = {"BabyZombieRules.json"};
            for (String rulesFile : ruleFiles) {
                Path ruleFilePath = rulesDir.resolve(rulesFile);
                //TBBLogger.getInstance().debug("loadBabyZombieRules",String.format("About to read [%s] and Load",ruleFilePath));
                babyZombieRules = gson.fromJson(new FileReader(ruleFilePath.toFile()), BabyZombieRules.class);

            }
        } catch (JsonSyntaxException e) {
            // Handle JSON syntax exception
            TBBLogger.getInstance().error("loadBabyZombieRules",String.format("JSON Syntax Error: Failed to parse BabyZombieRules.json due to malformed JSON. [%s]", e));
        } catch (JsonIOException e) {
            // Handle JSON I/O exception
            TBBLogger.getInstance().error("loadBabyZombieRules",String.format("JSON I/O Error: Failed to read BabyZombieRules.json due to an I/O error. [%s]", e));
        } catch (IOException e) {
            // Handle general I/O exception
            TBBLogger.getInstance().error("loadBabyZombieRules",String.format("I/O Error: Failed to read BabyZombieRules.json due to an I/O error. [%s]", e));
        }finally {
            ruleCache.populateCache(babyZombieRules);
            TBBLogger.getInstance().info("loadBabyZombieRules","BabyZombie Rule Cache populated");
            //CustomLogger.getInstance().debug(String.format("Cache Contents:\n%s",ruleCache));
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
