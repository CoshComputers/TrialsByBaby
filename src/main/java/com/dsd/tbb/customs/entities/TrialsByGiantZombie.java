package com.dsd.tbb.customs.entities;

import com.dsd.tbb.customs.goals.GiantWanderGoal;
import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Boolean> IS_WALKING = SynchedEntityData.defineId(TrialsByGiantZombie.class, EntityDataSerializers.BOOLEAN);
    public static final RawAnimation ATTACK_SMASH = RawAnimation.begin().thenPlay("attack.smackdown");
    public AnimatableManager animatableManager = new AnimatableManager<TrialsByGiantZombie>(this);

    public TrialsByGiantZombie(EntityType type, Level world) {
        super(type,world);

        AnimationController ac = (AnimationController)this.animatableManager.getAnimationControllers().get("walkController");
        ac.triggerableAnim("walk",DefaultAnimations.WALK);

    }

    public void moveToPosition(Vec3 playerPos, Direction playerFacing) {
        Vec3 offset = new Vec3(playerFacing.getStepX() * 5, 0, playerFacing.getStepZ() * 5);
        Vec3 newPos = playerPos.add(offset);
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
        this.entityData.define(IS_WALKING, false);
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "walkController", 0, this::walkingPredicate));


    }

    private PlayState walkingPredicate(AnimationState<TrialsByGiantZombie> trialsByGiantZombieAnimationState) {

        if(this.isWalking()){
              TBBLogger.getInstance().debug("walkingPredicate","IS WALKING");
              trialsByGiantZombieAnimationState.getController().setAnimation(DefaultAnimations.WALK);
              return PlayState.CONTINUE;
        }else{
            TBBLogger.getInstance().debug("walkingPredicate","IS NOT WALKING");
            trialsByGiantZombieAnimationState.getController().setAnimation(DefaultAnimations.IDLE);
            return PlayState.CONTINUE;
        }
    }

    /*public void playWalkingAnimation(boolean isWalking){
        AnimationController ac = (AnimationController) this.animatableManager.getAnimationControllers().get("walkController");

        if(isWalking) {
            //TBBLogger.getInstance().debug("playWalking","Is Walking TRUE");
            this.isWalking = true;
            ac.setAnimation(DefaultAnimations.WALK);
            ac.tryTriggerAnimation("walk");

        }

        TBBLogger.getInstance().debug("playWalking",String.format("Animation State - %s",ac.getAnimationState().name()));
    }*/
    @Override
    public void registerGoals(){
        super.registerGoals();
        this.goalSelector.addGoal(5, new GiantWanderGoal(this, 1.0D));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }


    public void setWalking(boolean walking) {
        // Only update if there's a change to reduce network traffic
        if (this.entityData.get(IS_WALKING) != walking) {
            this.entityData.set(IS_WALKING, walking);
        }
    }

    public boolean isWalking() {
        return this.entityData.get(IS_WALKING);
    }
}
