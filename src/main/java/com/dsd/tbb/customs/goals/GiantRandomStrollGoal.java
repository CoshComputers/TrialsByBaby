package com.dsd.tbb.customs.goals;

import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

public class GiantRandomStrollGoal extends RandomStrollGoal {
    private TrialsByGiantZombie giant;
    public GiantRandomStrollGoal(PathfinderMob entity, double p_25735_) {
        super(entity, p_25735_);
        this.giant = (TrialsByGiantZombie) entity;
    }


    @Override
    public boolean canUse() {
        boolean canUse = super.canUse();
        if(canUse) giant.triggerAnim("mainController","walk");
        return canUse;
    }

    @Override
    public boolean canContinueToUse() {
        boolean canContinue = super.canContinueToUse();

        return canContinue;
    }

    @Override
    public void start() {
        super.start();
        giant.triggerAnim("mainController","walk");
    }

    @Override
    public void stop() {
        giant.triggerAnim("mainController","idle");
        super.stop();
    }
}
