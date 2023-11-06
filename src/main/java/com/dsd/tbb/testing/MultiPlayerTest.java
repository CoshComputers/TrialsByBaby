package com.dsd.tbb.testing;

import com.dsd.tbb.customs.entities.TrialsByGiantZombie;
import com.dsd.tbb.handlers.ModEventHandlers;
import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.TBBLogger;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MultiPlayerTest {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static List<MyFakePlayer> fakePlayers = new ArrayList<>();
    public static RegistryObject<EntityType<Entity>> ATTACK_VILLAGER_TYPE;

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES, TrialsByBaby.MOD_ID);

    public MultiPlayerTest(){

    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("spawnFakePlayer")
                .requires(source -> source.hasPermission(2))
                .executes(context -> spawnFakePlayer(context.getSource()))
        );

        event.getDispatcher().register(Commands.literal("spawnGiant")
                .requires(source -> source.hasPermission(2))
                .executes(context -> spawnGiant(context.getSource()))
        );
    }

    private static int spawnGiant(CommandSourceStack source) {
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
            TrialsByGiantZombie newGiant = ModEventHandlers.TRIALS_BY_GIANT_ZOMBIE.get().create(source.getLevel());
            newGiant.setMyName(newName);
            newGiant.moveToPosition(playerPos, playerFacing);
            source.getLevel().addFreshEntity(newGiant);
        }catch(Exception e){
            TBBLogger.getInstance().error("spawnGiantCommand","Something has gone wrong spawning the Giant.");
            e.printStackTrace();
        }
        return 1;
    }


    private static int spawnFakePlayer(CommandSourceStack source) {
        Player player;
        try {
            player = source.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        String timeDateStamp = formatter.format(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        String fakePlayerName = "TBBFakePlayer" + timeDateStamp;
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(),fakePlayerName);
        Vec3 playerPos = player.position();
        Direction playerFacing = player.getDirection();
        String newName = ConfigManager.getInstance().getRandomName();
        MyFakePlayer fakePlayer = new MyFakePlayer(source.getLevel(),gameProfile);
        fakePlayer.setVariables(50, 100, newName,
                    playerPos, playerFacing, source.getLevel().getGameTime());
        source.getLevel().addFreshEntity(fakePlayer);

        fakePlayers.add(fakePlayer);


        return 1;  // success
    }




}
