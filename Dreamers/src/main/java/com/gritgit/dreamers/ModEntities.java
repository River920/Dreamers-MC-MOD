package com.gritgit.dreamers;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Dreamers.MODID);

    public static final RegistryObject<EntityType<com.gritgit.dreamers.entity.DreamtEntity>> DREAMT = ENTITIES.register("dreamt",
            () -> EntityType.Builder.of(com.gritgit.dreamers.entity.DreamtEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .build(Dreamers.MODID + ":dreamt"));
}
