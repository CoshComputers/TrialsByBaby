package com.dsd.tbb.entities;

import com.dsd.tbb.util.ConfigManager;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TrialsByGiantZombie extends Zombie {

    public static int MY_DEFAULT_HEIGHT;
    private int scaleFactor;

    public TrialsByGiantZombie(Level world){
        super(world);
        scaleFactor = ConfigManager.getInstance().getGiantConfig().getScaleFactor();
        MY_DEFAULT_HEIGHT = (int) Math.ceil(Zombie.DEFAULT_BB_HEIGHT*scaleFactor);
        this.setBoundingBox(this.getBoundingBox().inflate(scaleFactor - 1));

    }
    public TrialsByGiantZombie(EntityType type, Level world) {
        super(type,world);
    }

    public void moveToPosition(Vec3 playerPos, Direction playerFacing) {
        Vec3 offset = new Vec3(playerFacing.getStepX() * 5, 0, playerFacing.getStepZ() * 5);
        Vec3 newPos = playerPos.add(offset);
        this.setPos(newPos.x, newPos.y, newPos.z);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions baseDimensions = super.getDimensions(pose);
        return EntityDimensions.scalable(baseDimensions.width * scaleFactor, baseDimensions.height * scaleFactor);
    }
    @Override
    public float getEyeHeight(Pose pose) {
        float baseEyeHeight = super.getEyeHeight(pose);
        return baseEyeHeight * scaleFactor;
    }
}
