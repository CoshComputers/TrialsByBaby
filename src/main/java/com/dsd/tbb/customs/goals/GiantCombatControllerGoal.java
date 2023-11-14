package com.dsd.tbb.customs.goals;

import com.dsd.tbb.ZZtesting.loggers.TestEventLogger;
import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.util.EnumTypes;
import com.dsd.tbb.util.ModUtilities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class GiantCombatControllerGoal extends MeleeAttackGoal {
    private static final int G_CHARGE_DISTANCE = 100;
    private static final int G_SMASH_DISTANCE = 25;
    private final double G_REACH;
    private final TrialsByGiantZombie giant;
    private final double origDamageValue;
    private int chargeCooldown = 0;
    private int roarCooldown = 0;
    private int smashCooldown = 0;
    private int animationTickTimer = 0;
    private double originalSpeed;
    private boolean isFirstEncounter = true;
    private EnumTypes.GiantAttackType attackType;
    private boolean walking;

    public GiantCombatControllerGoal(TrialsByGiantZombie giant) {
        super(giant, 1.0, true);

        this.giant = giant;
        attackType = EnumTypes.GiantAttackType.NONE;
        walking = false;
        G_REACH = this.getAttackReachSqr(giant);

        this.originalSpeed = giant.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
        this.origDamageValue = ConfigManager.getInstance().getGiantConfig().getDamage();
        //TBBLogger.getInstance().debug("Construct Melee Goal","Damage Value = " + origDamageValue);

    }

    @Override
    public void start() {
        super.start();
        attackType = EnumTypes.GiantAttackType.NONE;
        giant.setState(EnumTypes.GiantState.ATTACKING_START);
        walking = false;
    }

    @Override
    public boolean canUse() {
        boolean canUse = super.canUse();
        if(canUse) {
            this.isFirstEncounter = true;
            giant.setState(EnumTypes.GiantState.ATTACKING_START);
        }
        return canUse;
    }

    @Override
    public boolean canContinueToUse() {
        boolean contToUse = super.canContinueToUse();
        if(contToUse){
            this.isFirstEncounter = false;
        }else{
            giant.setState(EnumTypes.GiantState.ATTACKING_STOPPING);

        }
        return contToUse;
    }

    @Override
    public void stop() {
        attackType = EnumTypes.GiantAttackType.NONE;
        giant.triggerAnim("mainController","idle");
        giant.setState(EnumTypes.GiantState.IDLE);
        super.stop();
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.mob.getTarget(), 30.0F, 30.0F);

        //if we are in a CHARGE:
        if (attackType == EnumTypes.GiantAttackType.CHARGE) {
            if (roarCooldown <= 0) { //tick so giant moves towards target
                giant.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.originalSpeed * 3);
                super.tick();
            } else { //Do not tick if in Charge and Roar counter > 0
                roarCooldown--;
            }
            return;
        }

        //if SMACKDOWN or MELEE active, we don't want to move, we just want to check status
        if (attackType == EnumTypes.GiantAttackType.SMACKDOWN || attackType == EnumTypes.GiantAttackType.MELEE) {
            checkAndPerformAttack(this.mob.getTarget(), G_SMASH_DISTANCE);
            return;
        }

        //No Attack type set so Walking towards the target
        triggerWalking();
        if (chargeCooldown > 0) chargeCooldown--;
        if (smashCooldown > 0) smashCooldown--;

        //any other scenario - we tick as normal
        super.tick();

    }

    @Override
    protected void checkAndPerformAttack(LivingEntity attackTarget, double distanceCanPerform) {
        double distanceToTarget = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(attackTarget);

        //if distance to target greater than 10 blocks (square of 100), continue to track target
        if (distanceToTarget > G_CHARGE_DISTANCE) {
            resetAttacks(); //if player is out of range, reset the attack to NONE.
            triggerWalking();
        } else {
            //If we are in range, do we have an attack type already, if so and in range for that attack type, go do it
            if (attackType == EnumTypes.GiantAttackType.CHARGE && distanceToTarget <= G_CHARGE_DISTANCE) {
                checkAndPerformCharge(false);
            } else if (attackType == EnumTypes.GiantAttackType.SMACKDOWN && distanceToTarget <= G_SMASH_DISTANCE) {
                checkAndPerformSmackdown(false);
            } else if (attackType == EnumTypes.GiantAttackType.MELEE && distanceToTarget <= G_REACH) {
                checkAndPerformMelee(false);
            } else { //assumed no active attacks, but we are in range now.
                if (isFirstEncounter) {
                    attackType = EnumTypes.GiantAttackType.CHARGE;
                    checkAndPerformCharge(true);
                } else {
                    resetAttacks();
                    triggerWalking();
                    //select new attack based on Random number, Cool downs and distance to target.
                    //eg: it can only select MELEE if within the Giants Reach.
                    attackType = selectRandomAttackType(distanceToTarget);
                    switch (attackType) {
                        case MELEE:
                            checkAndPerformMelee(true);
                            break;
                        case SMACKDOWN:
                            checkAndPerformSmackdown(true);
                            break;
                        case CHARGE:
                            checkAndPerformCharge(true);
                            break;
                        default:
                            //TBBLogger.getInstance().warn("Giant Attack Goal", "No Attack Type selected. Continuing");
                            break;
                    }
                }
            }
        }
    }


    private EnumTypes.GiantAttackType selectRandomAttackType(double distanceToTarget) {
        EnumTypes.GiantAttackType rAttack = EnumTypes.GiantAttackType.NONE;
        int randomValue = ModUtilities.nextInt(100);
        if (chargeCooldown <= 0 && randomValue < 10 && distanceToTarget <= G_CHARGE_DISTANCE)
            rAttack = EnumTypes.GiantAttackType.CHARGE;
        else if (smashCooldown <= 0 && randomValue < 20 && distanceToTarget <= G_SMASH_DISTANCE)
            rAttack = EnumTypes.GiantAttackType.SMACKDOWN;
        else if (distanceToTarget <= G_REACH) rAttack = EnumTypes.GiantAttackType.MELEE;

        return rAttack;
    }

    private void checkAndPerformMelee(boolean firstCall) {
        if (firstCall) {
            walking = false;
            giant.triggerAnim("mainController", "melee");
            giant.setState(EnumTypes.GiantState.ATTACKING_MELEE);
            animationTickTimer = 20;
        } else {
            if (animationTickTimer > 0) {
                animationTickTimer--;
            } else {
                setDamageAmount();
                giant.doHurtTarget(this.mob.getTarget());
                giant.setLastHurtMob(this.mob.getTarget());
                resetAttacks();
            }
        }

    }

    private void checkAndPerformSmackdown(boolean firstCall) {
        if (firstCall) {
            walking = false;
            giant.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
            giant.triggerAnim("mainController", "attack_smash");
            giant.setState(EnumTypes.GiantState.ATTACKING_SMASH);
            smashCooldown = ConfigManager.getInstance().getGiantConfig().getSmashCooldown();
            animationTickTimer = 25;
        } else {
            if (animationTickTimer > 0) {
                animationTickTimer--;
            } else {
                setDamageAmount();
                //TODO: Add Nauseu affect to player, add knockback to player
                giant.doHurtTarget(this.mob.getTarget());
                giant.setLastHurtMob(this.mob.getTarget());
                attackType = EnumTypes.GiantAttackType.NONE;
                resetAttacks();
            }
        }
    }


    private void checkAndPerformCharge(boolean firstCall) {
        LivingEntity target = giant.getTarget();
        double distanceToTarget = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(target);
        if (firstCall) {
            giant.setState(EnumTypes.GiantState.ATTACKING_CHARGE);
            walking = false;
            giant.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
            giant.triggerAnim("mainController", "attack_charge");
            TestEventLogger.logEvent(giant.getStringUUID(), "Giant Attack Goal",String.valueOf(giant.getId()),
                    "Initiated Charge");

            chargeCooldown = ConfigManager.getInstance().getGiantConfig().getChargeCooldown();
            roarCooldown = 50;
            animationTickTimer = 40;

            giant.playRoar();
        } else {
            if (distanceToTarget > G_REACH && animationTickTimer > 0) {
                animationTickTimer--;
            } else {
                if (distanceToTarget <= G_REACH) { //the Charge has to hit the player
                    setDamageAmount();
                    //ToDO: Add knock back to player
                    giant.doHurtTarget(target);
                    giant.setLastHurtMob(target);
                    resetAttacks();
                }
            }
        }
    }


    /***************************************** HELPER METHODS ***********************************************/

    private void setDamageAmount() {
        LivingEntity target = this.mob.getTarget();
        double damageModifier = 1.0;

        if (attackType == EnumTypes.GiantAttackType.CHARGE) damageModifier = 2;
        else if (attackType == EnumTypes.GiantAttackType.SMACKDOWN) damageModifier = 1.5;
        if (target.isBlocking()) damageModifier = damageModifier / 2;
        double newDamageAmount = origDamageValue * damageModifier;
        giant.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newDamageAmount);

    }
    private void triggerWalking() {
        if (!walking) {
            giant.triggerAnim("mainController", "walk");
            giant.setState(EnumTypes.GiantState.WALKING);
            walking = true;
        }
    }

    private void resetAttacks() {
        attackType = EnumTypes.GiantAttackType.NONE;
        giant.setState(EnumTypes.GiantState.IDLE);
        animationTickTimer = 0;
        roarCooldown = 0;
        //Reset attributes to defaults
        giant.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(origDamageValue);
        giant.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.originalSpeed);
    }
}
