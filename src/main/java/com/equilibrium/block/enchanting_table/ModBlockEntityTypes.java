package com.equilibrium.block.enchanting_table;

import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.block.enchanting_table.diamond.DiamondEnchantingTableBlockEntity;
import com.equilibrium.block.enchanting_table.emerald.EmeraldEnchantingTableBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class ModBlockEntityTypes {

    public static BlockEntityType<EmeraldEnchantingTableBlockEntity> EMERALD_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(MOD_ID, "emerald_enchanting_table"),
            BlockEntityType.Builder.create(EmeraldEnchantingTableBlockEntity::new, ModBlocksRegistry.EMERALD_ENCHANTING_TABLE).build());

    public static BlockEntityType<DiamondEnchantingTableBlockEntity> DIAMOND_ENCHANTING_TABLE_BLOCK_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(MOD_ID, "diamond_enchanting_table"),
            BlockEntityType.Builder.create(DiamondEnchantingTableBlockEntity::new, ModBlocksRegistry.DIAMOND_ENCHANTING_TABLE).build());

    public static void modBlockEntityTypesInit() {
    }
}
