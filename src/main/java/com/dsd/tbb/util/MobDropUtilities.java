package com.dsd.tbb.util;

import com.dsd.tbb.config.MobDropConfig;
import com.dsd.tbb.entities.TrialsByBabyZombie;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MobDropUtilities {

    public static ItemStack getDropForBabyZombie(TrialsByBabyZombie babyZombie) {
        MobDropConfig mobDropConfig = ConfigManager.getInstance().getBabyConfig();
        for (MobDropConfig.AppearanceConfig appearanceConfig : mobDropConfig.getMobDropConfig()) {
            if (appearanceConfig.getAppearance().equals(babyZombie.getAppearance().name())) {
                //TBBLogger.getInstance().bulkLog("getDrop",String.format("Getting Drops for [%s] Type",
                //        babyZombie.getAppearance().name()));
                return chooseDrop(appearanceConfig.getDrops());
            }
        }
        return ItemStack.EMPTY;  // Return an empty ItemStack if no matching appearance is found
    }

    private static ItemStack chooseDrop(List<MobDropConfig.DropConfig> drops) {
        for (MobDropConfig.DropConfig dropConfig : drops) {
            //TBBLogger.getInstance().bulkLog("chooseDrop",String.format("Drop Check: [%s]",drops));
            if (Math.random() < dropConfig.getRarity()) {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(dropConfig.getItem()));
                int quantity = (int) (Math.random() * (dropConfig.getMax() - dropConfig.getMin() + 1)) + dropConfig.getMin();
                return new ItemStack(item, quantity);
            }
        }
        return ItemStack.EMPTY;  // Return an empty ItemStack if no drop is chosen due to rarity
    }
}
