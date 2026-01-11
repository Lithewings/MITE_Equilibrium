package com.equilibrium.block.furnace_and_its_entity;


import com.equilibrium.OnServerInitialize;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.equilibrium.block.ModBlocksRegistry2.*;

public class FurnaceEntityRegistry {

    public static BlockEntityType<TheFurnaceEntity> THE_FURNACE = register(
            "the_furnace",
            BlockEntityType.Builder.create(TheFurnaceEntity::new,CLAY_FURNACE,OBSIDIAN_FURNACE,NETHERRACK_FURNACE
            ).build(null));

    public static void init() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(OnServerInitialize.MOD_ID, name), type);
    }
}
