package com.dsd.tbb.customs.entities;

import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.EnumTypes;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
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

    // Class member variable to store the last position
    private Vec3 lastPosition = Vec3.ZERO;
    private int followRange;
    private EnumTypes.GiantAttackType attackType;
    private boolean l_isStill;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AnimatableManager animatableManager = new AnimatableManager<TrialsByGiantZombie>(this);

    public TrialsByGiantZombie(EntityType type, Level world) {
        super(type, world);
        this.followRange = ConfigManager.getInstance().getGiantConfig().getFollowRange();
        //AnimationController ac = (AnimationController)this.animatableManager.getAnimationControllers().get("walkController");
        //ac.triggerableAnim("walk",DefaultAnimations.WALK);
        //ac.triggerableAnim("idle",DefaultAnimations.IDLE);

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
        //this.entityData.define(IS_ATTACKING,false);

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "mainController", 0, this::getAnimationState));

    }

    @Override
    public void tick() {
        super.tick();
        // Get the current position of the giant
        Vec3 currentPosition = this.position();

        // Calculate the squared distance to check for movement (more efficient than the actual distance)
        double distanceSquared = currentPosition.distanceToSqr(lastPosition);
        // If the distance squared is greater than a small threshold, we consider the giant to be moving
        l_isStill = distanceSquared < 0.001;
        //this.l_isStill=((lastPosition.x == currentPosition.x) && (lastPosition.z == currentPosition.z) && (lastPosition.y == currentPosition.y));

        // Update the last position for the next check
        lastPosition = currentPosition;

        /*animatableManager.getAnimationControllers().forEach((name, controller) -> {
            if (controller instanceof AnimationController) {
                int objectId = System.identityHashCode(this);
                AnimationController.State animState = ((AnimationController<?>) controller).getAnimationState();
                RawAnimation curAnim = ((AnimationController<?>) controller).getCurrentRawAnimation();
                TBBLogger.getInstance().debug("Ticking", String.format("Ticking [%d] with Animation Controller %s in State %s ",
                        objectId, ((AnimationController<?>) controller).getName(),animState.name()));
                if (curAnim != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Current Active Animation [").append(curAnim).append("]");
                    TBBLogger.getInstance().debug("Giant Tick", sb.toString());
                } else {
                    TBBLogger.getInstance().debug("Ticking", "No Current Animation");
                }
            }
        });*/
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this,
                Player.class, this.followRange, false, false, null));

        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }


    /********************************* GETTERS / /SETTERS ****************************************/
    public PlayState getAnimationState(AnimationState state) {
        RawAnimation returningAnim;
        if (this.l_isStill) {
            returningAnim = DefaultAnimations.IDLE;
        } else {
            returningAnim = DefaultAnimations.WALK;
        }
        return state.setAndContinue(returningAnim);

        /*StringBuilder sb = new StringBuilder();

        for(RawAnimation.Stage stage : returningAnim.getAnimationStages()){
        sb.append("Stage : [").append(stage.animationName()).append("]\n");

        }
        TBBLogger.getInstance().debug("getAnimState",sb.toString());*/
    }

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


    /*private PlayState walkingPredicate(AnimationState<TrialsByGiantZombie> state) {
        // Get the current position of the giant
        Vec3 currentPosition = this.position();

        // Calculate the squared distance to check for movement (more efficient than the actual distance)
        double distanceSquared = currentPosition.distanceToSqr(lastPosition);
        TBBLogger.getInstance().bulkLog("predicate",String.format("Last Pos [%f][%f][%f], Cur Pos [%f][%f][%f] - diff sqr [%f]",
                lastPosition.x,lastPosition.y,lastPosition.z,currentPosition.x,currentPosition.y,currentPosition.z,distanceSquared));
        // If the distance squared is greater than a small threshold, we consider the giant to be moving
        //isStill = distanceSquared == 0.0;
        isStill = (lastPosition.x == currentPosition.x) && (lastPosition.z == currentPosition.z) && (lastPosition.y == currentPosition.y);
        // Update the last position for the next check
        lastPosition = currentPosition;
        AnimationController ac = state.getController();
        // Set the appropriate animation based on whether the giant is moving
        if (isStill) {
            TBBLogger.getInstance().bulkLog("predicate","We are still");
            //state.setAndContinue(DefaultAnimations.IDLE);
            //state.getController().tryTriggerAnimation("idle");
            ac.setAnimation(DefaultAnimations.IDLE);

        }else {
            TBBLogger.getInstance().bulkLog("predicate", "We are moving");
            // Play walking animation

            //state.setAndContinue(DefaultAnimations.WALK);
            //state.getController().tryTriggerAnimation("walk");
        }

        return PlayState.CONTINUE;
    }*/

        /*public boolean isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }
    public void setAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }
    public boolean isSlamming() {
        return this.entityData.get(IS_SLAMMING);
    }
    public void setSlamming(boolean isSlamming) {
        this.entityData.set(IS_ATTACKING, isSlamming);
    }*/
}
