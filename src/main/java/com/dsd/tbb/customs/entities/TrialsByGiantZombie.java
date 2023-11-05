package com.dsd.tbb.customs.entities;

import com.dsd.tbb.customs.goals.GiantCombatControllerGoal;
import com.dsd.tbb.customs.goals.GiantRandomStrollGoal;
import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.EnumTypes;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TrialsByGiantZombie extends PathfinderMob implements GeoEntity {

    private Vec3 lastPosition = Vec3.ZERO;
    private int followRange;
    private EnumTypes.GiantAttackType attackType;
    private AnimationController<TrialsByGiantZombie> mainAnimController;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AnimatableManager animatableManager = new AnimatableManager<TrialsByGiantZombie>(this);

    public static final RawAnimation GIANT_ROAR_N_CHARGE = RawAnimation.begin()
            .thenPlay("attack.roar")
            .thenPlay("attack.charge");

    public TrialsByGiantZombie(EntityType type, Level world) {
        super(type, world);
        this.followRange = ConfigManager.getInstance().getGiantConfig().getFollowRange();
    }

    public void moveToPosition(Vec3 playerPos, Direction playerFacing) {
        Vec3 offset = new Vec3(playerFacing.getStepX() * 5, 0, playerFacing.getStepZ() * 5);
        Vec3 newPos = playerPos.add(offset);
        lastPosition = newPos;
        this.setPos(newPos.x, newPos.y, newPos.z);
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, ConfigManager.getInstance().getGiantConfig().getHealth())
                .add(Attributes.ATTACK_DAMAGE, ConfigManager.getInstance().getGiantConfig().getDamage())
                .add(Attributes.MOVEMENT_SPEED, ConfigManager.getInstance().getGiantConfig().getSpeed());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<TrialsByGiantZombie> controller = new AnimationController<>(this, "mainController", 5, this::getAnimationState);
        controller.triggerableAnim("attack_smash", DefaultAnimations.ATTACK_SLAM)
                .triggerableAnim("attack_charge",GIANT_ROAR_N_CHARGE)
                .triggerableAnim("walk", DefaultAnimations.WALK)
                .triggerableAnim("idle", DefaultAnimations.IDLE)
                .triggerableAnim("melee",DefaultAnimations.ATTACK_STRIKE);
        controllers.add(controller);
        this.mainAnimController = controller;
    }

    public PlayState getAnimationState(AnimationState state) {
        if (this.isDeadOrDying()) {
            return PlayState.STOP;  // Stop animation if the entity is dead or dying
        }


        return PlayState.CONTINUE;  // Otherwise, continue the animation
    }


    @Override
    public void tick() {
        super.tick();

        if(isAttackGoalActive()) {
            //TBBLogger.getInstance().debug("ticking","Attack active");

            // Attack animation is already being handled by the goal or animation system
        } else if (isStrollGoalActive()) {
           // TBBLogger.getInstance().debug("ticking","Attack not active - And WALKING");


        } else {
           // TBBLogger.getInstance().debug("ticking","Attack not active - And IDLE");
            //this.triggerAnim("mainController","idle");
        }
        /*
        Vec3 currentPosition = this.position();
        // Calculate the squared distance to check for movement (more efficient than the actual distance)
        double distanceSquared = currentPosition.distanceToSqr(lastPosition);
        // If the distance squared is greater than a small threshold, we consider the giant to be moving
        boolean l_isStill = distanceSquared < 0.001;
        // Update the last position for the next check
        lastPosition = currentPosition;*/

    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this,
                Player.class, this.followRange, false, false, null));

        this.goalSelector.addGoal(3, new GiantCombatControllerGoal(this));
        this.goalSelector.addGoal(5, new GiantRandomStrollGoal(this, 1.0D));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }


    /********************************* GETTERS / /SETTERS ****************************************/
    public boolean isAttackGoalActive() {
        return this.goalSelector.getRunningGoals()
                .anyMatch(goal -> goal.getGoal() instanceof GiantCombatControllerGoal);
    }
    public boolean isStrollGoalActive() {
        return this.goalSelector.getRunningGoals()
                .anyMatch(goal -> goal.getGoal() instanceof  GiantRandomStrollGoal);
    }
    /*public boolean isAttackAnimationRunning() {
        AnimationController<TrialsByGiantZombie> controller =
                (AnimationController<TrialsByGiantZombie>) this.getAnimatableManager().getAnimationControllers().get("mainController");
        String curAnim =   controller.getCurrentRawAnimation().getAnimationStages().get(0).animationName();
        if(curAnim != null){
            return false;
        }else{
            return curAnim.startsWith("attack");
        }
    }*/


    public EnumTypes.GiantAttackType getAttackType() {
        return attackType;
    }

    public void setAttackType(EnumTypes.GiantAttackType attackType) {
        this.attackType = attackType;
    }

    public AnimatableManager getAnimatableManager() {
        return animatableManager;
    }


    public boolean isPlayerInRange(double range) {
        LivingEntity target = this.getTarget();
        return target != null && target instanceof Player && this.distanceTo(target) <= range;
    }

}
