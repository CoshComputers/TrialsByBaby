package com.dsd.tbb.customs.goals;

import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.util.EnumTypes;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class GiantCombatControllerGoal extends MeleeAttackGoal {
    private final TrialsByGiantZombie giant;
    private int chargeCooldown = 0;
    private int roarCooldown = 0;
    private int smashCooldown = 0;
    private int animationTickTimer = 0;
    private double originalSpeed;
    private boolean isFirstEncounter = true;
    private EnumTypes.GiantAttackType attackType = EnumTypes.GiantAttackType.CHARGE;
    private boolean chargeActive = false;
    private boolean slamActive = false;
    private boolean meleeActive = false;
    private boolean walkingActive = true;

    public GiantCombatControllerGoal(TrialsByGiantZombie giant) {
        super(giant, 1.0, true);
        this.originalSpeed = giant.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
        this.giant = giant;
        this.attackType = EnumTypes.GiantAttackType.NONE;

    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public boolean canUse() {
        this.isFirstEncounter = true;
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        this.isFirstEncounter = false;
        return super.canContinueToUse();
    }

    @Override
    public void stop() {
        super.stop();
    }

    /*@Override
    public void tick() {
        if (!chargeActive && !slamActive) {
            if (animationTickTimer <= 0) {
                int randomValue = ModUtilities.nextInt(100);
                giant.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.originalSpeed);
                this.giant.triggerAnim("mainController", "walk");

                if (chargeCooldown > 0) chargeCooldown--;
                if (smashCooldown > 0) smashCooldown--;

                if (isFirstEncounter) {
                    //TBBLogger.getInstance().debug("goal tick", "First Encounter");
                    attackType = EnumTypes.GiantAttackType.CHARGE;
                    checkAndPerformAttack(this.mob.getTarget(),10);

                } else if (chargeCooldown <= 0 && randomValue < 40) {
                    //TBBLogger.getInstance().debug("goal tick", "Charge Selected");
                    attackType = EnumTypes.GiantAttackType.CHARGE;
                    checkAndPerformAttack(this.mob.getTarget(),10);

                } else if (smashCooldown <= 0 && randomValue < 80) {
                    // TBBLogger.getInstance().debug("goal tick", "Smash Selected");
                    attackType = EnumTypes.GiantAttackType.SMACKDOWN;
                    checkAndPerformAttack(this.mob.getTarget(),10);

                } else {
                    // TBBLogger.getInstance().debug("goal tick", "Melee Selected");
                    attackType = EnumTypes.GiantAttackType.MELEE;
                    checkAndPerformAttack(this.mob.getTarget(),10);
                    super.tick();
                }
            } else {
                checkAndPerformAttack(this.mob.getTarget(),10);
            }
        } else {
            if (chargeActive) { //charge animation in progress
                if (roarCooldown > 0) {
                    roarCooldown--;
                } else {
                    super.tick();
                }
            } else { //Slam Animation in progress
                checkAndPerformSmackdown();
                super.tick();
            }
        }
    }*/

    @Override
    public void tick(){

        //Do NOT Tick if SLAM is active or Roar Animation playing
        if(slamActive || roarCooldown > 0){
            if(roarCooldown > 0) roarCooldown --;
            checkAndPerformAttack(this.mob.getTarget(),0);
        }else{ // charge (but not roaring) or melee could be active or no attack happening

        }


    }

    @Override
    protected void checkAndPerformAttack(LivingEntity attackTarget, double distanceCanPerform) {
        double giantReach = this.getAttackReachSqr(attackTarget);
        double giantSmackDistance = 5;
        double giantChargeDistance = 10;

        double d0 = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(attackTarget);
        TBBLogger.getInstance().debug("checkAnd",String.format(" Attack Type [%s]- D to player [%f] Giant Reach [%f] Ani Timer [%d]",
                this.attackType.name(),d0,giantReach,animationTickTimer));

        //no attack currently active
        if(this.attackType == EnumTypes.GiantAttackType.NONE){
            if(d0 > giantChargeDistance){
                //out of reach, so if not already walking, set walking
                if(!walkingActive) {
                    giant.triggerAnim("mainController", "walk");
                    walkingActive = true;
                }
            }
        }else if (this.attackType == EnumTypes.GiantAttackType.CHARGE && (d0 <= giantChargeDistance || chargeActive)) {
            walkingActive = false;
            checkAndPerformCharge();
        } else if (this.attackType == EnumTypes.GiantAttackType.SMACKDOWN && (d0 <= giantSmackDistance || slamActive)) {
            walkingActive = false;
            checkAndPerformSmackdown();
        } else if (this.attackType == EnumTypes.GiantAttackType.MELEE && (d0 <= (giantReach+1) || meleeActive)) {
            walkingActive = false;
            checkAndPerformMelee();
        }else if (d0 > giantChargeDistance){
            //to far out for an attack and none are active:
        }
    }

    private void checkAndPerformMelee() {
        TBBLogger.getInstance().debug("perform Melee", String.format("Active [%s], Tick Timer [%d]",
                meleeActive ? "true" : "false", animationTickTimer));
        if (!meleeActive) {
            meleeActive = true;
            giant.triggerAnim("mainController", "melee");
            animationTickTimer = 10;
        } else {
            if (animationTickTimer > 0) {
                animationTickTimer--;
            } else {
                giant.doHurtTarget(this.mob.getTarget());
                giant.setLastHurtMob(this.mob.getTarget());
                meleeActive = false;
            }
        }

    }

    private void checkAndPerformSmackdown() {
        if (!slamActive) {
            giant.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
            giant.triggerAnim("mainController", "attack_smash");
            slamActive = true;
            smashCooldown = 100;
            animationTickTimer = 25;
        } else {
            if (animationTickTimer > 0) {
                animationTickTimer--;
            } else {
                giant.doHurtTarget(this.mob.getTarget());
                giant.setLastHurtMob(this.mob.getTarget());
                slamActive = false;
            }
        }
    }


    private void checkAndPerformCharge() {
        if (!chargeActive) {
            giant.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
            giant.triggerAnim("mainController", "attack_charge");
            // giant.getLevel().playSound(null, giant.getX(), giant.getY(), giant.getZ(), ModEventHandlers.GIANT_ROAR,
            //         SoundSource.NEUTRAL, 1.0F, 1.0F);
            chargeCooldown = 600;
            roarCooldown = 50;
            animationTickTimer = 10;
            chargeActive = true;
        } else {
            if (animationTickTimer > 0) {
                giant.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.originalSpeed * 3);
                animationTickTimer--;
            } else {
                giant.doHurtTarget(this.mob.getTarget());
                giant.setLastHurtMob(this.mob.getTarget());
                chargeActive = false;
            }
        }
    }

   /* private boolean canDoNextAttack() {
        if (animationTickTimer > 0) {
            animationTickTimer--;
            return false;
        } else {
            return true;
        }
    }*/

}
