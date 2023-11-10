package com.dsd.tbb.testing;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MyFakePlayer extends FakePlayer {
    private static final int MOVE_INTERVAL = 300;
    private long spawnTime;
    private int minMoveRange;
    private String myName;
    private int maxMoveRange;
    private StringBuilder sb = new StringBuilder();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")
            .withZone(ZoneId.systemDefault());
    private List<String> logOutput = new ArrayList<>();

    public MyFakePlayer(ServerLevel world, GameProfile gProfile) {
        super(world, gProfile);
    }

    public void setVariables(int vMinMoveRange,int vMaxMoveRange,String myName,Vec3 playerPos,Direction playerFacing, long spawnTime){

        minMoveRange = vMinMoveRange;
        this.maxMoveRange = vMaxMoveRange;
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 255));
        moveToPosition(playerPos,playerFacing);
        this.myName = myName;
        this.spawnTime = spawnTime;

        ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
        sword.enchant(Enchantments.SHARPNESS, 5);
        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
        this.addDamageAttribute();
        // Make the villager invulnerable
        this.setHealth(500);
        this.setGameMode(GameType.SURVIVAL);
    }

    private void addDamageAttribute() {
        UUID attackDamageModifierUUID = UUID.randomUUID();  // Generate a random UUID for this modifier
        AttributeModifier attackDamageModifier = new AttributeModifier(attackDamageModifierUUID, "Attack Damage Modifier", 5.0, AttributeModifier.Operation.ADDITION);
        ListTag modifiersList = this.getAttributes().save();
        CompoundTag modifierTag = new CompoundTag();
        attackDamageModifier.save();  // This method saves the modifier data to a CompoundTag
        modifiersList.add(modifierTag);
        this.getAttributes().save();
    }
    public void moveToRandomLocation(){
        Random random = new Random();
        int randomX = random.nextInt((maxMoveRange - minMoveRange) + 1) + minMoveRange;
        int randomZ = random.nextInt((maxMoveRange - minMoveRange) + 1) + minMoveRange;

        BlockPos surfacePos = this.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(randomX, 0, randomZ));
        //TBBLogger.getInstance().debug("moveToRandom-FakePlayer",String.format("Moved To [%d][%d][%d]",
        //        surfacePos.getX(), surfacePos.getY(), surfacePos.getZ()));
        this.setPos(surfacePos.getX(), surfacePos.getY(), surfacePos.getZ());
    }

    public void executeCommand(String commandToExecute){

    }

    public void killLocalMobs(){

    }

    private void moveToPosition(Vec3 playerPos, Direction playerFacing) {
        Vec3 offset = new Vec3(playerFacing.getStepX() * 5, 0, playerFacing.getStepZ() * 5);
        Vec3 newPos = playerPos.add(offset);
        this.setPos(newPos.x, newPos.y, newPos.z);
    }

    private void logOutput(String logEntry){
        sb.delete(0,sb.length());
        sb.append("[").append(formatter.format(Instant.now())).append("]");
        sb.append(logEntry);

    }


    public String getMyName(){
        return this.myName;
    }

    private void clearGoals(GoalSelector goalSelector) {
        Set<Goal> goalsToRemove = new HashSet<>();
        goalSelector.getAvailableGoals().forEach(goalEntry -> goalsToRemove.add(goalEntry.getGoal()));
        for (Goal goal : goalsToRemove) {
            goalSelector.removeGoal(goal);
        }
    }
    @Override
    public void tick(){
        super.tick();


    }
}
