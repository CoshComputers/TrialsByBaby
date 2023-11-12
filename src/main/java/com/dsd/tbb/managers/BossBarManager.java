package com.dsd.tbb.managers;

import com.dsd.tbb.ZZtesting.loggers.TestEventLogger;
import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
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
import java.util.stream.Collectors;

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
        synchronized (this.bossBars) {
            this.bossBars.put(giantId, bossBar);
        }
        //TBBLogger.getInstance().debug("createBossBar",String.format("Created Boss Bar for giant [%s]-[%s]",giantId, name.getString()));
        return bossBar;
    }

    public ServerBossEvent getOrCreateBossBar(UUID giantId, Component name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay){
        ServerBossEvent bossBar;
        synchronized (this.bossBars) {
            bossBar = this.bossBars.get(giantId);
        }

        if(bossBar == null){
            //TBBLogger.getInstance().bulkLog("getOrCreateBossBar",String.format("Have to Create Boss Bar for Giant [%s]-[%s]",
           //         giantId,name.getString()));
            bossBar = createBossBar(giantId, name, color, overlay);
        }
        //TBBLogger.getInstance().bulkLog("getOrCreateBossBar",String.format("Returning Boss Bar for Giant [%s]-[%s]",
        //        giantId,name.getString()));

        return bossBar;
    }

    public void ensureBossBarsExist(ServerLevel level){
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof TrialsByGiantZombie) {
                UUID giantUUID = entity.getUUID();
                TrialsByGiantZombie giant = (TrialsByGiantZombie) entity;
                // Check if Boss Bar exists, and create if not
                getOrCreateBossBar(giantUUID, giant.getDisplayName(),BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
            }
        }
    }


    public void updateBossBars(ServerLevel level) {
        synchronized (this.bossBars) {
            this.bossBars.forEach((uuid, bossBar) -> {
                Entity giant = level.getEntity(uuid);
                if (giant != null && giant.isAlive()) {
                    AABB bossBarRange = new AABB(giant.blockPosition()).inflate(BB_RANGE);
                    List<ServerPlayer> playersInRange = level.getEntitiesOfClass(ServerPlayer.class, bossBarRange);

                    addPlayersToBossBar(bossBar, playersInRange);
                    removePlayersFromBossBar(bossBar, playersInRange, level);
                }
            });
        }
    }

    private void addPlayersToBossBar(ServerBossEvent bossBar, List<ServerPlayer> playersInRange) {
        // Iterate over players in range and add them to the boss bar if not already present
        for (ServerPlayer player : playersInRange) {
            if (!bossBar.getPlayers().contains(player)) {
                TBBLogger.getInstance().debug("updateBossBar", String.format("Added Player [%s] to Boss Bar", player.getName()));
                TestEventLogger.logEvent(player.getStringUUID(), "Boss Bar Update","Adding Player to Boss Bar");
                bossBar.addPlayer(player);
            }
        }
    }

    private void removePlayersFromBossBar(ServerBossEvent bossBar, List<ServerPlayer> playersInRange, ServerLevel level) {
        // Collect players to be removed
        List<ServerPlayer> playersToRemove = bossBar.getPlayers().stream()
                .filter(player -> !playersInRange.contains(player) || !player.level.dimension().equals(level.dimension()))
                .collect(Collectors.toList());

        // Remove players after iteration
        playersToRemove.forEach(player -> {
            TBBLogger.getInstance().debug("updateBossBar", String.format("Removing Player [%s] from Boss Bar", player.getName()));
            bossBar.removePlayer(player);
            TestEventLogger.logEvent(player.getStringUUID(), "Boss Bar Update","Removing Player to Boss Bar");
        });
    }

    public void updateProgress(UUID giantId, float progress) {
        synchronized (this.bossBars) {
            ServerBossEvent bossBar = this.bossBars.get(giantId);
            if (bossBar != null) {
                bossBar.setProgress(progress);
            }
        }
    }

    public void addPlayerToBossBar(ServerPlayer player, UUID giantId) {
        synchronized (this.bossBars) {
            ServerBossEvent bossBar = this.bossBars.get(giantId);
            if (bossBar != null && !bossBar.getPlayers().contains(player)) {
                bossBar.addPlayer(player);
            }
        }
    }

    public void removeBossBar(UUID uuid) {
        TBBLogger.getInstance().debug("removeBossBar",String.format("Removing Boss Bar for UUID [%s]",uuid));
        synchronized (this.bossBars) {
            ServerBossEvent bossBar = this.bossBars.remove(uuid);
            if (bossBar != null) {
                bossBar.removeAllPlayers();
            }
        }
    }

    public void removeAllBossBars(){
        //TBBLogger.getInstance().warn("RemoveAllBossBars","Removing all boss bars!");
        synchronized (this.bossBars) {
            this.bossBars.clear();
        }
    }
    public void onDimensionChange(ServerPlayer player) {
        // Handle dimension change events for boss bars
    }

    public Collection<ServerPlayer> getPlayers(UUID giantId) {
        synchronized (this.bossBars) {
            ServerBossEvent bossBar = this.bossBars.get(giantId);
            return bossBar != null ? bossBar.getPlayers() : Collections.emptyList();
        }
    }

    public void setVisible(UUID giantId, boolean visible) {
        synchronized (this.bossBars) {
            ServerBossEvent bossBar = this.bossBars.get(giantId);
            if (bossBar != null) {
                bossBar.setVisible(visible);
                // If the visibility is set to false, you may also want to remove all players
                if (!visible) {
                    bossBar.removeAllPlayers();
                }
            }
        }
    }


    public void setBossBarName(UUID giantId, Component name) {
        synchronized (this.bossBars) {
            ServerBossEvent bossBar = this.bossBars.get(giantId);
            if (bossBar != null) {
                bossBar.setName(name);
            }
        }
    }

    public Collection<ServerBossEvent> getBossBarsForPlayer(UUID playerUUID) {
        List<ServerBossEvent> playerBossBars = new ArrayList<>();
        synchronized (this.bossBars) {
            for (ServerBossEvent bossBar : this.bossBars.values()) {
                if (bossBar.getPlayers().stream().anyMatch(player -> player.getUUID().equals(playerUUID))) {
                    playerBossBars.add(bossBar);
                }
            }
        }
        return playerBossBars;
    }

    // Additional methods as needed...
}
