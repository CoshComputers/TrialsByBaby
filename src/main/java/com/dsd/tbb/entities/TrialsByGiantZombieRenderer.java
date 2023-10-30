package com.dsd.tbb.entities;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrialsByGiantZombieRenderer extends ZombieRenderer {
    public TrialsByGiantZombieRenderer(EntityRendererProvider.Context context) {
        super(context);
    }




}
