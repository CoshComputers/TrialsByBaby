package com.dsd.tbb.config;

import com.dsd.tbb.util.ModUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NamesConfig {
    private StringBuilder rName;
    @SerializedName("names")
    private List<String> names;
    @SerializedName("adjectives")
    private List<String> adjectives;

    private static volatile NamesConfig INSTANCE = null;

    public NamesConfig() {
        this.names = new ArrayList<>();
        this.adjectives = new ArrayList<>();
        names.add("William");
        adjectives.add("Darsterdly");

        rName = new StringBuilder();
    }

    public static NamesConfig getInstance(){
        if (INSTANCE == null){
            synchronized (NamesConfig.class){
                INSTANCE = new NamesConfig();
            }
        }
        return INSTANCE;
    }

    // Getters and setters for each list
    public synchronized List<String> getNames() {
        return names;
    }
    public synchronized List<String> getAdjectives() {
        return adjectives;
    }
    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public synchronized String getRandomName(){
        if(!rName.isEmpty()){
            rName.delete(0,rName.length());
        }
        rName.append(names.get(ModUtilities.nextInt(names.size())));
        rName.append(" The ");
        rName.append(adjectives.get(ModUtilities.nextInt(adjectives.size())));

        return rName.toString();
    }

}
