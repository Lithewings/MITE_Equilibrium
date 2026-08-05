package com.equilibrium.block.furnace;


import com.equilibrium.OnServerInitialize;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.equilibrium.block.furnace.FurnaceBlocks.*;

public class FurnaceEntityRegistry {

    public static BlockEntityType<TheFurnaceEntity> THE_FURNACE = register(
            "the_furnace",
            BlockEntityType.Builder.of(TheFurnaceEntity::new,CLAY_FURNACE.get(),OBSIDIAN_FURNACE.get(),NETHERRACK_FURNACE.get()
            ).build(null));

    public static void init() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, name), type);
    }
}
