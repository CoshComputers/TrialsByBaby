package com.dsd.tbb.customs.entities;

import com.dsd.tbb.config.InitialGearConfig;
import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.util.ModUtilities;
import com.dsd.tbb.managers.PlayerManager;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class PlayerEntityHelper {
    private static final PlayerManager playerManager = PlayerManager.getInstance();
    private static final ConfigManager configManager = ConfigManager.getInstance();
    private PlayerEntityHelper(){};

    public static void givePlayerGear(Player player){
        if(playerManager == null || configManager == null){
            TBBLogger.getInstance().error("givePlayerGear","Something has gone wrong with PlayerManager or ConfigManager. No action taken");
            return;
        }

        for(InitialGearConfig.GearItem gearItem : configManager.getGearConfig().getInitialGear()){
            ResourceLocation itemResourceLocation = new ResourceLocation(gearItem.getItem());
            Item item = ForgeRegistries.ITEMS.getValue(itemResourceLocation);
            //TBBLogger.getInstance().bulkLog("givePlayerGear",String.format("Gear Item to Give:\n[%s]",gearItem.toString()));
            if (item == null | item == Items.AIR) {
                TBBLogger.getInstance().error("givePlayerGear",String.format("Item not found: %s", itemResourceLocation));
            } else {
                ItemStack stack = new ItemStack(item);
                Map<Enchantment, Integer> enchantments = new HashMap<>();
                for (Map.Entry<String, Integer> enchantmentEntry : gearItem.getEnchantments().entrySet()) {
                    ResourceLocation enchantmentResourceLocation = new ResourceLocation(enchantmentEntry.getKey());
                    Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentResourceLocation);
                    if (enchantment != null) {
                        int validLevel = ModUtilities.getValidEnchantmentLevel(enchantment, enchantmentEntry.getValue());
                        enchantments.put(enchantment, enchantmentEntry.getValue());
                    }
                }
                EnchantmentHelper.setEnchantments(enchantments, stack);
                addGearToPlayer(player,stack);
            }
        }
    }

    private static void addGearToPlayer(Player player,ItemStack stack){
        Item item = stack.getItem();
        EquipmentSlot slotType = null;
        if(item instanceof ArmorItem){
            ArmorItem armorItem = (ArmorItem) item;
            EquipmentSlot eqp = armorItem.getEquipmentSlot();
            if(eqp == null){
                TBBLogger.getInstance().error("addGearToPlayer",String.format("Armour Item [%s] Has no Slot, ignoring Item",armorItem.getName(stack)));
                return;
            }
            switch (eqp) {
                case HEAD:
                    slotType = EquipmentSlot.HEAD;
                    break;
                case CHEST:
                    slotType = EquipmentSlot.CHEST;
                    break;
                case LEGS:
                    slotType = EquipmentSlot.LEGS;
                    break;
                case FEET:
                    slotType = EquipmentSlot.FEET;
                    break;
            }
        }
        if(slotType != null) {
            player.setItemSlot(slotType,stack);
        }else{
            player.getInventory().add(stack);
        }

    }


}
