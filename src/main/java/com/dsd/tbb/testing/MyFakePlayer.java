package com.dsd.tbb.testing;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.util.FakePlayer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyFakePlayer extends FakePlayer {
    private int minMoveRange;
    private String myName;
    private int maxMoveRange;
    private StringBuilder sb = new StringBuilder();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")
            .withZone(ZoneId.systemDefault());

    private List<String> logOutput = new ArrayList<>();
    public MyFakePlayer(ServerLevel level, GameProfile name, int minMoveRange, int maxMoveRange) {
        super(level, name);
        this.minMoveRange = minMoveRange;
        this.maxMoveRange = maxMoveRange;
        logOutput(String.format("Fakeplayer - %s - Instantiated",name.getName()));
        this.myName = "SomeRandomNameGenerator";
    }

    public void moveToRandomLocation(){
        Random random = new Random();
        int randomX = random.nextInt((maxMoveRange - minMoveRange) + 1) + minMoveRange;
        int randomZ = random.nextInt((maxMoveRange - minMoveRange) + 1) + minMoveRange;

        BlockPos surfacePos = this.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(randomX, 0, randomZ));

        this.setPos(surfacePos.getX(), surfacePos.getY(), surfacePos.getZ());
    }

    public void executeCommand(String commandToExecute){

    }

    public void killLocalMobs(){

    }

    private void logOutput(String logEntry){
        sb.delete(0,sb.length());
        sb.append("[").append(formatter.format(Instant.now())).append("]");
        sb.append(logEntry);
        logOutput.add(sb.toString());
    }


    public String getMyName(){
        return this.myName;
    }
}
