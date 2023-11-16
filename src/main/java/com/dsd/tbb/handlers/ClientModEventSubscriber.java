package com.dsd.tbb.handlers;

import com.dsd.tbb.customs.renderers.RisingBlockRenderer;
import com.dsd.tbb.customs.renderers.TrialsByGiantZombieRenderer;
import com.dsd.tbb.main.TrialsByBaby;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;



@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventSubscriber {

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(ModEventHandlers.TRIALS_BY_GIANT_ZOMBIE.get(), TrialsByGiantZombieRenderer::new);
        });

        event.enqueueWork(() -> {
            EntityRenderers.register(ModEventHandlers.TRIALS_BY_BABY_ZOMBIE.get(), ZombieRenderer::new);
        });

        event.enqueueWork(() -> {
            EntityRenderers.register(ModEventHandlers.RISING_BLOCK_ENTITY.get(), RisingBlockRenderer::new);
        });
    }

    // ... other client-side event handlers ...
}
