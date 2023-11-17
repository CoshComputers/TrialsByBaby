package com.dsd.tbb.handlers;

import com.dsd.tbb.customs.entities.endgiant.EndGiant;
import com.dsd.tbb.customs.entities.general.RisingBlockEntity;
import com.dsd.tbb.customs.entities.general.TrialsByBabyZombie;
import com.dsd.tbb.customs.entities.general.TrialsByGiantZombie;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.rulehandling.RuleManager;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandlers {

    ;

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TrialsByBaby.MOD_ID);

    public static final RegistryObject<EntityType<TrialsByBabyZombie>> TRIALS_BY_BABY_ZOMBIE = ENTITIES.register(
            "trials_by_baby_zombie",
            () -> EntityType.Builder.of((EntityType<TrialsByBabyZombie> type, Level world) -> new TrialsByBabyZombie(type, world), MobCategory.MONSTER)
                    .sized(0.6F, 1.5F)
                    .build(new ResourceLocation(TrialsByBaby.MOD_ID, "trials_by_baby_zombie").toString())
    );

    public static final RegistryObject<EntityType<TrialsByGiantZombie>> TRIALS_BY_GIANT_ZOMBIE = ENTITIES.register(
            "trials_by_giant_zombie",
            () -> EntityType.Builder.of((EntityType<TrialsByGiantZombie> type, Level world)
                            -> new TrialsByGiantZombie(type, world), MobCategory.MONSTER)
                    .sized(1.2F, 3.2F)
                    .build(new ResourceLocation(TrialsByBaby.MOD_ID, "trials_by_giant_zombie").toString())
    );

    public static final RegistryObject<EntityType<EndGiant>> END_GIANT = ENTITIES.register(
            "end_giant",
            () -> EntityType.Builder.of((EntityType<EndGiant> type, Level world)
                    -> new EndGiant(type, world), MobCategory.MONSTER)
                    .sized(3.2F, 14.6F) // Adjust the size according to your needs
                    .build(new ResourceLocation(TrialsByBaby.MOD_ID, "end_giant").toString())
    );

    public static final RegistryObject<EntityType<RisingBlockEntity>> RISING_BLOCK_ENTITY = ENTITIES.register(
            "rising_block_entity",
            () -> EntityType.Builder.<RisingBlockEntity>of(RisingBlockEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F) // Adjust the size according to your needs
                    .build(new ResourceLocation(TrialsByBaby.MOD_ID, "rising_block_entity").toString())
    );


    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) throws InstantiationException, IllegalAccessException {
        TBBLogger.getInstance().info("commonSetup","Trials By Baby has started");
        ConfigManager.getInstance();
        TBBLogger.getInstance().info("commonSetup","Configs Set to Defaults");
        //TBBLogger.getInstance().debug("commonSetup",ConfigManager.getInstance().getTrialsConfig().toString());
        RuleManager.getInstance();
        TBBLogger.getInstance().info("commonSetup","RuleManager initialized");
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(TRIALS_BY_GIANT_ZOMBIE.get(), TrialsByGiantZombie.createAttributes().build());
        event.put(TRIALS_BY_BABY_ZOMBIE.get(), TrialsByBabyZombie.createAttributes().build());
        event.put(END_GIANT.get(),EndGiant.createAttributes().build());
    }



}

