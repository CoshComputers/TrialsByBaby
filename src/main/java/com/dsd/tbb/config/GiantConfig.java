package com.dsd.tbb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class GiantConfig {
    private static final List<String> setCommandList = new ArrayList<>();
    private static volatile GiantConfig INSTANCE = null;

    private double spawnFrequency;
    private int maxentitiesnearby;
    private int health;
    private int damage;
    private int followRange;
    private int visibilityRange;
    private int xpPoints;
    private String myName;

    private int chargeCooldown;
    private int smashCooldown;



    private GiantConfig(){

        setCommandList.add("spawnFrequency");
        setCommandList.add("maxentitiesnearby");
        setCommandList.add("followRange");
        setCommandList.add("visibilityRange");
        setCommandList.add("chargeCooldown");
        setCommandList.add("smashCooldown");

        setDefaults();
    }

    public static GiantConfig getInstance(){
        if (INSTANCE == null){
            synchronized (GiantConfig.class){
                INSTANCE = new GiantConfig();
            }
        }
        return INSTANCE;
    }

    private void setDefaults() {
        this.spawnFrequency = 0.01;
        this.maxentitiesnearby = 50;
        this.health = 100;
        this.damage = 1;
        this.followRange = 40;
        this.visibilityRange = 50;
        this.xpPoints = 50;
        this.chargeCooldown = 300;
        this.smashCooldown = 100;
    }
    // Getters and Setters for each field
    public static List<String> getSetCommandList() { return setCommandList; }


    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
    /********************************** SETTERS *************************************/
    public synchronized void setSpawnFrequency(double spawnFrequency) {
        this.spawnFrequency = spawnFrequency;
    }
    public synchronized void setFollowRange(int followRange) {
        this.followRange = followRange;
    }
    public synchronized void setVisibilityRange(int visibilityRange) {
        this.visibilityRange = visibilityRange;
    }
    public synchronized void setMyName(String myName) {
        this.myName = myName;
    }

    public synchronized void setChargeCooldown(int cooldown){ this.chargeCooldown = cooldown; }
    public synchronized void setSmashCooldown(int cooldown){ this.smashCooldown = cooldown; }

    public synchronized void setMaxentitiesnearby(int maxentitiesnearby) {
        this.maxentitiesnearby = maxentitiesnearby;
    }

    /********************************** GETTERS *************************************/
    public synchronized double getSpawnFrequency() {
        return spawnFrequency;
    }
    public synchronized int getHealth() {
        return health;
    }

    public synchronized int getDamage() {
        return damage;
    }

    public synchronized int getFollowRange() {
        return followRange;
    }
    public synchronized int getVisibilityRange() {
        return visibilityRange;
    }

    public synchronized int getXpPoints() {
        return xpPoints;
    }

    public synchronized String getMyName() {
        return myName;
    }

    public synchronized int getChargeCooldown() { return chargeCooldown;}
    public synchronized int getSmashCooldown() { return  smashCooldown;};

    public synchronized int getMaxentitiesnearby() {
        return maxentitiesnearby;
    }


}
