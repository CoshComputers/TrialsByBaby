package com.dsd.tbb.ZZtesting;

import com.dsd.tbb.ZZtesting.loggers.TestEventLogger;
import com.dsd.tbb.ZZtesting.scenarios.ITestScenario;
import com.dsd.tbb.ZZtesting.scenarios.RandomMovementScenario;
import com.dsd.tbb.customs.entities.endgiant.EndGiant;
import com.dsd.tbb.handlers.ModEventHandlers;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.util.TBBLogger;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.function.Supplier;


@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MultiPlayerTest {
    public enum ScenarioType {
        RANDOM_MOVEMENT,
        DIMENSION_TRAVEL
        // ... other scenarios ...
    }
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static boolean isSpawnRateIncrementEnabled = false;

    public MultiPlayerTest(){

    }

    public static void logTestStart(ServerPlayer player){
        if(!TrialsByBaby.MOD_IS_IN_TESTING) return;
        ConfigManager cfg = ConfigManager.getInstance();
       TBBLogger.getInstance().info("logTestStart","******************STARTING TEST RUN********************");
       //TBBLogger.getInstance().bulkLog("Mod Configuration",cfg.getTrialsConfig().toString());
       //TBBLogger.getInstance().bulkLog("Giant Zombie Configuration",cfg.getGiantConfig().toString());

       //TBBLogger.getInstance().bulkLog("Random Movement Scenario Settings",RandomMovementScenario.setupOutput());

       //TBBLogger.getInstance().bulkLog("Player Position",String.format("X [%f], Y [%f], Z [%f]",
       //        player.position().x,player.position().y,player.position().z));

    }

    public static void logTestEnd(){
        if(!TrialsByBaby.MOD_IS_IN_TESTING) return;
        TBBLogger.getInstance().info("logTestEnd","TESTING CONCLUDED");
        TBBLogger.getInstance().info("logTestEnd","*********************************************************");
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if(!TrialsByBaby.MOD_IS_IN_TESTING) return;
        event.getDispatcher().register(Commands.literal("spawnFakePlayer")
                .requires(source -> source.hasPermission(2))
                .executes(context -> spawnFakePlayer(context.getSource()))
        );

        event.getDispatcher().register(Commands.literal("spawnGiant")
                .requires(source -> source.hasPermission(2))
                .executes(context -> spawnGiant(context.getSource()))
        );

        event.getDispatcher().register(Commands.literal("toggleSpawnRateIncrement")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    toggleSpawnRateIncrement(context.getSource());
                    return 1; // Command was successful
                })
        );


    }


    public static void toggleSpawnRateIncrement(CommandSourceStack source) {
        if(!TrialsByBaby.MOD_IS_IN_TESTING) return;
        Style style = Style.EMPTY.withColor(TextColor.parseColor("Green"));
        isSpawnRateIncrementEnabled = !isSpawnRateIncrementEnabled;
        Supplier<net.minecraft.network.chat.Component> componentSupplier = () -> Component.literal("Toggling Spawn Increasing Test: " +
                (isSpawnRateIncrementEnabled ? "enabled" : "disabled")).setStyle(style);
        source.sendSuccess(componentSupplier.get(),true);

        MultiPlayerTestScheduler.setEnabled(isSpawnRateIncrementEnabled);
    }

    public static int spawnFakePlayer(CommandSourceStack source) {
        if(!TrialsByBaby.MOD_IS_IN_TESTING) return 0;
        Style style = Style.EMPTY.withColor(TextColor.parseColor("Green"));
        Player player;
        try {
            player = source.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            Style failStyle = Style.EMPTY.withColor(TextColor.parseColor("Red"));
            Supplier<net.minecraft.network.chat.Component> componentSupplier = () -> Component.literal("Command Not Run by a Player").setStyle(failStyle);
            source.sendSuccess(componentSupplier.get(),true);
            return 0; // Failure
        }

        String timeDateStamp = FORMATTER.format(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        String fakePlayerName = "TBBFakePlayer" + timeDateStamp;
        Vec3 playerPos = player.position();
        Vec3 spawnPos = playerPos.add(player.getLookAngle().scale(5)); // Spawn 5 blocks in front
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), fakePlayerName);
        ScenarioType scenario = ScenarioType.RANDOM_MOVEMENT; // This will be from the command

        MyFakePlayer fakePlayer = new MyFakePlayer(source.getLevel(), gameProfile, true, 0);
        fakePlayer.setTestScenario(ScenarioFactory.createScenario(scenario, fakePlayer));
        fakePlayer.moveTo(spawnPos);
        source.getLevel().addFreshEntity(fakePlayer);
        TBBLogger.getInstance().info("Created Fake Player", String.format("[%s]", fakePlayer.getUUID()));
        TestEventLogger.logEvent(fakePlayer.getStringUUID() ,"Fake Player Created",String.valueOf(fakePlayer.getId()),"Fake player created from Command");

        Supplier<net.minecraft.network.chat.Component> componentSupplier = () -> Component.literal("Spawned fake player " + fakePlayerName + " at " + spawnPos).setStyle(style);
        source.sendSuccess(componentSupplier.get(),true);
        return 1; // Success
    }

    /******************* SPAWN GIANT COMMAND ******************************************/


    private static int spawnGiant(CommandSourceStack source) {
        if(!TrialsByBaby.MOD_IS_IN_TESTING) return 0;
        Player player;
        try {
            player = source.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        Vec3 playerPos = player.position();
        Direction playerFacing = player.getDirection();
        String newName = ConfigManager.getInstance().getRandomName();
        try {
            EndGiant newGiant = ModEventHandlers.END_GIANT.get().create(source.getLevel());

            assert newGiant != null;
            TBBLogger.getInstance().debug("*********Spawn Giant",String.format("Position [%s], Facing [%s]",playerPos,playerFacing));
            newGiant.moveToPosition(playerPos, playerFacing);
            source.getLevel().addFreshEntity(newGiant);
        }catch(Exception e){
            TBBLogger.getInstance().error("spawnGiantCommand","Something has gone wrong spawning the Giant.");
            e.printStackTrace();
        }
        return 1;
    }

    /*************** INNER CLASSES TO MANAGE SCENARIOS ****************************************************/

    public static class ScenarioFactory {

        public static ITestScenario createScenario(MultiPlayerTest.ScenarioType type, FakePlayer player) {
            switch (type) {
                case RANDOM_MOVEMENT:
                    return new RandomMovementScenario(player);
                case DIMENSION_TRAVEL:
                    return null;
                // ... handle other scenarios ...
                default:
                    throw new IllegalArgumentException("Unknown scenario type: " + type);
            }
        }
    }

}



