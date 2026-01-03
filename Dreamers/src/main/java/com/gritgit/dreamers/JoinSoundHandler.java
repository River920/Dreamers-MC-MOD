package com.gritgit.dreamers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Random;

@EventBusSubscriber(modid = Dreamers.MODID, value = Dist.CLIENT)
public class JoinSoundHandler {

    // Register all sound events
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
            ForgeRegistries.SOUND_EVENTS, Dreamers.MODID);

    public static final RegistryObject<SoundEvent> JOIN_SOUND = SOUNDS.register("join_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Dreamers.MODID, "join_sound")));

    public static void register() {
        // Empty method needed so EventBusSubscriber picks up this class
    }

    // time (millis) when the next sound should play; -1 means not scheduled
    private static long nextPlayTimeMs = -1L;
    private static final Random RANDOM = new Random();

    // when player logs in, schedule the first play 20-30 seconds later
    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        scheduleNextPlay();
    }

    // client tick handler: when time reaches nextPlayTimeMs, play sound and reschedule 20-30s later
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // only act on END phase to reduce checks; ensures Minecraft instance is fully ticked
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        // if no player or not in-world, do nothing
        if (mc.player == null) return;

        long now = System.currentTimeMillis();
        if (nextPlayTimeMs > 0 && now >= nextPlayTimeMs) {
            // play the join sound as UI sound
            mc.getSoundManager().play(SimpleSoundInstance.forUI(JOIN_SOUND.get(), 1.0F));
            // schedule the next play
            scheduleNextPlay();
        }
    }

    private static void scheduleNextPlay() {
        int delaySeconds = 600 + RANDOM.nextInt(1201);
        nextPlayTimeMs = System.currentTimeMillis() + (delaySeconds * 1000L);
    }
}