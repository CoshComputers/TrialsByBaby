package com.dsd.tbb.main;


import com.dsd.tbb.handlers.ModEventHandlers;
import com.dsd.tbb.managers.ModSounds;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.ScheduledExecutorService;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(TrialsByBaby.MOD_ID)
public class TrialsByBaby
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "trialsbybaby";
    public static ScheduledExecutorService scheduler;
    public static MinecraftServer MOD_SERVER = null;
    public static final boolean MOD_IS_IN_TESTING = false;

    // Directly reference a slf4j logger
    private static final TBBLogger LOGGER = TBBLogger.getInstance();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public TrialsByBaby()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEventHandlers.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        // Register the commonSetup method for modloading
        //modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        ModSounds.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        //registerEntities();
        // Register ourselves for server and other game utils we are interested in
        MinecraftForge.EVENT_BUS.register(this);


    }


    public static void registerSounds() {

    }

    public static void registerEntities() {
        /*ENTITIES.register("trials_by_baby_zombie", () -> EntityType.Builder.of((type, world) -> new TrialsByBabyZombie(type, world), MobCategory.MONSTER)
                .sized(0.6F, 1.5F)
                .build(new ResourceLocation(MOD_ID, "trials_by_baby_zombie").toString()));

        ENTITIES.register("trials_by_giant_zombie", () -> EntityType.Builder.of((type, world) -> new TrialsByGiantZombie(type, world), MobCategory.MONSTER)
                .sized(1.2F, 1.9F)
                .build(new ResourceLocation(MOD_ID, "trials_by_giant_zombie").toString()));
*/


    }

    /************************************* HOOKS *************************************/
    public static void triggerShutdown() {
        MOD_SERVER.halt(false); // Gracefully stops the server
    }


    /************************************* GETTERS/SETTERS *************************************/

    public static void setServer(MinecraftServer server){
        MOD_SERVER = server;
    }

}
