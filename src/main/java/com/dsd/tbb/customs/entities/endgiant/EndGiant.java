package com.dsd.tbb.customs.entities.endgiant;

import com.dsd.tbb.util.TBBLogger;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EndGiant extends PathfinderMob implements GeoEntity {

    //GEO ENTITY AND ANIMATION VARIABLES
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public AnimatableManager<EndGiant> animatableManager = new AnimatableManager<>(this);

    // Entity parts as separate entities
    public final EndGiantPart leftArm;
    public final EndGiantPart rightArm;
    public final EndGiantPart leftLeg;
    public final EndGiantPart rightLeg;
    public final EndGiantPart torso;
    public final EndGiantPart head;

    // Constructor
    public EndGiant(EntityType type, Level world) {
        super(type, world);

        // Initialize Entity Parts
        this.leftArm = new EndGiantPart(this, "left_arm", 1.0F, 1.0F);
        this.rightArm = new EndGiantPart(this, "right_arm", 1.0F, 1.0F);
        this.leftLeg = new EndGiantPart(this,"left_leg",1.0F,1.0F);
        this.rightLeg = new EndGiantPart(this,"right_leg",1.0F,1.0F);
        this.torso = new EndGiantPart(this,"torso",1.0F,1.0F);
        this.head = new EndGiantPart(this,"head",1.0F,1.0F);

        // ... Initialize other parts similarly

        // Add parts to parts list if necessary
    }

    public static AttributeSupplier.Builder createAttributes() {
        TBBLogger.getInstance().debug("Regular Zombie CreateAttributes","Called");

        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.ATTACK_DAMAGE, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.05);
    }

    // Overridden Mob methods and unique EndGiant methods
    // ...

    // Additional methods for AI, phase management, etc.
    // ...

    public boolean hurt(EndGiantPart p_31121_, DamageSource p_31122_, float p_31123_) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }
    public AnimatableManager<EndGiant> getAnimatableManager() {
        return this.animatableManager;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }

    public void moveToPosition(Vec3 playerPos, Direction playerFacing) {
        Vec3 offset = new Vec3(playerFacing.getStepX() * 5, 0, playerFacing.getStepZ() * 5);
        Vec3 newPos = playerPos.add(offset);
        this.setPos(newPos.x, newPos.y, newPos.z);
    }
}
