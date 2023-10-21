package com.dsd.tbb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TrialsConfig {
    private boolean overrideMobs;
    private boolean spawnGiants;
    private boolean giveInitialGear;
    private boolean giveSpecialLoot;
    private boolean usePlayerHeads;
    private boolean debugOn;

    // Getters and Setters for each field
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
