package com.dsd.tbb.handlers;

import com.dsd.tbb.main.TrialsByBaby;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilEventHandler {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft(); // The item in the left slot
        ItemStack right = event.getRight(); // The enchanted book in the right slot
        int level = 1;
        if (left.isEmpty() || right.isEmpty() || !right.is(Items.ENCHANTED_BOOK)) {
            return; // If either slot is empty or the right slot isn't an enchanted book, do nothing.
        }

        Map<Enchantment, Integer> leftItemEnchants = EnchantmentHelper.getEnchantments(left);
        Map<Enchantment, Integer> rightItemEnchants = EnchantmentHelper.getEnchantments(right);

        // Check for high-level enchantments in the book
        boolean hasHighLevelEnchants = rightItemEnchants.entrySet().stream()
                .anyMatch(entry -> entry.getValue() > entry.getKey().getMaxLevel());


        if (hasHighLevelEnchants) {
            // Apply the enchantments from the book to the left slot item
            for (Map.Entry<Enchantment, Integer> entry : rightItemEnchants.entrySet()) {
                Enchantment enchantment = entry.getKey();
                level = entry.getValue();

                // If the left item already has the enchantment, check if the level should be increased.
                if (leftItemEnchants.containsKey(enchantment)) {
                    // If the book's level is higher, apply it.
                    if (leftItemEnchants.get(enchantment) < level) {
                        leftItemEnchants.put(enchantment, level);
                    }
                    // If the book's level is equal or lower, ignore it.
                } else {
                    // If the left item does not have the enchantment, apply it.
                    leftItemEnchants.put(enchantment, level);
                }
            }

            ItemStack result = left.copy();
            EnchantmentHelper.setEnchantments(leftItemEnchants, result);
            event.setOutput(result);

            boolean wasRenamed = !left.getDisplayName().getString().equals(result.getDisplayName().getString());

            event.setCost(calculateEnchantmentCost(leftItemEnchants,wasRenamed,level)); // Set the experience cost to 30 levels as an example
            event.setMaterialCost(1); // Set the material cost to 1 as an example
        }
    }

    public static int calculateEnchantmentCost(Map<Enchantment, Integer> enchantments, boolean wasRenamed, int enchantLevel) {
        //EnchantmentCost = prior work penalty + renaming cost + durability cost
        //                  + enchantment cost

        //Prior work penalty is based on the Anvil usages the item has been through.
        //The formula is:  (prior use penalty) = 2^(Anvil use count) - 1
        int anvilUsage = 1;
        double priorWorkPenalty = Math.pow(anvilUsage,2)-1;

        //No repair cost at present as adding a book does not force a repair.
        int repairCost = 0; //here for future and reference

        //Renaming Cost - if the item is being renamed - cost of 1 xp
        int renamingCost = wasRenamed? 1:0;

        //For compatability there is none as it's a book! Enchantment cost: This is the "level" on the book * a multiplier
        //The Multiplier depends on the enchantment, eg: Protection is 1, Thorns is 4. So protection 10 would be 10
        int enchantmentCost = 0;
        for (Enchantment enchantment : enchantments.keySet()) {
           // TBBLogger.getInstance().debug("Calculate Cost", "Rarity = " + enchantment.getRarity().getWeight());
            int nextEnchantCost = (enchantLevel * enchantment.getRarity().getWeight());
            enchantmentCost += Math.min(nextEnchantCost, 15);
        }

       // TBBLogger.getInstance().debug("Calculate Cost",String.format("Priory Use [%f], Repair Cost [%d], Rename Cost [%d], Enchantment Cost [%d]",
       //         priorWorkPenalty,repairCost,renamingCost,enchantmentCost));
        // Calculate the total cost
       return (int) (priorWorkPenalty + + repairCost + renamingCost + enchantmentCost);
    }
}
