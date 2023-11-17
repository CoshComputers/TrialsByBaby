package com.dsd.tbb.customs.renderers;

import com.dsd.tbb.customs.entities.endgiant.EndGiant;
import com.dsd.tbb.customs.models.EndGiantModel;
import com.dsd.tbb.main.TrialsByBaby;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class EndGiantRenderer extends GeoEntityRenderer<EndGiant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TrialsByBaby.MOD_ID, "textures/endgiant.png");

    public EndGiantRenderer(EntityRendererProvider.Context context) {
        super(context, new EndGiantModel());
    }

    @Override
    public ResourceLocation getTextureLocation(EndGiant entity) {
        return TEXTURE;
    }
}