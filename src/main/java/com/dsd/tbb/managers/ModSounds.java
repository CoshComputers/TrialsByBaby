package com.dsd.tbb.managers;

import com.dsd.tbb.main.TrialsByBaby;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TrialsByBaby.MOD_ID);

    public static final RegistryObject<SoundEvent> GIANT_ROAR = SOUNDS.register("giant_roar",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TrialsByBaby.MOD_ID, "giant_roar")));

    public static final RegistryObject<SoundEvent> GIANT_SMASH = SOUNDS.register("giant_smash",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TrialsByBaby.MOD_ID, "giant_smash")));
}

