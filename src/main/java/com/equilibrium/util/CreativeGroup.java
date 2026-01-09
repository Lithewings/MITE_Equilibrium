package com.equilibrium.util;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.craft_time_register.BlockInit;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CreativeGroup {
    public static void addGroup() {
        Registry.register(Registries.ITEM_GROUP, id("crafttime.group"), FabricItemGroup.builder()
                .icon(() -> new ItemStack(BlockInit.DIAMOND_CRAFTING_TABLE))
                .displayName(Text.translatable("crafttime.group"))
                .entries((context, entries) -> {
                    entries.add(BlockInit.FLINT_CRAFTING_TABLE);
                    entries.add(BlockInit.COPPER_CRAFTING_TABLE);
                    entries.add(BlockInit.IRON_CRAFTING_TABLE);
                    entries.add(BlockInit.DIAMOND_CRAFTING_TABLE);
                    entries.add(BlockInit.NETHERITE_CRAFTING_TABLE);
                    entries.add(BlockInit.CLAY_FURNACE);
                    entries.add(BlockInit.OBSIDIAN_FURNACE);
                    entries.add(BlockInit.NETHERRACK_FURNACE);
                })
                .build());
    }

    public static Identifier id(String path) {
        return  Identifier.of(OnServerInitialize.MOD_ID, path);
    }
}
