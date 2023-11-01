package com.dsd.tbb.customs.models;

import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.main.TrialsByBaby;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TrialsByGiantZombieModel extends GeoModel<TrialsByGiantZombie> {
    private final ResourceLocation modelResource = new ResourceLocation(TrialsByBaby.MOD_ID, "geo/trialsbygiantzombie.geo.json");
    private final ResourceLocation textureResource = new ResourceLocation(TrialsByBaby.MOD_ID, "textures/giantnewtexture.png");

    private final ResourceLocation animationResource = new ResourceLocation(TrialsByBaby.MOD_ID, "animations/trialsbygiantzombie.animation.json");
    @Override
    public ResourceLocation getModelResource(TrialsByGiantZombie animatable) {
        return this.modelResource;
    }

    @Override
    public ResourceLocation getTextureResource(TrialsByGiantZombie animatable) {
        return this.textureResource;
    }

    @Override
    public ResourceLocation getAnimationResource(TrialsByGiantZombie animatable) {
        return this.animationResource;
    }
}
