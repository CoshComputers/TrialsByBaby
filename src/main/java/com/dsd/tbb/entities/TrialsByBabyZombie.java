package com.dsd.tbb.entities;

import com.dsd.tbb.util.EnumTypes;
import com.dsd.tbb.rulehandling.RuleManager;
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

    public TrialsByBabyZombie(EntityType type, Level world) {

        super(type,world);
        this.setBaby(true);
        this.setAppearance(RuleManager.getInstance().determineAppearance(world));
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
                break;
        }
        this.setItemSlot(EquipmentSlot.HEAD, head);
        this.setItemSlot(EquipmentSlot.MAINHAND, handItem);
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