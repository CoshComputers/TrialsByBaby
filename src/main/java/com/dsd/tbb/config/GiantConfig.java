package com.dsd.tbb.config;

import com.dsd.tbb.util.ModUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class GiantConfig {
    private static final List<String> setCommandList = new ArrayList<>();
    private static volatile GiantConfig INSTANCE = null;

    private double spawnFrequency;
    private int health;
    private int damage;
    private int followRange;
    private int visibilityRange;
    private int xpPoints;
    private String myName;

    private int chargeCooldown;
    private int smashCooldown;
    private int spawnCooldown;

    private List<EnchantedBookDrop> drops = new ArrayList<>();


    private GiantConfig(){
        setCommandList.add("spawnFrequency");
        setCommandList.add("spawnCooldown");
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
        this.health = 100;
        this.damage = 1;
        this.followRange = 40;
        this.visibilityRange = 50;
        this.xpPoints = 50;
        this.chargeCooldown = 300;
        this.smashCooldown = 100;
        this.spawnCooldown = 20000;
    }
    // Getters and Setters for each field
    public static List<String> getSetCommandList() { return setCommandList; }

    // This method will return a list of unique random drops
    public List<ItemStack> getRandomDrops() {
        List<EnchantedBookDrop> allDrops = new ArrayList<>(this.drops); // Assume this.drops is your list of possible drops
        Collections.shuffle(allDrops); // Shuffle the list to randomize it
        int numberOfDrops = getRandomNumberOfDrops(); // Your method to determine the number of drops

        // Stream the list and limit it to the number of drops you want, then map to ItemStacks
        return allDrops.stream().limit(numberOfDrops)
                .map(drop -> createItemStackFromDrop(drop))
                .collect(Collectors.toList());
    }

    // Helper method to create an ItemStack from a drop
    private ItemStack createItemStackFromDrop(EnchantedBookDrop drop) {

        Map<Enchantment, Integer> enchantments = new HashMap<>();
        ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(drop.getEnchantment()));
        if(enchantment != null) {
            int enchantmentLevel = getNewEnchantmentLevel(enchantment,drop.getMaxlevel());
            enchantments.put(enchantment,enchantmentLevel);
            EnchantmentHelper.setEnchantments(enchantments, itemStack);
        }
        return itemStack;
    }

    private int getNewEnchantmentLevel(Enchantment enchantment, int bookLevel) {
        int enchantDefaultMaxLevel = enchantment.getMaxLevel();
        if(enchantDefaultMaxLevel < bookLevel){
            //return a random number between the default max level and the level from the
            //drop. Eg: if we have Efficiency, and the drop had level 9. This will return
            //a level between 5 and 9.
            return ModUtilities.nextInt(enchantDefaultMaxLevel+1,bookLevel);
        }else{
            //return the book level, as it's lower than the Max level anyway.
            return bookLevel;
        }


    }


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
    public synchronized void setSpawnCooldown(int spawnCooldown) { this.spawnCooldown = spawnCooldown;}

    public synchronized void setDrops(List<EnchantedBookDrop> drops) { this.drops = drops;}


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
    public synchronized int getSpawnCooldown() { return spawnCooldown; }
    public List<EnchantedBookDrop> getDrops() {
        return drops;
    }

    private int getRandomNumberOfDrops() {
        if(drops.size() > 1) {
            return ModUtilities.nextInt(1, Math.min(drops.size(),4));
        }else return 1;
    }


    // Nested class to represent enchanted book drops
    public static class EnchantedBookDrop {
        private String item;
        private String enchantment;
        private int maxlevel;

        public EnchantedBookDrop(String item, String enchantment, int level) {
            this.item = item;
            this.enchantment = enchantment;
            this.maxlevel = level;
        }

        public String getItem() { return item;}

        public void setItem(String item) { this.item = item;}

        public String getEnchantment() {
            return enchantment;
        }

        public void setEnchantment(String enchantment) {
            this.enchantment = enchantment;
        }

        public int getMaxlevel() {
            return maxlevel;
        }

        public void setMaxlevel(int maxlevel) {
            this.maxlevel = maxlevel;
        }
    }


}
