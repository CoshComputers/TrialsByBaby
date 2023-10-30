package com.dsd.tbb.testing;

import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.util.ConfigManager;
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
