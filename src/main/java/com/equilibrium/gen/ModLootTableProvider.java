package com.equilibrium.gen;

import com.equilibrium.block.ModBlocks;
import com.equilibrium.craft_time_register.BlockInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLootTableProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ModLootTableProvider extends FabricBlockLootTableProvider {

    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }


    @Override
    public void generate() {
        addDrop(ModBlocks.GOLD_BLOCK);
        addDrop(ModBlocks.SILVER_BLOCK);
        addDrop(ModBlocks.ANCIENT_METAL_BLOCK);
        addDrop(ModBlocks.COPPER_BLOCK);
        addDrop(ModBlocks.ADAMANTIUM_BLOCK);
        addDrop(ModBlocks.MITHRIL_BLOCK);

        addDrop(BlockInit.CLAY_FURNACE);
        addDrop(BlockInit.OBSIDIAN_FURNACE);
        addDrop(BlockInit.NETHERRACK_FURNACE);

        addDrop(BlockInit.FLINT_CRAFTING_TABLE);
        addDrop(BlockInit.COPPER_CRAFTING_TABLE);
        addDrop(BlockInit.SILVER_CRAFTING_TABLE);
        addDrop(BlockInit.DIAMOND_CRAFTING_TABLE);
        addDrop(BlockInit.IRON_CRAFTING_TABLE);
        addDrop(BlockInit.NETHERITE_CRAFTING_TABLE);

    }

}
