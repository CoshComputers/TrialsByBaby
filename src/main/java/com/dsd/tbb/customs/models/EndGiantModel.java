package com.dsd.tbb.customs.models;

import com.dsd.tbb.customs.entities.endgiant.EndGiant;
import com.dsd.tbb.main.TrialsByBaby;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EndGiantModel extends GeoModel<EndGiant> {
    private final ResourceLocation modelResource = new ResourceLocation(TrialsByBaby.MOD_ID, "geo/endgiant.geo.json");
    private final ResourceLocation textureResource = new ResourceLocation(TrialsByBaby.MOD_ID, "textures/endgiant.png");
    private final ResourceLocation animationResource = new ResourceLocation(TrialsByBaby.MOD_ID, "animations/endgiant.animation.json");

    @Override
    public ResourceLocation getModelResource(EndGiant object) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureResource(EndGiant object) {
        return textureResource;
    }

    @Override
    public ResourceLocation getAnimationResource(EndGiant object) {
        return animationResource;
    }
}
