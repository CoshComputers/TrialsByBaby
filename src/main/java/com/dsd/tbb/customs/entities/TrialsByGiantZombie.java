package com.dsd.tbb.customs.entities;

import com.dsd.tbb.util.ConfigManager;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
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
import software.bernie.geckolib.util.GeckoLibUtil;

public class TrialsByGiantZombie extends PathfinderMob implements GeoEntity {

    // Class member variable to store the last position
    private Vec3 lastPosition = Vec3.ZERO;
    private int followRange;
    //private boolean isStill = false;
    //private static final EntityDataAccessor<Boolean> IS_STILL = SynchedEntityData.defineId(TrialsByGiantZombie.class, EntityDataSerializers.BOOLEAN);
    private boolean l_isStill;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public AnimatableManager animatableManager = new AnimatableManager<TrialsByGiantZombie>(this);

    public TrialsByGiantZombie(EntityType type, Level world) {
        super(type,world);
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

    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
           new AnimationController<>(this, 0,
                        state -> state.setAndContinue(this.l_isStill ? DefaultAnimations.IDLE : DefaultAnimations.WALK)));

    }

    @Override
    public void tick(){
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

    @Override
    public void registerGoals(){
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this,
                Player.class, this.followRange, false, false, null));

        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
