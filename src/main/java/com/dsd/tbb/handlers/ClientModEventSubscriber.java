package com.dsd.tbb.handlers;

import com.dsd.tbb.customs.renderers.TrialsByGiantZombieRenderer;
import com.dsd.tbb.main.TrialsByBaby;
import net.minecraft.client.renderer.entity.EntityRenderers;
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
    }

    // ... other client-side event handlers ...
}
