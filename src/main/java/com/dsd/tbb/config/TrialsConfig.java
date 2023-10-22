package com.dsd.tbb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class TrialsConfig {
    private static final List<String> commandList = new ArrayList<>();
    private static volatile TrialsConfig INSTANCE = null;

    private boolean overrideMobs;
    private boolean spawnGiants;
    private boolean giveInitialGear;
    private boolean giveSpecialLoot;
    private boolean usePlayerHeads;
    private boolean debugOn;


    private TrialsConfig(){
        commandList.add("overrideMobs");
        commandList.add("spawnGiants");
        commandList.add("giveInitialGear");
        commandList.add("giveSpecialLoot");
        commandList.add("usePlayerHeads");
        commandList.add("debugOn");

        setDefaults();
    }

    public static TrialsConfig getInstance(){
        if (INSTANCE == null){
            synchronized (TrialsConfig.class){
                INSTANCE = new TrialsConfig();
            }
        }
        return INSTANCE;
    }

    private void setDefaults() {
        this.overrideMobs = false;
        this.spawnGiants = false;
        this.giveInitialGear = false;
        this.giveSpecialLoot = false;
        this.usePlayerHeads = false;
        this.debugOn = false;
    }
    // Getters and Setters for each field
    public static List<String> getCommandList() {
        return commandList;
    }
    public boolean isOverrideMobs() {
        return overrideMobs;
    }

    public void setOverrideMobs(boolean overrideMobs) {
        this.overrideMobs = overrideMobs;
    }

    public boolean isSpawnGiants() {
        return spawnGiants;
    }

    public void setSpawnGiants(boolean spawnGiants) {
        this.spawnGiants = spawnGiants;
    }

    public boolean isGiveInitialGear() {
        return giveInitialGear;
    }

    public void setGiveInitialGear(boolean giveInitialGear) {
        this.giveInitialGear = giveInitialGear;
    }

    public boolean isGiveSpecialLoot() {
        return giveSpecialLoot;
    }

    public void setGiveSpecialLoot(boolean giveSpecialLoot) {
        this.giveSpecialLoot = giveSpecialLoot;
    }

    public boolean isUsePlayerHeads() {
        return usePlayerHeads;
    }

    public void setUsePlayerHeads(boolean usePlayerHeads) {
        this.usePlayerHeads = usePlayerHeads;
    }

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
