package com.dsd.tbb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class GiantConfig {
    private static final List<String> setCommandList = new ArrayList<>();
    private static volatile GiantConfig INSTANCE = null;

    private double spawnFrequency;
    private int scaleFactor;
    private int health;
    private int damage;
    private int followRange;
    private int aggressionLevel;
    private int visibilityRange;
    private int xpPoints;
    private double speed;

    private String myName;



    private GiantConfig(){

        setCommandList.add("spawnFrequency");
        setCommandList.add("scaleFactor");
        setCommandList.add("followRange");
        setCommandList.add("visibilityRange");
        setCommandList.add("speed");

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
        this.scaleFactor = 2;
        this.health = 100;
        this.damage = 15;
        this.followRange = 40;
        this.aggressionLevel = 2;
        this.visibilityRange = 50;
        this.xpPoints = 50;
        this.speed = 0.15;
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

    public synchronized void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public synchronized void setFollowRange(int followRange) {
        this.followRange = followRange;
    }

    public synchronized void setVisibilityRange(int visibilityRange) {
        this.visibilityRange = visibilityRange;
    }
    public synchronized void setSpeed(double speed){ this.speed = speed;}

    public void setMyName(String myName) {
        this.myName = myName;
    }

    /********************************** GETTERS *************************************/
    public synchronized double getSpawnFrequency() {
        return spawnFrequency;
    }

    public synchronized int getScaleFactor() {
        return scaleFactor;
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

    public synchronized int getAggressionLevel() {
        return aggressionLevel;
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

    public synchronized double getSpeed() { return speed; }
}
