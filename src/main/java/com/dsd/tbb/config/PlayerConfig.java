package com.dsd.tbb.config;

import com.dsd.tbb.util.TBBLogger;
import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PlayerConfig {
    @SerializedName("playerUuid")
    private static UUID playerUuid;

    @SerializedName("lastLogin")
    private String lastLogin;


    private String playerName;
    // No-arg constructor for Gson
    private PlayerConfig() {
        // Gson will override this
    }

    // Constructor for new players
    public PlayerConfig(UUID playerUuid, String playerName) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        updatePlayerTimeStamp();
        TBBLogger.getInstance().debug("PlayerConfig constructor", this.toString());
    }


    // Method to update the lastLogin field
    public void updatePlayerTimeStamp() {Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        String formatted = formatter.format(now);
        this.lastLogin = formatted;  // Outputs: 2023-10-26T10:15:30.555Z
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

    /*public ItemStack getPlayerHead() {
        ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);  // Ensure PLAYER_HEAD is the correct item for your Minecraft version
        CompoundNBT nbt = new CompoundNBT();
        UUID testUUID = UUID.fromString("0db1ebd5-50e2-46e9-95fb-ffd49efcf79c");
        nbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), new GameProfile(testUUID, this.playerName)));
        playerHead.setTag(nbt);
        CustomLogger.getInstance().debug(String.format("player [%s] head NBT = %s",this.playerName,playerHead.getTag().toString()));
        return playerHead;
    }*/

    @Override
    public String toString(){
        String s = "Player Name: [" + this.playerName + "] " +
                "Player UUID: [" + this.playerUuid + "] " +
                "Player Last Login: [" + this.lastLogin + "] ";

        return s;
    }


}
