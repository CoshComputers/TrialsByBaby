package com.dsd.tbb.config;

import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.TBBLogger;
import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerConfig {
    @SerializedName("playerUuid")
    private static UUID playerUuid;

    @SerializedName("lastLogin")
    private String lastLogin;

    private static final int MAX_SCAN_DISTANCE = 64;
    private transient Set<UUID> nearbyGiants = ConcurrentHashMap.newKeySet();
    public transient int GIANT_COOLDOWN = ConfigManager.getInstance().getGiantConfig().getSpawnCooldown();
    private String playerName;
    // No-arg constructor for Gson
    private PlayerConfig() {
        // Gson will override this
    }

    // Constructor for new players
    public PlayerConfig(Player player, Level level) {
        this.playerUuid = player.getUUID();
        this.playerName = player.getName().getString();
        updatePlayerTimeStamp();
        updateNearbyGiants(level,player);
        TBBLogger.getInstance().debug("PlayerConfig constructor", this.toString());

    }


    // Method to update the lastLogin field
    public void updatePlayerTimeStamp() {Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        String formatted = formatter.format(now);
        this.lastLogin = formatted;  // Outputs: 2023-10-26T10:15:30.555Z
    }


    public void addGiant(UUID giantUuid) {
        nearbyGiants.add(giantUuid);
    }

    public void removeGiant(UUID giantUuid) {
        nearbyGiants.remove(giantUuid);
    }

    public Set<UUID> getNearbyGiants() {
        return Collections.unmodifiableSet(nearbyGiants);
    }

    public int numberOfNearbyGiants() {
        return nearbyGiants.size();
    }

    public void updateNearbyGiants(Level level, Player player) {
        // Define the search area around the player
        AABB searchArea = new AABB(
                player.getX() - MAX_SCAN_DISTANCE, player.getY() - MAX_SCAN_DISTANCE, player.getZ() - MAX_SCAN_DISTANCE,
                player.getX() + MAX_SCAN_DISTANCE, player.getY() + MAX_SCAN_DISTANCE, player.getZ() + MAX_SCAN_DISTANCE
        );

        // Find all giant entities within the search area
        List<TrialsByGiantZombie> currentGiants = level.getEntitiesOfClass(TrialsByGiantZombie.class, searchArea);

        // Update the nearbyGiants set
        Set<UUID> currentGiantUUIDs = new HashSet<>();
        for (TrialsByGiantZombie giant : currentGiants) {
            UUID giantUUID = giant.getUUID();
            currentGiantUUIDs.add(giantUUID);
            // If this giant is not already in the nearbyGiants set, add it.
            if (!nearbyGiants.contains(giantUUID)) {
                nearbyGiants.add(giantUUID);
                // Additional logic for when a new giant is detected
            }
        }

        // Remove any giants that are no longer nearby
        nearbyGiants.removeIf(uuid -> !currentGiantUUIDs.contains(uuid));
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setPlayerName(String pName){
            this.playerName = pName;
    }
    public String getPlayerName(){
        return playerName;
    }

    public ItemStack getPlayerHead() {
        ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);  // Ensure PLAYER_HEAD is the correct item for your Minecraft version
        CompoundTag nbt = new CompoundTag();
        nbt.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), new GameProfile(this.getPlayerUuid(), this.playerName)));
        playerHead.setTag(nbt);
        return playerHead;
    }

    @Override
    public String toString(){
        String s = "Player Name: [" + this.playerName + "] " +
                "Player UUID: [" + this.playerUuid + "] " +
                "Player Last Login: [" + this.lastLogin + "] " +
                "Giant Spawn Cooldown [" + this.GIANT_COOLDOWN + "] " +
                "Nearby Giants [" + this.numberOfNearbyGiants() + "]";

        return s;
    }


}
