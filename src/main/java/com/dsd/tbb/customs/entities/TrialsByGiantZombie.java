package com.dsd.tbb.customs.entities;

import com.dsd.tbb.customs.goals.GiantCombatControllerGoal;
import com.dsd.tbb.customs.goals.GiantRandomStrollGoal;
import com.dsd.tbb.util.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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

import java.util.ArrayList;
import java.util.List;

public class TrialsByGiantZombie extends PathfinderMob implements GeoEntity {

    private String myName;
    private int followRange;
    private double baseDamage;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ServerBossEvent bossBar;
    public AnimatableManager animatableManager = new AnimatableManager<TrialsByGiantZombie>(this);

    public static final double BB_RANGE = 64.0;
    public static final float WIDTH = 1.2F;
    public static final float HEIGHT = 3.2F;
    public static final RawAnimation GIANT_ROAR_N_CHARGE = RawAnimation.begin()
            .thenPlay("attack.roar")
            .thenPlay("attack.charge");

    public TrialsByGiantZombie(EntityType type, Level world) {
        super(type, world);
        this.followRange = ConfigManager.getInstance().getGiantConfig().getFollowRange();
        this.baseDamage = ConfigManager.getInstance().getGiantConfig().getDamage();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(baseDamage);
        if(!world.isClientSide){
            this.bossBar = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
        }
    }

    public void moveToPosition(Vec3 playerPos, Direction playerFacing) {
        Vec3 offset = new Vec3(playerFacing.getStepX() * 5, 0, playerFacing.getStepZ() * 5);
        Vec3 newPos = playerPos.add(offset);
        this.setPos(newPos.x, newPos.y, newPos.z);
    }
    public void moveToPosition(BlockPos spawnPos) {
        this.setPos(spawnPos.getX(),spawnPos.getY(),spawnPos.getZ());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, ConfigManager.getInstance().getGiantConfig().getHealth())
                .add(Attributes.ATTACK_DAMAGE, ConfigManager.getInstance().getGiantConfig().getDamage())
                .add(Attributes.MOVEMENT_SPEED, 0.15);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<TrialsByGiantZombie> controller = new AnimationController<>(this, "mainController", 5, this::getAnimationState);
        controller.triggerableAnim("attack_smash", DefaultAnimations.ATTACK_SLAM)
                .triggerableAnim("attack_charge", GIANT_ROAR_N_CHARGE)
                .triggerableAnim("walk", DefaultAnimations.WALK)
                .triggerableAnim("idle", DefaultAnimations.IDLE)
                .triggerableAnim("melee", DefaultAnimations.ATTACK_STRIKE);
        controllers.add(controller);
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
        if (!this.level.isClientSide && this.bossBar != null) {
            //update the progress on the Boss Bar
            this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());

            //set a bounding box around the Giant to check if players are within range of
            //it. If they are they'll be able to see the Boss Bar
            AABB bossBarRange = new AABB(this.blockPosition()).inflate(BB_RANGE);

            // Add players that are now within range and not already seeing the boss bar
            List<Player> playersInRange = this.level.getEntitiesOfClass(Player.class, bossBarRange);
            for (Player player : playersInRange) {
                if (!this.bossBar.getPlayers().contains(player)) {
                    this.bossBar.addPlayer((ServerPlayer) player);
                }
            }

            // Remove players that are out of range
            List<Player> playersToRemove = new ArrayList<>();
            for (ServerPlayer player : this.bossBar.getPlayers()) {
                if (!bossBarRange.contains(player.position())) {
                    playersToRemove.add(player);
                }
            }
            for (Player player : playersToRemove) {
                this.bossBar.removePlayer((ServerPlayer) player);
            }
        }

        if (isAttackGoalActive()) {
            //TBBLogger.getInstance().debug("ticking","Attack active");

            // Attack animation is already being handled by the goal or animation system
        } else if (isStrollGoalActive()) {
            // TBBLogger.getInstance().debug("ticking","Attack not active - And WALKING");


        } else {
            // TBBLogger.getInstance().debug("ticking","Attack not active - And IDLE");
            //this.triggerAnim("mainController","idle");
        }

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

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.myName != null && !this.myName.isEmpty()) {
            // Convert the simple string to a JSON string representing a text component
            compound.putString("CustomName", Component.Serializer.toJson(Component.literal(this.myName)));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("CustomName", 8)) { // 8 signifies a string
            // Convert the JSON string back to a Component
            Component comp = Component.Serializer.fromJson(compound.getString("CustomName"));
            this.setMyName(comp.getString());
        }
        this.updateBossBarName();
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        // Set the boss bar to invisible
        if(!this.level.isClientSide && this.bossBar != null) this.bossBar.setVisible(false);
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        if (!this.level.isClientSide && this.bossBar != null) {
            this.bossBar.removeAllPlayers();
        }
    }
    /********************************* GETTERS / /SETTERS ****************************************/
    public boolean isAttackGoalActive() {
        return this.goalSelector.getRunningGoals()
                .anyMatch(goal -> goal.getGoal() instanceof GiantCombatControllerGoal);
    }

    public boolean isStrollGoalActive() {
        return this.goalSelector.getRunningGoals()
                .anyMatch(goal -> goal.getGoal() instanceof GiantRandomStrollGoal);
    }

    public AnimatableManager getAnimatableManager() {
        return animatableManager;
    }

    public String getMyName() {
        return myName;
    }

    public void updateBossBarName() {
        if (this.bossBar != null && this.myName != null && !this.myName.isEmpty()) {
            this.bossBar.setName(Component.literal(this.myName));
        }
    }

    public void setMyName(String myName) {
        this.myName = myName;
        updateBossBarName();
    }
}
