package com.equilibrium.block.furnace_and_its_entity;


import com.equilibrium.OnServerInitialize;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.equilibrium.block.ModBlocksRegistry2.*;

public class FurnaceEntityRegistry {

    // 1. 创建 DeferredRegister 实例
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OnServerInitialize.MOD_ID);

    // 2. 注册方块实体类型，返回 Supplier 或 DeferredHolder
    public static final Supplier<BlockEntityType<TheFurnaceEntity>> THE_FURNACE =
            BLOCK_ENTITY_TYPES.register("the_furnace",
                    () -> BlockEntityType.Builder.of(TheFurnaceEntity::new,
                            CLAY_FURNACE,
                            OBSIDIAN_FURNACE,
                            NETHERRACK_FURNACE
                    ).build(null)
            );
}
