package com.dsd.tbb.ZZtesting;

import com.dsd.tbb.ZZtesting.loggers.TestEventLogger;
import com.dsd.tbb.ZZtesting.loggers.TestResultData;
import com.dsd.tbb.ZZtesting.scenarios.ITestScenario;
import com.dsd.tbb.config.PlayerConfig;
import com.dsd.tbb.customs.entities.TrialsByBabyZombie;
import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.managers.BossBarManager;
import com.dsd.tbb.managers.PlayerManager;
import com.dsd.tbb.managers.SpawningManager;
import com.dsd.tbb.util.TBBLogger;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Collection;

public class MyFakePlayer extends FakePlayer {

    private ITestScenario testScenario;
    private boolean isInvincible;
    private int lifespanTicks; // Lifespan in ticks (20 ticks = 1 second)
    private static final int TICK_COOLDOWN= 20;
    public static final int MAX_RECORD_DATA_COOLDOWN = 60; //Should be a factor of the Scenario Cooldown
    private static final int MAX_RECORD_ENTITY_COUNT_COOLDOWN = 200;
    public boolean neverDies;
    private int spawningCooldown = 0;
    private int recordCooldown = 0;
    private int recordEntityCountCooldown = 0;

    public MyFakePlayer(ServerLevel world, GameProfile gProfile, boolean isInvincible, int lifeSpanSeconds) {
        super(world, gProfile);
        this.isInvincible = isInvincible;

        if(lifeSpanSeconds == 0) neverDies = true;
        else neverDies = false;

        this.lifespanTicks = lifeSpanSeconds * 20;


    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvincible) {
            return false; // Ignore damage if invincible
        }
        return super.hurt(source, amount);
    }
    @Override
    public void tick(){
        super.tick();
        if(spawningCooldown <= 0){
            PlayerConfig pcfg = PlayerManager.getInstance().getRandomPlayer();
            SpawningManager.spawnManagerTick(this.level,this,pcfg);
            spawningCooldown = TICK_COOLDOWN;

        }else{
            spawningCooldown --;
        }

        // Collect and record data every tick
        if(recordCooldown <= 0) {
            collectAndRecordData();
            recordCooldown = MAX_RECORD_DATA_COOLDOWN;
        }else{
            recordCooldown --;
        }

        if(recordEntityCountCooldown <=0){
            int eCount = EntityCounter.countEntities(this.getLevel());
            TestEventLogger.logEvent(this.stringUUID,"Total Entity Count", String.valueOf(eCount));
            recordEntityCountCooldown = MAX_RECORD_ENTITY_COUNT_COOLDOWN;
        }else{
            recordEntityCountCooldown --;
        }

        // Scenario-specific logic
        if (this.testScenario != null) {
            this.testScenario.update();
        }

        // Lifespan countdown
        if(!neverDies) {
            if (lifespanTicks > 0) {
                lifespanTicks--;
                if (lifespanTicks == 0) {
                    this.removeSelf(); // Custom method to safely remove the FakePlayer
                }
            }
        }
        // Place a marker block at the current position
        placeMarkerBlock(this.blockPosition());
    }

    private void placeMarkerBlock(BlockPos position) {
        if (this.level.isEmptyBlock(position)) { // Check to avoid overwriting existing blocks
            this.level.setBlock(position, Blocks.RED_STAINED_GLASS.defaultBlockState(), 3); // Example: Use glass
        }
    }
    private void removeSelf() {
        // Implement the logic to safely remove this FakePlayer from the world
        // This might include dropping items, clearing effects, etc.
        TBBLogger.getInstance().info("Fake Player Terminating",String.format("Fake Player [%s] Terminated", this.getUUID()));
        TestEventLogger.logEvent(this.getUUID().toString(),"Fake Player Terminating","Removing Self due to Timer expiring");
        this.remove(Entity.RemovalReason.DISCARDED); // Example removal

    }
    public void setTestScenario(ITestScenario testScenario) {
        this.testScenario = testScenario;
    }

    private int countPlayerBossBars() {
        BossBarManager bossBarManager = BossBarManager.getInstance();
        Collection<ServerBossEvent> bossBars = bossBarManager.getBossBarsForPlayer(this.getUUID());
        return bossBars.size();
    }
    private void collectAndRecordData() {
        AABB boundingBox = new AABB(this.blockPosition()).inflate(64); // 64 blocks radius, adjust as needed

        int babyZombiesCount = 0;// Logic to count nearby Baby Zombies
        int giantsCount = 0;// Logic to count nearby Giants
        int bossBarsCount = countPlayerBossBars();// Logic to count connected Boss Bars


        for (Entity entity : this.level.getEntities(this, boundingBox)) {
            if (entity instanceof TrialsByBabyZombie) {
                babyZombiesCount++;
            } else if (entity instanceof TrialsByGiantZombie) {
                giantsCount++;
            }
        }

        // Record data in TestResultData
        TestResultData.recordData(this.stringUUID,this.blockPosition(),babyZombiesCount,giantsCount,bossBarsCount);
    }

    public class EntityCounter {

        public static int countEntities(ServerLevel serverLevel) {
            int count = 0;
            for (Entity entity : serverLevel.getAllEntities()) {
                count++;
            }
            return count;
        }
    }
}
