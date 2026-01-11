package com.equilibrium.util;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.block.ModBlocksRegistry2;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CreativeGroup {
    public static void addGroup() {
        Registry.register(Registries.ITEM_GROUP, id("crafttime.group"), FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModBlocksRegistry2.DIAMOND_CRAFTING_TABLE))
                .displayName(Text.translatable("crafttime.group"))
                .entries((context, entries) -> {
                    entries.add(ModBlocksRegistry2.FLINT_CRAFTING_TABLE);
                    entries.add(ModBlocksRegistry2.COPPER_CRAFTING_TABLE);
                    entries.add(ModBlocksRegistry2.IRON_CRAFTING_TABLE);
                    entries.add(ModBlocksRegistry2.DIAMOND_CRAFTING_TABLE);
                    entries.add(ModBlocksRegistry2.NETHERITE_CRAFTING_TABLE);
                    entries.add(ModBlocksRegistry2.CLAY_FURNACE);
                    entries.add(ModBlocksRegistry2.OBSIDIAN_FURNACE);
                    entries.add(ModBlocksRegistry2.NETHERRACK_FURNACE);
                })
                .build());
    }

    public static Identifier id(String path) {
        return  Identifier.of(OnServerInitialize.MOD_ID, path);
    }
}
