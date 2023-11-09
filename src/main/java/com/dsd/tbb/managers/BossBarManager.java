package com.dsd.tbb.managers;

import com.dsd.tbb.util.TBBLogger;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager {
    private static BossBarManager instance;
    public static final double BB_RANGE = 64.0;
    private Map<UUID, ServerBossEvent> bossBars = new ConcurrentHashMap<>();


    private BossBarManager() {
        bossBars = new ConcurrentHashMap<>();
    }

    public static synchronized BossBarManager getInstance() {
        if (instance == null) {
            instance = new BossBarManager();
        }
        return instance;
    }

    public ServerBossEvent createBossBar(UUID giantId, Component name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
        // Create a new ServerBossEvent with the giant's name, color, and overlay
        ServerBossEvent bossBar = new ServerBossEvent(name, color, overlay);
        // Add the new boss bar to the map with the giant's UUID as the key
        this.bossBars.put(giantId, bossBar);
        TBBLogger.getInstance().debug("createBossBar",String.format("Created Boss Bar for giant [%s]",giantId));
        return bossBar;
    }

    public void updateBossBars(ServerLevel level) {
        TBBLogger.getInstance().bulkLog("updateBossBar",String.format("Number of Boss Bars = [%d]",
                this.bossBars.size()));
        this.bossBars.forEach((uuid, bossBar) -> {
            Entity giant = level.getEntity(uuid);
            TBBLogger.getInstance().bulkLog("updateBossBar", String.format("Null [%s], Is Alive [%s]",
                    giant==null?"true":"false","TEMP"));
            if (giant != null && giant.isAlive()) { // Ensure the giant is alive
                AABB bossBarRange = new AABB(giant.blockPosition()).inflate(BB_RANGE);
                List<ServerPlayer> playersInRange = level.getEntitiesOfClass(ServerPlayer.class, bossBarRange);
                TBBLogger.getInstance().bulkLog("updateBossBars",String.format("Players in Range = [%d]",
                        playersInRange.size()));
                // Add new players to the boss bar and remove players who are no longer in range
                // Add players who are in range and not already seeing the boss bar
                for (ServerPlayer player : playersInRange) {
                    if (!bossBar.getPlayers().contains(player)) {
                        TBBLogger.getInstance().debug("updateBossBar", String.format("Added Player [%s] to Boss Bar",
                                player.getName()));
                        bossBar.addPlayer(player);
                    }
                }
                // Remove players who are no longer in range or in a different dimension
                bossBar.getPlayers().forEach(player -> {
                    if (!playersInRange.contains(player) || !player.level.dimension().equals(level.dimension())) {
                        TBBLogger.getInstance().debug("updateBossBar", String.format("Removing Player [%s] to Boss Bar",
                                player.getName()));
                        bossBar.removePlayer(player);
                    }
                });
            }else {
                TBBLogger.getInstance().bulkLog("updateBossBar",String.format("UUID [%s] Giant Null or Not Alive",uuid));
                // If the giant is null or not alive, remove the boss bar
                //removeBossBar(uuid);
            }
        });
    }

    public void updateProgress(UUID giantId, float progress) {
        ServerBossEvent bossBar = bossBars.get(giantId);
        if (bossBar != null) {
            bossBar.setProgress(progress);
        }
    }

    public void addPlayerToBossBar(ServerPlayer player, UUID giantId) {
        ServerBossEvent bossBar = bossBars.get(giantId);
        if (bossBar != null && !bossBar.getPlayers().contains(player)) {
            bossBar.addPlayer(player);
        }
    }

    public void removePlayerFromBossBar(ServerPlayer player, UUID giantId) {
        ServerBossEvent bossBar = bossBars.get(giantId);
        if (bossBar != null && bossBar.getPlayers().contains(player)) {
            bossBar.removePlayer(player);
        }
    }

    public void removeBossBar(UUID uuid) {
        TBBLogger.getInstance().debug("removeBossBar",String.format("Removing Boss Bar for UUID [%s]",uuid));
        ServerBossEvent bossBar = bossBars.remove(uuid);
        if (bossBar != null) {
            bossBar.removeAllPlayers();
        }
    }
    public void onDimensionChange(ServerPlayer player) {
        // Handle dimension change events for boss bars
    }

    public Collection<ServerPlayer> getPlayers(UUID giantId) {
        ServerBossEvent bossBar = bossBars.get(giantId);
        return bossBar != null ? bossBar.getPlayers() : Collections.emptyList();
    }

    public void setVisible(UUID giantId, boolean visible) {
        ServerBossEvent bossBar = bossBars.get(giantId);
        if (bossBar != null) {
            bossBar.setVisible(visible);
            // If the visibility is set to false, you may also want to remove all players
            if (!visible) {
                bossBar.removeAllPlayers();
            }
        }
    }


    public void setBossBarName(UUID giantId, Component name) {
        ServerBossEvent bossBar = bossBars.get(giantId);
        if (bossBar != null) {
            bossBar.setName(name);
        }
    }


    // Additional methods as needed...
}
