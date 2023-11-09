package com.dsd.tbb.customs.entities;

import com.dsd.tbb.util.EnumTypes;
import com.dsd.tbb.managers.PlayerManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TrialsByBabyZombie extends Zombie {
    public static final EntityDataAccessor<String> APPEARANCE = SynchedEntityData.defineId(TrialsByBabyZombie.class, EntityDataSerializers.STRING);
    public static final int MY_DEFAULT_HEIGHT = 1;

    /*public TrialsByBabyZombie(Level world){
        super(world);
        this.setBaby(true);
        setAppearance(EnumTypes.ZombieAppearance.REGULAR);
    }*/

    public TrialsByBabyZombie(EntityType type, Level world) {
        super(type,world);
        this.setBaby(true);
        //setAppearance(EnumTypes.ZombieAppearance.REGULAR);

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(APPEARANCE, EnumTypes.ZombieAppearance.REGULAR.name());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Add custom goals here
    }

    public void setAppearance(EnumTypes.ZombieAppearance appearance) {
        this.entityData.set(APPEARANCE, appearance.name());
        ItemStack head = ItemStack.EMPTY;
        ItemStack handItem = ItemStack.EMPTY;
        switch(appearance) {
            case BLAZE:  // Blaze head and blaze rod
                head = getSkull("MHF_Blaze");
                handItem = new ItemStack(Items.BLAZE_ROD);
                break;
            case ENDERMAN:  // Enderman head and ender pearl
                head = getSkull("MHF_Enderman");
                handItem = new ItemStack(Items.ENDER_PEARL);
                break;
            default:
                head = PlayerManager.getInstance().getRandomPlayer().getPlayerHead();
                break;
        }
        this.setItemSlot(EquipmentSlot.HEAD, head);
        this.setItemSlot(EquipmentSlot.MAINHAND, handItem);
    }

    public EnumTypes.ZombieAppearance getAppearance() {
        return EnumTypes.ZombieAppearance.valueOf(this.entityData.get(APPEARANCE));
    }

    private ItemStack getSkull(String skullOwner) {
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        CompoundTag tag = skull.getOrCreateTag();
        tag.putString("SkullOwner", skullOwner);
        return skull;
    }

    @Override
    public String toString() {
        return "CreatorTrialsBabyZombie{" +
                " appearance = " + this.getEntityData().get(TrialsByBabyZombie.APPEARANCE) +
                '}';
    }
}