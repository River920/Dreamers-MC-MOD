package com.gritgit.dreamers;

import com.gritgit.dreamers.entity.DreamtEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Dreamers.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DreamtSpawner {
    private static int tickCounter = 0;
    private static final int TICKS_PER_MINUTE = 1200;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter < TICKS_PER_MINUTE) return;
        tickCounter = 0;

        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        // only spawn one Dreamt globally
        for (ServerLevel level : server.getAllLevels()) {
            boolean exists = false;
            for (var ent : level.entitiesForRendering()) {
                if (ent instanceof DreamtEntity) { exists = true; break; }
            }
            if (exists) continue;
            List<ServerPlayer> players = server.getPlayerList().getPlayers();
            if (players.isEmpty()) continue;
            ServerPlayer p = players.get(new Random().nextInt(players.size()));
            ServerLevel lvl = p.getLevel();

            // try up to 10 positions within 50 blocks
            Random rnd = new Random();
            boolean spawned = false;
            for (int i = 0; i < 10 && !spawned; i++) {
                double angle = rnd.nextDouble() * Math.PI * 2.0;
                double dist = rnd.nextDouble() * 50.0;
                int x = (int)Math.round(p.getX() + Math.cos(angle) * dist);
                int z = (int)Math.round(p.getZ() + Math.sin(angle) * dist);
                int y = lvl.getHeight(net.minecraft.world.level.LevelHeightAccessor.MOTION_BLOCKING_NO_LEAVES, x, z);
                BlockPos pos = new BlockPos(x, y + 1, z);
                if (!lvl.getBlockState(pos).isAir() && lvl.getBlockState(pos).getMaterial().isLiquid()) continue;
                DreamtEntity dreamt = new DreamtEntity(ModEntities.DREAMT.get(), lvl);
                dreamt.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0f, 0f);
                lvl.addFreshEntity(dreamt);

                // grant advancement to chosen player
                var adv = server.getPlayerList().getAdvancementManager().getAdvancement(new ResourceLocation(Dreamers.MODID, "am_i_dreaming"));
                if (adv != null) {
                    var progress = p.getAdvancements().getOrStartProgress(adv);
                    if (!progress.isDone()) {
                        var rewards = adv.getRewards();
                        p.getAdvancements().award(adv, "impossible");
                    }
                }

                // play dreamy sound to the player
                var dreamy = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Dreamers.MODID, "dreamy"));
                if (dreamy != null) {
                    lvl.playSound(null, p.blockPosition(), dreamy, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                }

                spawned = true;
            }
        }
    }
}
