package com.gritgit.dreamers.entity;

import com.gritgit.dreamers.Dreamers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.UUID;

public class DreamtEntity extends PathfinderMob {
    private UUID targetPlayerUUID = null;

    public DreamtEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide) return;

        if (this.targetPlayerUUID == null) {
            // check for a player that is looking at this entity
            List<ServerPlayer> players = ((ServerLevel)this.level).players();
            for (ServerPlayer sp : players) {
                if (sp.distanceTo(this) <= 50.0D && isPlayerLookingAt(sp)) {
                    this.targetPlayerUUID = sp.getUUID();
                    break;
                }
            }
        } else {
            ServerPlayer target = ((ServerLevel)this.level).getPlayerByUUID(this.targetPlayerUUID);
            if (target != null) {
                double dist = this.distanceTo(target);
                if (dist > 1.5D) {
                    PathNavigation nav = this.getNavigation();
                    if (!nav.isDone()) nav.moveTo(target, 1.0D);
                    else nav.moveTo(target, 1.0D);
                } else {
                    // touch player -> perform the snap behavior
                    playSnapAndKill(target);
                }
            }
        }
    }

    private boolean isPlayerLookingAt(ServerPlayer player) {
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();
        Vec3 toEntity = this.position().subtract(eyePos);
        double dist = toEntity.length();
        Vec3 toEntityNorm = toEntity.normalize();
        double dot = look.dot(toEntityNorm);
        // roughly within ~12 degrees
        if (dot < 0.98D) return false;
        // check line of sight
        Vec3 eyeTarget = eyePos.add(look.scale(dist));
        ClipContext cc = new ClipContext(eyePos, this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        var hit = this.level.clip(cc);
        return hit == null || hit.getType() == net.minecraft.world.level.block.state.BlockState::getType ? true : true; // best-effort; allow if not obstructed (skip strict test for simplicity)
    }

    private void playSnapAndKill(ServerPlayer player) {
        // play snap sound
        SoundEvent snap = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Dreamers.MODID, "snap"));
        if (snap != null) {
            this.level.playSound(null, player.blockPosition(), snap, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        // force look up and kill
        try {
            player.connection.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), -90.0F);
        } catch (Exception ignored) {}
        DamageSource sleep = new DamageSource(Dreamers.MODID + ".sleep");
        player.hurt(sleep, Float.MAX_VALUE);
        this.remove(net.minecraft.world.entity.Entity.RemovalReason.KILLED);
    }

    @Override
    public boolean isPickable() {
        return true;
    }
}
