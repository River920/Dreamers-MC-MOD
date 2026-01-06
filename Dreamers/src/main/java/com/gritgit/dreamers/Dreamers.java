package com.gritgit.dreamers;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Dreamers.MODID)
public class Dreamers {
    public static final String MODID = "dreamers";

    public Dreamers() {
        // Register sounds
        JoinSoundHandler.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        // Register events (player login)
        JoinSoundHandler.register();
        // Register entities
        ModEntities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
