package com.gritgit.dreamers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

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

    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(JOIN_SOUND.get(), 1.0F));
        }
    }
}
