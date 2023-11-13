package com.dsd.tbb.customs.goals;

import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.util.EnumTypes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

public class GiantRandomStrollGoal extends RandomStrollGoal {
    private TrialsByGiantZombie giant;

    public GiantRandomStrollGoal(PathfinderMob entity, double speedModifier, int interval, boolean checkNoActionTime) {
        super(entity, speedModifier, interval, checkNoActionTime);
        this.giant = (TrialsByGiantZombie) entity;
    }


    @Override
    public boolean canUse() {
        boolean canUse = super.canUse();
        if (canUse) {
            triggerWalking();
        }
        return canUse;
    }

    @Override
    public boolean canContinueToUse() {
        boolean canContinue = super.canContinueToUse();
        if(canContinue){
            if(giant.getState() != EnumTypes.GiantState.WALKING){
                triggerWalking();
            }
        }
        return canContinue;
    }

    @Override
    public void start() {
        super.start();
        triggerWalking();
    }

    @Override
    public void stop() {
        giant.triggerAnim("mainController","idle");
        giant.setState(EnumTypes.GiantState.IDLE);
        super.stop();
    }

    private void triggerWalking(){
        giant.triggerAnim("mainController", "walk");
        giant.setState(EnumTypes.GiantState.WALKING);
    }
}
