package com.dsd.tbb.customs.goals;

import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

public class GiantWanderGoal extends RandomStrollGoal {
    private final TrialsByGiantZombie giant;

    public GiantWanderGoal(TrialsByGiantZombie giant, double speed) {
        super(giant, speed);
        this.giant = giant;
    }

    @Override
    public void tick() {
        super.tick();
        giant.setWalking(giant.isPathFinding());

    }

}
