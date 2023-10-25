package com.dsd.tbb.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;

public class MyFakePlayer extends FakePlayer {
    public MyFakePlayer(ServerLevel level, GameProfile name) {
        super(level, name);
    }
}
