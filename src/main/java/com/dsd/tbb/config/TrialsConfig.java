package com.dsd.tbb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class TrialsConfig {
    private static final List<String> commandList = new ArrayList<>();
    private static final List<String> setCommandList = new ArrayList<>();
    private static volatile TrialsConfig INSTANCE = null;

    private boolean overrideMobs;
    private int spawnpositionretry;
    private int mobcountthreshold;
    private int spawnYsearchrange;
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

        setCommandList.add("spawnpositionretry");
        setCommandList.add("mobcountthreshold");
        setCommandList.add("spawnYsearchrange");

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
        this.spawnpositionretry = 5;
        this.mobcountthreshold = 50;
        this.spawnYsearchrange = 15;
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
    public static List<String> getSetCommandList() { return setCommandList; }
    public synchronized boolean isOverrideMobs() {
        return overrideMobs;
    }

    public synchronized void setOverrideMobs(boolean overrideMobs) {
        this.overrideMobs = overrideMobs;
    }

    public synchronized int getSpawnPositionRetry() {
        return spawnpositionretry;
    }

    public synchronized void setSpawnPositionRetry(int spawnpositionretry) {
        this.spawnpositionretry = spawnpositionretry;
    }

    public synchronized int getMobCountThreshold() {
        return mobcountthreshold;
    }

    public synchronized void setMobCountThreshold(int mobcountthreshold) {
        this.mobcountthreshold = mobcountthreshold;
    }

    public synchronized int getSpawnYsearchrange() {
        return spawnYsearchrange;
    }

    public synchronized void setSpawnYsearchange(int spawnYsearchrange) {
        this.spawnYsearchrange = spawnYsearchrange;
    }

    public synchronized boolean isSpawnGiants() {
        return spawnGiants;
    }

    public synchronized void setSpawnGiants(boolean spawnGiants) {
        this.spawnGiants = spawnGiants;
    }

    public synchronized boolean isGiveInitialGear() {
        return giveInitialGear;
    }

    public synchronized void setGiveInitialGear(boolean giveInitialGear) {
        this.giveInitialGear = giveInitialGear;
    }

    public synchronized boolean isGiveSpecialLoot() {
        return giveSpecialLoot;
    }

    public synchronized void setGiveSpecialLoot(boolean giveSpecialLoot) {
        this.giveSpecialLoot = giveSpecialLoot;
    }

    public synchronized boolean isUsePlayerHeads() {
        return usePlayerHeads;
    }

    public synchronized void setUsePlayerHeads(boolean usePlayerHeads) {
        this.usePlayerHeads = usePlayerHeads;
    }

    public synchronized boolean isDebugOn() {
        return debugOn;
    }

    public synchronized void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
