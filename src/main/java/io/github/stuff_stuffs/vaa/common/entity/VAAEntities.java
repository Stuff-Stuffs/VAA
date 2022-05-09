package io.github.stuff_stuffs.vaa.common.entity;

import io.github.stuff_stuffs.vaa.common.VAA;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.registry.Registry;

public class VAAEntities {
    public static final EntityType<TestEntity> TEST_ENTITY_TYPE = FabricEntityTypeBuilder.create(SpawnGroup.MISC, TestEntity::new).dimensions(new EntityDimensions(0.25f, 2f, true)).build();


    private static <T extends Entity> EntityType<T> register(final String name, final EntityType<T> builder) {
        return Registry.register(Registry.ENTITY_TYPE, VAA.createId(name), builder);
    }

    public static void init() {
        register("test", TEST_ENTITY_TYPE);
    }

    static {
        FabricDefaultAttributeRegistry.register(TEST_ENTITY_TYPE, MobEntity.createMobAttributes());
    }
}
