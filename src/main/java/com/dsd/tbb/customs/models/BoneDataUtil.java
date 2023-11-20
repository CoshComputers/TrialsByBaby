package com.dsd.tbb.customs.models;

import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;

public class BoneDataUtil {
    private Vec3 position;
    private Vec3 rotation;
    private Vec3 scale;
    private Double inflate; // Added inflate property

    // Constructor and other methods...
    public void BoneDataUtil(GeoBone geoBone){
        this.position = new Vec3(geoBone.getPosX(), geoBone.getPosY(), geoBone.getPosZ());
        this.rotation = new Vec3(geoBone.getRotX(), geoBone.getRotY(), geoBone.getRotZ());
        this.scale = new Vec3(geoBone.getScaleX(), geoBone.getScaleY(), geoBone.getScaleZ());
        this.inflate = geoBone.getInflate();
    }

    public void setBoneData(GeoBone geoBone) {
        this.position = new Vec3(geoBone.getPosX(), geoBone.getPosY(), geoBone.getPosZ());
        this.rotation = new Vec3(geoBone.getRotX(), geoBone.getRotY(), geoBone.getRotZ());
        this.scale = new Vec3(geoBone.getScaleX(), geoBone.getScaleY(), geoBone.getScaleZ());
        this.inflate = geoBone.getInflate();

    }

}
