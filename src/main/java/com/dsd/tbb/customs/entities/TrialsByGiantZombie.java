package com.dsd.tbb.customs.entities;

import com.dsd.tbb.config.GiantConfig;
import com.dsd.tbb.customs.goals.GiantCombatControllerGoal;
import com.dsd.tbb.customs.goals.GiantRandomStrollGoal;
import com.dsd.tbb.managers.BossBarManager;
import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrialsByGiantZombie extends PathfinderMob implements GeoEntity {

    //Data to share between Client and Server
    private static final EntityDataAccessor<String> GIANT_NAME = SynchedEntityData.defineId(TrialsByGiantZombie.class, EntityDataSerializers.STRING);

    //--------------------------------------

    private final BossBarManager bossBarManager;

    private List<ItemStack> drops = new ArrayList<>();
    private int followRange;
    private double baseDamage;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public AnimatableManager animatableManager = new AnimatableManager<TrialsByGiantZombie>(this);

    public static final double BB_RANGE = 64.0;
    public static final float WIDTH = 1.2F;
    public static final float HEIGHT = 3.2F;
    public static final RawAnimation GIANT_ROAR_N_CHARGE = RawAnimation.begin()
            .thenPlay("attack.roar")
            .thenPlay("attack.charge");

    public TrialsByGiantZombie(EntityType type, Level world) {
        super(type, world);
        UUID uniqueID = this.getUUID();
        //TBBLogger.getInstance().bulkLog("Entity Constructor",String.format("Constructing UUID [%s]",uniqueID));
        this.followRange = ConfigManager.getInstance().getGiantConfig().getFollowRange();
        this.baseDamage = ConfigManager.getInstance().getGiantConfig().getDamage();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(baseDamage);
        this.setPersistenceRequired();
        if(!world.isClientSide){
            TBBLogger.getInstance().bulkLog("Giant Constructor",String.format("Dimension [%s]", world.dimension().location()));
            this.bossBarManager = BossBarManager.getInstance();
            bossBarManager.createBossBar(this.getUUID(), this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

        }else{
            this.bossBarManager = null;
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

    public void setRandomDrops() {
        GiantConfig config = ConfigManager.getInstance().getGiantConfig();
        this.drops.addAll(config.getRandomDrops());
    }


    // Call this when the giant is defeated to drop the items
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitInPlayer) {
        super.dropCustomDeathLoot(source, looting, recentlyHitInPlayer);
        for (ItemStack drop : drops) {
            this.spawnAtLocation(drop, 0.0F);
        }
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GIANT_NAME, "");
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

        //Update Boss Bar Progress.
        float healthPercentage = this.getHealth() / this.getMaxHealth();
        BossBarManager.getInstance().updateProgress(this.getUUID(), healthPercentage);

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
        //TBBLogger.getInstance().bulkLog("Entity Save Additional",String.format("Adding Additional Data for Save [%s]",this.getMyName()));
        super.addAdditionalSaveData(compound);
        if (this.getMyName() != null && !this.getMyName().isEmpty()) {
            // Convert the simple string to a JSON string representing a text component
            compound.putString("CustomName", Component.Serializer.toJson(Component.literal(this.getMyName())));
        }

        ListTag listTag = new ListTag();
        for (ItemStack stack : this.drops) {
            CompoundTag itemTag = new CompoundTag();
            stack.save(itemTag);
            listTag.add(itemTag);
        }
        compound.put("EnchantedBooks", listTag);

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        //TBBLogger.getInstance().bulkLog("Entity Read Additional",String.format("Reading Additional Data for Save [%s]",this.getMyName()));
        super.readAdditionalSaveData(compound);
        if (compound.contains("CustomName", 8)) { // 8 signifies a string
            // Convert the JSON string back to a Component
            Component comp = Component.Serializer.fromJson(compound.getString("CustomName"));
            this.setMyName(comp.getString());
        }
        this.updateBossBarName();

        ListTag listTag = compound.getList("EnchantedBooks", 10); // 10 is the tag type for CompoundTag
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag itemTag = listTag.getCompound(i);
            this.drops.add(ItemStack.of(itemTag));
        }
    }

    @Override
    public void checkDespawn() {

        // Overriding without calling super prevents the default despawn logic from running
    }



    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        //SpawningManager.removeGiantZombieByUUID(this.getUUID());
        // Set the boss bar to invisible
        if (!this.level.isClientSide) {
            TBBLogger.getInstance().debug("Entity Die",String.format("Giant [%s] Died",this.getMyName()));
            BossBarManager.getInstance().removeBossBar(this.getUUID());
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
        return this.entityData.get(GIANT_NAME);
    }

    public void updateBossBarName() {
        if (this.getMyName() != null && !this.getMyName().isEmpty()) {
            BossBarManager.getInstance().setBossBarName(this.getUUID(), Component.literal(this.getMyName()));
        }
    }

    public void setMyName(String myName) {
        //TBBLogger.getInstance().bulkLog("Setting Name",String.format("Name [%s] Set for UUID [%s]",myName,this.getUUID()));
        this.entityData.set(GIANT_NAME, myName);
        updateBossBarName();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Giant - [").append(this.getMyName()).append("] UUID [").append(this.getUUID()).append("] ");
        sb.append("In Dimension [").append(this.getLevel().dimension().location()).append("] ");

        return sb.toString();
    }
}
