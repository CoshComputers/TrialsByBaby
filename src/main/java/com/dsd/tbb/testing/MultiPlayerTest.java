package com.dsd.tbb.testing;

import com.dsd.tbb.main.TrialsByBaby;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;


@Mod.EventBusSubscriber(modid = TrialsByBaby.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MultiPlayerTest {

    List<MyFakePlayer> fakePlayers;
    public MultiPlayerTest(){

       // MyFakePlayer fakePlayer = new MyFakePlayer("overworld",new GameProfile(UUID.randomUUID(),"will"));


    }


    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("spawnFakePlayer")
                .requires(source -> source.hasPermission(2))
                .executes(context -> spawnFakePlayer(context.getSource()))
        );
    }

    private static int spawnFakePlayer(CommandSourceStack source) {
        // Logic to spawn a fake player
        // ...
        return 1;  // success
    }

}
