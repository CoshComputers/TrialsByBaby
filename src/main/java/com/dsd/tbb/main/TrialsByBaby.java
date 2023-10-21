package com.dsd.tbb.main;


import com.dsd.tbb.util.CustomLogger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(TrialsByBaby.MOD_ID)
public class TrialsByBaby
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "trialsbybaby";
    // Directly reference a slf4j logger
    private static final CustomLogger LOGGER = CustomLogger.getInstance();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
    public TrialsByBaby()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        //registerEntities();
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }
	/*
    public static void registerEntities() {
        ENTITIES.register("baby_zombie", () -> EntityType.Builder.of((type, world) -> new CreatorTrialsBabyZombie(type, world), MobCategory.MONSTER)
                .sized(0.6F, 1.5F)
                .build(new ResourceLocation("minecraft:zombie").toString()));
    }*/

}