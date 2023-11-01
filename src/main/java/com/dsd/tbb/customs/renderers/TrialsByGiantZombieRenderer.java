package com.dsd.tbb.customs.renderers;

import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.customs.models.TrialsByGiantZombieModel;
import com.dsd.tbb.main.TrialsByBaby;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class TrialsByGiantZombieRenderer extends GeoEntityRenderer<TrialsByGiantZombie> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TrialsByBaby.MOD_ID, "textures/giantnewtexture.png");

    public TrialsByGiantZombieRenderer(EntityRendererProvider.Context context) {
        super(context,new TrialsByGiantZombieModel());
    }

    @Override
    public ResourceLocation getTextureLocation(TrialsByGiantZombie entity) {
        return TEXTURE;
    }


}
