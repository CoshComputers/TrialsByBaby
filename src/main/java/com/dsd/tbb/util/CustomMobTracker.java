package com.dsd.tbb.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomMobTracker {
    // ConcurrentHashMap to hold the tracking data in a thread-safe manner
    private ConcurrentHashMap<UUID, TrackedMob> mobs = new ConcurrentHashMap<>();
    // ConcurrentHashMap to hold the count of mobs per region
    private final ConcurrentHashMap<String, AtomicInteger> regionMobCount = new ConcurrentHashMap<>();

    // The single instance of CustomMobTracker
    private static CustomMobTracker instance;

    // Private constructor prevents instantiation from other classes
    private CustomMobTracker() { }

    // The method to access the single instance of CustomMobTracker
    public static synchronized CustomMobTracker getInstance() {
        if (instance == null) {
            instance = new CustomMobTracker();
        }
        return instance;
    }
    public int getSize(){
        return mobs.size();
    }
    public void addMob(Entity mob) {
        // Create a new TrackedMob instance and put it in the map
        TrackedMob trackedMob = new TrackedMob(mob);
        mobs.put(trackedMob.entityId, trackedMob);
        incrementRegionMobCount(trackedMob.dimension, trackedMob.regionX, trackedMob.regionZ);
    }

    public void removeMob(UUID entityId) {
        TrackedMob trackedMob = mobs.remove(entityId);
        if (trackedMob != null) {
            decrementRegionMobCount(trackedMob.dimension, trackedMob.regionX, trackedMob.regionZ);
        }
    }

    public synchronized void updateMobPosition(UUID entityId, ChunkPos pos) {
        // Get the TrackedMob instance from the map
        TrackedMob trackedMob = mobs.get(entityId);
        if(trackedMob != null) {
            trackedMob.regionX = pos.getRegionLocalX();
            trackedMob.regionZ = pos.getRegionLocalZ();
        }
    }

    public synchronized void updateMobDimension(UUID entityId, ResourceKey<Level> dimension) {
        // Get the TrackedMob instance from the map
        TrackedMob trackedMob = mobs.get(entityId);
        if(trackedMob != null) {
            trackedMob.dimension = dimension;
        }
    }

    private String getRegionKey(ResourceKey<Level> dimension, int regionX, int regionZ) {
        return dimension.toString() + "_" + regionX + "_" + regionZ;
    }

    private void incrementRegionMobCount(ResourceKey<Level> dimension, int regionX, int regionZ) {
        String key = getRegionKey(dimension, regionX, regionZ);
        regionMobCount.merge(key, new AtomicInteger(1), (existing, newValue) -> {
            existing.incrementAndGet();
            return existing;
        });
    }

    private void decrementRegionMobCount(ResourceKey<Level> dimension, int regionX, int regionZ) {
        String key = getRegionKey(dimension, regionX, regionZ);
        regionMobCount.computeIfPresent(key, (k, count) -> {
            count.decrementAndGet();
            return count.get() > 0 ? count : null;
        });
    }

    public int getMobCountInRegion(ResourceKey<Level> dimension, int regionX, int regionZ) {
        String key = getRegionKey(dimension, regionX, regionZ);
        AtomicInteger count = regionMobCount.get(key);
        return count != null ? count.get() : 0;
    }



    public String getAllMobsInfo() {
        StringBuilder sb = new StringBuilder();
        for (TrackedMob mob : mobs.values()) {
            sb.append(mob.toString()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    // Private inner class TrackedMob, accessible only through CustomMobTracker
    private static class TrackedMob {
        public final UUID entityId;
        public ResourceKey<Level> dimension;
        public int regionX;
        public int regionZ;
        public final EnumTypes.CustomMobTypes type;

        public TrackedMob(Entity mob) {
            ChunkPos tempPos = new ChunkPos(mob.blockPosition());
            this.entityId = mob.getUUID();
            this.dimension = mob.level.dimension();
            this.regionX = tempPos.getRegionLocalX();
            this.regionZ = tempPos.getRegionLocalZ();
            this.type = EnumTypes.CustomMobTypes.BABYZOMBIE;

            /*if(mob instanceof TrialsByBabyZombie){

            }else{
                if (mob instanceof TrialsByGiantZombie) {
                    this.type = EnumTypes.CustomMobTypes.GIANTZOMBIE;
                }
            }*/
        }

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append("Type [").append(this.type.name()).append("], ");
            sb.append("Mob UUID [").append(this.entityId).append("], ");
            sb.append("Dimension [").append(this.dimension.toString()).append("], ");
            sb.append("Region Position [").append(this.regionX).append("],[").append(this.regionZ).append("]");
            return sb.toString();

        }
    }
    // ... Other methods to add, remove, and query mobs, and update their data ...
}
