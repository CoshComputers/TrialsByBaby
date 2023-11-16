package com.dsd.tbb.customs.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RisingBlockEntity extends Entity {
    private BlockState mimickedBlockState;
    private int animationDuration = 100;
    private double maxHeight = 5.0;
    private int currentTick = 0;
    private boolean goingUp = true;

    public RisingBlockEntity(EntityType<RisingBlockEntity> risingBlockEntityEntityType, Level level) {
        super(risingBlockEntityEntityType, level);
        this.setNoGravity(true); // Disables gravity for this entity
    }

    @Override
    public void tick() {
        super.tick();

        if (currentTick < animationDuration) {
            double progress = (double) currentTick / animationDuration;
            double verticalSpeed;

            if (goingUp) {
                verticalSpeed = Math.sin(progress * Math.PI) * maxHeight / animationDuration;
            } else {
                verticalSpeed = -Math.sin(progress * Math.PI) * maxHeight / animationDuration;
            }

            this.setDeltaMovement(this.getDeltaMovement().x, verticalSpeed, this.getDeltaMovement().z);
            this.move(MoverType.SELF, this.getDeltaMovement());

            currentTick++;

            if (currentTick == animationDuration) {
                currentTick = 0;
                goingUp = !goingUp; // Change direction

                if (!goingUp) {
                    // Start going down
                    animationDuration *= 2; // Slower descent, for example
                } else {
                    // Reset for next cycle
                    animationDuration = 20;
                }
            }
        } else {
            this.remove(RemovalReason.DISCARDED); // Remove the entity after animation
        }
    }

    private boolean shouldRemove() {
        // Your logic to determine if the entity should be removed
        return false;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}



    public BlockState getMimickedBlockState() {
        return mimickedBlockState;
    }

    public void setMimickedBlockState(BlockState mimickedBlockState) {
        this.mimickedBlockState = mimickedBlockState;
    }
}
