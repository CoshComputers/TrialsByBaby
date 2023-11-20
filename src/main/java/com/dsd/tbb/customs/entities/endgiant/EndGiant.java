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
import net.minecraftforge.entity.PartEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EndGiant extends PathfinderMob implements GeoEntity {

    //GEO ENTITY AND ANIMATION VARIABLES
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public AnimatableManager<EndGiant> animatableManager = new AnimatableManager<>(this);

    // Entity parts as separate entities
    private final EndGiantPart[] parts;
    public final EndGiantPart leftUpperArm;
    public final EndGiantPart leftLowerArm;
    public final EndGiantPart rightUpperArm;
    public final EndGiantPart rightLowerArm;
    public final EndGiantPart leftLowerLeg;
    public final EndGiantPart leftUpperLeg;
    public final EndGiantPart rightLowerLeg;
    public final EndGiantPart rightUpperLeg;
    public final EndGiantPart body;
    public final EndGiantPart head;
    public final EndGiantPart headwear;

    // Constructor
    public EndGiant(EntityType type, Level world) {
        super(type, world);

        // Initialize Entity Parts
        this.leftUpperArm = new EndGiantPart(this, "left_upper_arm", 1.6F, 4.0F);
        this.leftLowerArm = new EndGiantPart(this, "left_lower_arm", 1.6F, 4.0F);
        this.rightUpperArm = new EndGiantPart(this, "right_upper_arm", 1.6F, 4.0F);
        this.rightLowerArm = new EndGiantPart(this, "right_lower_arm", 1.6F, 4.0F);
        this.leftUpperLeg = new EndGiantPart(this,"left_upper_leg",1.3F,3.0F);
        this.leftLowerLeg = new EndGiantPart(this,"left_lower_leg",1.3F,3.0F);
        this.rightUpperLeg = new EndGiantPart(this,"right_upper_leg",1.3F,3.0F);
        this.rightLowerLeg = new EndGiantPart(this,"right_lower_leg",1.3F,3.0F);
        this.body = new EndGiantPart(this,"torso",3.0F,4.0F);
        this.head = new EndGiantPart(this,"skull",3.0F,3.0F);
        this.headwear = new EndGiantPart(this,"headwear",3.0F,3.0F);

        // Store parts in an array
        this.parts = new EndGiantPart[]{this.leftUpperArm,this.leftUpperArm,this.rightUpperArm,this.rightLowerArm,
                                        this.leftUpperLeg,this.leftLowerLeg,this.rightUpperLeg,this.rightLowerLeg,
                                        this.body,this.head,this.headwear};
        this.setId(ENTITY_COUNTER.getAndAdd(this.parts.length + 1) + 1); // Forge: Fix MC-158205: Make sure part ids are successors of parent mob id
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.parts.length; i++) // Forge: Fix MC-158205: Set part ids to successors of parent mob id
            this.parts[i].setId(id + i + 1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.ATTACK_DAMAGE, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.05);
    }

    @Override
    public boolean hurt(DamageSource dSource,float dValue){
        TBBLogger.getInstance().debug("End GIant Hurt","Hurt");
        return super.hurt(dSource,dValue);
    }


    public boolean hurt(EndGiantPart partHit, DamageSource dSource, float dValue) {
        TBBLogger.getInstance().debug("End GIant Hurt","Hurt from part" + partHit.name);
        this.hurt(dSource,dValue);
        return true;
    }

    public EndGiantPart[] getEGPParts(){ return this.parts;}

    @Override
    public PartEntity<?>[] getParts() {
        return this.parts;
    }
    public EndGiantPart getPart(int index){
        return this.parts[index];
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
