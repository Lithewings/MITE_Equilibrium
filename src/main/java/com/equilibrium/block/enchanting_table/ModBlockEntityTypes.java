package com.equilibrium.block.enchanting_table;

import com.equilibrium.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class ModBlockEntityTypes {

    public static BlockEntityType<ModEnchantingTableBlockEntity> ENCHANTING_TABLE_BLOCK_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(MOD_ID, "emerald_enchanting_table.json"),
            BlockEntityType.Builder.create(ModEnchantingTableBlockEntity::new, ModBlocks.EMERALD_ENCHANTING_TABLE).build());


    public static void modBlockEntityTypesInit() {
    }
}
