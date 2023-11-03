package com.dsd.tbb.customs.goals;

import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.util.EnumTypes;
import com.dsd.tbb.util.ModUtilities;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import software.bernie.geckolib.constant.DefaultAnimations;

public class GiantCombatControllerGoal extends Goal {
    private final TrialsByGiantZombie giant;

    // Define a static DamageSource instance
    private static final DamageSource GIANT_DAMAGE_SOURCE = new DamageSource((Holder<DamageType>) DamageTypes.MOB_ATTACK);

    private int attackCooldown = 0;

    public GiantCombatControllerGoal(TrialsByGiantZombie giant) {
        this.giant = giant;
    }

    @Override
    public boolean canUse() {
        return giant.getTarget() != null && attackCooldown == 0;  // Only use this goal when there's a target and the cooldown is zero
    }

    @Override
    public void start() {
        EnumTypes.GiantAttackType attackType = EnumTypes.GiantAttackType.values()[ModUtilities.nextInt(EnumTypes.GiantAttackType.values().length)];
        giant.setAttackType(attackType);
        if(attackType == EnumTypes.GiantAttackType.MELEE) {
            attackCooldown = 20; //cooldown shorter for melee attack
        }else attackCooldown = 200; //Charge and Smack down have a longer cooldown.
        triggerAttackAnimation(attackType);
    }

    @Override
    public void tick() {
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        applyDamageBasedOnAttackType(giant.getAttackType());
    }

    private void triggerAttackAnimation(EnumTypes.GiantAttackType attackType) {
        switch (attackType) {
            case MELEE:
                giant.playAnimation(DefaultAnimations.ATTACK_STRIKE);
                break;
            case SMACKDOWN:
                giant.playAnimation(DefaultAnimations.SMACK_DOWN);
                break;
            case CHARGE:
                giant.playAnimation(DefaultAnimations.CHARGE);
                break;
        }
    }

    private void applyDamageBasedOnAttackType(EnumTypes.GiantAttackType attackType) {
        LivingEntity target = giant.getTarget();
        if (target != null) {
            double distance = giant.distanceTo(target);
            switch (attackType) {
                case MELEE:
                    if (distance < 2.0D) {
                        target.hurt(GIANT_DAMAGE_SOURCE, 10.0F);  // Example damage value
                    }
                    break;
                case SMACKDOWN:
                    if (distance < 5.0D) {
                        target.hurt(GIANT_DAMAGE_SOURCE, 20.0F);  // Example damage value
                    }
                    break;
                case CHARGE:
                    if (distance < 3.0D) {
                        target.hurt(GIANT_DAMAGE_SOURCE, 15.0F);  // Example damage value
                    }
                    break;
            }
        }
    }
}
