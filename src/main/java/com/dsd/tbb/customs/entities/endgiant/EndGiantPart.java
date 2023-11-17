package com.dsd.tbb.customs.entities.endgiant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class EndGiantPart  extends net.minecraftforge.entity.PartEntity<EndGiant>{
    public final EndGiant parentMob;
    public final String name;
    private final EntityDimensions size;

    public EndGiantPart(EndGiant parent, String partName, float width, float height) {
        super(parent);
        this.parentMob = parent;
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.name = partName;
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

    public boolean hurt(DamageSource damageSource, float dAmount) {
        return this.isInvulnerableTo(damageSource) ? false : this.parentMob.hurt(this, damageSource, dAmount);
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
}
