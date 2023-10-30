package com.dsd.tbb.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ZombieModel;

public class TrialsByGiantZombieModel extends ZombieModel<TrialsByGiantZombie> {
    public TrialsByGiantZombieModel() {
        super();
        // Initialize model parts here
    }

    @Override
    public void setupAnim(TrialsByGiantZombie entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Set up animations here
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Render model parts here
    }
}
