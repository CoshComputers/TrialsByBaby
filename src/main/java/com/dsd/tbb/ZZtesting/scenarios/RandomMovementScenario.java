package com.dsd.tbb.ZZtesting.scenarios;

import com.dsd.tbb.ZZtesting.loggers.TestEventLogger;
import com.dsd.tbb.util.TBBLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Random;

public class RandomMovementScenario implements ITestScenario {
    private final FakePlayer player;
    private int movementCooldown;
    private final Random random;

    //20 Ticks per second - so n * 20 gives up Cooldown in n seconds.
    private static final int MAX_COOLDOWN = 10 * 20; //IF changed - ensure the FakePlayer Record Cooldown is changed too.


    private static final int RADIUS = 200;
    private BlockPos originalPos;



    public static String setupOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append("Random Movement Scenario Settings,");
        sb.append("Movement Cooldown,").append(MAX_COOLDOWN).append(",");
        sb.append("Move Radius,").append(RADIUS);
        return sb.toString();
    }
    public RandomMovementScenario(FakePlayer player) {
        this.player = player;
        this.movementCooldown = MAX_COOLDOWN;
        this.random = new Random();
        originalPos = player.blockPosition();
    }

    @Override
    public void update() {
        if (movementCooldown <= 0) {
            movePlayerToRandomPosition();
            movementCooldown = MAX_COOLDOWN;
        } else {
            movementCooldown--;
        }
    }

    private void movePlayerToRandomPosition() {
        Level level = player.level;

        int x = random.nextInt(RADIUS * 2) - RADIUS + originalPos.getX();
        int z = random.nextInt(RADIUS * 2) - RADIUS + originalPos.getZ();
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z); // Find a safe Y position

        BlockPos newPos = new BlockPos(x, y, z);
        player.setPos(newPos.getX(), newPos.getY(), newPos.getZ());
        TBBLogger.getInstance().info("Moving Fake Player",String.format("[%s] - [%d][%d][%d]",player.getUUID(),x,y,z));
        TestEventLogger.logEvent(player.getStringUUID(), "Move Fake Player",String.valueOf(player.getId()) ,String.format("Fake Player Moved to [%d][%d][%d]",x,y,z));
        // Assuming TestResultData handling is done elsewhere
    }
}
