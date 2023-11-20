package com.dsd.tbb.customs.entities.endgiant;

import com.dsd.tbb.util.TBBLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class EndGiantPart  extends net.minecraftforge.entity.PartEntity<EndGiant>{
    // Synced data parameters
    private static final EntityDataAccessor<Vector3f> PART_POSITION = SynchedEntityData.defineId(EndGiantPart.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Vector3f> PART_ROTATION = SynchedEntityData.defineId(EndGiantPart.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Vector3f> PART_SIZE = SynchedEntityData.defineId(EndGiantPart.class, EntityDataSerializers.VECTOR3);

    public final EndGiant parentMob;
    public final String name;
    private final EntityDimensions size;

    private int testTickCountdown = 20;

    public EndGiantPart(EndGiant parent, String partName, float width, float height) {
        super(parent);
        this.parentMob = parent;
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.name = partName;

    }


    @Override
    public void tick() {
        super.tick();

        if(testTickCountdown <= 0){
            TBBLogger.getInstance().debug("Ticking Part",String.format("Bounding Box at: [%f][%f][%f]",
                    this.getBoundingBox().minX,this.getBoundingBox().minY,this.getBoundingBox().minZ));
            testTickCountdown = 20;
        }else{
            testTickCountdown --;
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag dToRead) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag dToSave) {

    }
    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        return this.parentMob.getPickResult();
    }

    @Override
    public boolean hurt(DamageSource damageSource, float dAmount) {
            TBBLogger.getInstance().debug("Part Hit", this.name + " was hit");
            return this.parentMob.hurt(this, damageSource, dAmount);
    }

    public boolean is(Entity p_31031_) {
        return this == p_31031_ || this.parentMob == p_31031_;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    public EntityDimensions getDimensions(Pose p_31023_) {
        return this.size;
    }

    public boolean shouldBeSaved() {
        return false;
    }


    /******************************************** GETTERS / SETTERS *********************************************/
    public String getPartName() {
        return name;
    }
}
