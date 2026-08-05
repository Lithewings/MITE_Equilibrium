package com.equilibrium.item;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.block.anvil.AnvilBlocks;
import com.equilibrium.block.crafting_table.CraftingTableBlocks;
import com.equilibrium.block.enchanting_table.EnchantingTableBlocks;
import com.equilibrium.block.furnace.FurnaceBlocks;
import com.equilibrium.block.material.MaterialBlocks;
import com.equilibrium.block.miscellaneous.MiscellaneousBlocks;
import com.equilibrium.block.ore.OreBlocks;
import com.equilibrium.item.armor.ArmorItems;
import com.equilibrium.item.coin.CoinItems;
import com.equilibrium.item.food.FoodItems;
import com.equilibrium.item.material.MaterialItems;
import com.equilibrium.item.miscellaneous.MiscellaneousItems;
import com.equilibrium.item.tool.ToolItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.FurnaceBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItemGroups {
    // 创建一个 DeferredRegister 专门管理创意标签页
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, OnServerInitialize.MOD_ID);

    // 方块 / 金属 / 杂项
    public static final Supplier<CreativeModeTab> BLOCKS = TABS.register("blockgroup", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("mod.itemGroup.blocks_and_metallic_items"))
                    .icon(() -> new ItemStack(CraftingTableBlocks.FLINT_CRAFTING_TABLE))
                    .displayItems((params, output) -> {
                        output.accept(MiscellaneousBlocks.MUNDANE_GRAVEL);
                        output.accept(CraftingTableBlocks.FLINT_CRAFTING_TABLE);
                        output.accept(CraftingTableBlocks.COPPER_CRAFTING_TABLE);
                        output.accept(CraftingTableBlocks.SILVER_CRAFTING_TABLE);
                        output.accept(CraftingTableBlocks.IRON_CRAFTING_TABLE);
                        output.accept(CraftingTableBlocks.DIAMOND_CRAFTING_TABLE);
                        output.accept(CraftingTableBlocks.NETHERITE_CRAFTING_TABLE);

                        output.accept(FurnaceBlocks.CLAY_FURNACE);
                        output.accept(FurnaceBlocks.OBSIDIAN_FURNACE);
                        output.accept(FurnaceBlocks.NETHERRACK_FURNACE);

                        output.accept(MaterialBlocks.SILVER_BLOCK);
                        output.accept(MaterialBlocks.COPPER_BLOCK);
                        output.accept(MaterialBlocks.ADAMANTIUM_BLOCK);
                        output.accept(MaterialBlocks.ANCIENT_METAL_BLOCK);
                        output.accept(MaterialBlocks.MITHRIL_BLOCK);
                        output.accept(MaterialBlocks.GOLD_BLOCK);

                        output.accept(OreBlocks.GOLD_ORE);
                        output.accept(OreBlocks.ADAMANTIUM_ORE);
                        output.accept(OreBlocks.COPPER_ORE);
                        output.accept(OreBlocks.MITHRIL_ORE);
                        output.accept(OreBlocks.SILVER_ORE);
                        output.accept(EnchantingTableBlocks.EMERALD_ENCHANTING_TABLE);
                        output.accept(EnchantingTableBlocks.DIAMOND_ENCHANTING_TABLE);

                        output.accept(AnvilBlocks.IRON_ANVIL);
                        output.accept(AnvilBlocks.MITHRIL_ANVIL);
                        output.accept(AnvilBlocks.ADAMANTIUM_ANVIL);

                        output.accept(MaterialItems.ADAMANTIUM_INGOT.get());
                        output.accept(MaterialItems.COPPER_INGOT.get());
                        output.accept(MaterialItems.ANCIENT_METAL_INGOT.get());
                        output.accept(MaterialItems.GOLD_INGOT.get());
                        output.accept(MaterialItems.MITHRIL_INGOT.get());
                        output.accept(MaterialItems.SILVER_INGOT.get());
                        output.accept(MaterialItems.FLINT.get());

                        output.accept(MaterialItems.ADAMANTIUM_NUGGET.get());
                        output.accept(MaterialItems.ANCIENT_METAL_NUGGET.get());
                        output.accept(MaterialItems.COPPER_NUGGET.get());
                        output.accept(MaterialItems.GOLD_NUGGET.get());
                        output.accept(MaterialItems.SILVER_NUGGET.get());
                        output.accept(MaterialItems.MITHRIL_NUGGET.get());

                        output.accept(MaterialItems.RAW_ADAMANTIUM.get());
                        output.accept(MaterialItems.RAW_MITHRIL.get());
                        output.accept(MaterialItems.RAW_SILVER.get());

                        output.accept(CoinItems.COPPER_COIN.get());
                        output.accept(CoinItems.IRON_COIN.get());

                        output.accept(MiscellaneousItems.DIAMOND_SHARD.get());
                        output.accept(MiscellaneousItems.EMERALD_SHARD.get());
                        output.accept(MiscellaneousItems.GLASS_FRAGMENT.get());
                    })
                    .build()
    );

    // 工具栏
    public static final Supplier<CreativeModeTab> TOOLS = TABS.register("toolsgroup", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("mod.itemGroup.armors_and_tools"))
                    .icon(() -> new ItemStack(ToolItems.ADAMANTIUM_PICKAXE.get()))   // 若 Tools 已改为 DeferredItem 则需 .get()
                    .displayItems((params, output) -> {
                        output.accept(ToolItems.FLINT_AXE.get());
                        output.accept(ToolItems.FLINT_HATCHET.get());
                        output.accept(ToolItems.FLINT_KNIFE.get());
                        output.accept(ToolItems.FLINT_SHOVEL.get());

                        output.accept(ToolItems.COPPER_AXE.get());
                        output.accept(ToolItems.COPPER_DAGGER.get());
                        output.accept(ToolItems.COPPER_HAMMER.get());
                        output.accept(ToolItems.COPPER_HOE.get());
                        output.accept(ToolItems.COPPER_PICKAXE.get());
                        output.accept(ToolItems.COPPER_SHOVEL.get());
                        output.accept(ToolItems.COPPER_SWORD.get());

                        output.accept(ToolItems.GOLD_AXE.get());
                        output.accept(ToolItems.GOLD_DAGGER.get());
                        output.accept(ToolItems.GOLD_HAMMER.get());
                        output.accept(ToolItems.GOLD_HOE.get());
                        output.accept(ToolItems.GOLD_PICKAXE.get());
                        output.accept(ToolItems.GOLD_SHOVEL.get());
                        output.accept(ToolItems.GOLD_SWORD.get());

                        output.accept(ToolItems.SILVER_AXE.get());
                        output.accept(ToolItems.SILVER_DAGGER.get());
                        output.accept(ToolItems.SILVER_HAMMER.get());
                        output.accept(ToolItems.SILVER_HOE.get());
                        output.accept(ToolItems.SILVER_PICKAXE.get());
                        output.accept(ToolItems.SILVER_SHOVEL.get());
                        output.accept(ToolItems.SILVER_SWORD.get());

                        output.accept(ToolItems.IRON_AXE.get());
                        output.accept(ToolItems.IRON_DAGGER.get());
                        output.accept(ToolItems.IRON_HAMMER.get());
                        output.accept(ToolItems.IRON_HOE.get());
                        output.accept(ToolItems.IRON_PICKAXE.get());
                        output.accept(ToolItems.IRON_SHOVEL.get());
                        output.accept(ToolItems.IRON_SWORD.get());

                        output.accept(ToolItems.MITHRIL_AXE.get());
                        output.accept(ToolItems.MITHRIL_DAGGER.get());
                        output.accept(ToolItems.MITHRIL_HAMMER.get());
                        output.accept(ToolItems.MITHRIL_HOE.get());
                        output.accept(ToolItems.MITHRIL_SHOVEL.get());
                        output.accept(ToolItems.MITHRIL_SWORD.get());
                        output.accept(ToolItems.MITHRIL_PICKAXE.get());

                        output.accept(ToolItems.ADAMANTIUM_AXE.get());
                        output.accept(ToolItems.ADMANTIUM_DAGGER.get());
                        output.accept(ToolItems.ADAMANTIUM_HOE.get());
                        output.accept(ToolItems.ADAMANTIUM_HAMMER.get());
                        output.accept(ToolItems.ADAMANTIUM_SHOVEL.get());
                        output.accept(ToolItems.ADMANTIUM_SWORD.get());
                        output.accept(ToolItems.ADAMANTIUM_PICKAXE.get());

                        // Armors 为普通 Item 字段，无需 .get()
                        output.accept(ArmorItems.COPPER_HELMET.get());
                        output.accept(ArmorItems.COPPER_CHEST_PLATE.get());
                        output.accept(ArmorItems.COPPER_LEGGINGS.get());
                        output.accept(ArmorItems.COPPER_BOOTS.get());

                        output.accept(ArmorItems.MITHRIL_HELMET.get());
                        output.accept(ArmorItems.MITHRIL_CHEST_PLATE.get());
                        output.accept(ArmorItems.MITHRIL_LEGGINGS.get());
                        output.accept(ArmorItems.MITHRIL_BOOTS.get());

                        output.accept(ArmorItems.ANCIENT_METAL_CHAINMAIL_HELMET.get());
                        output.accept(ArmorItems.ANCIENT_METAL_CHAINMAIL_CHEST_PLATE.get());
                        output.accept(ArmorItems.ANCIENT_METAL_CHAINMAIL_LEGGINGS.get());
                        output.accept(ArmorItems.ANCIENT_METAL_CHAINMAIL_BOOTS.get());
                    })
                    .build()
    );

    // 农业
    public static final Supplier<CreativeModeTab> FARM = TABS.register("farmgroup", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("mod.itemGroup.farm"))
                    .icon(() -> new ItemStack(FoodItems.SALAD.get()))
                    .displayItems((params, output) -> {
                        output.accept(FoodItems.CHOCOLATE.get());
                        output.accept(FoodItems.PUMPKIN_SOUP.get());
                        output.accept(FoodItems.WATER_BOWL.get());
                        output.accept(FoodItems.VEGETABLE_SOUP.get());
                        output.accept(FoodItems.BEEF_SOUP.get());
                        output.accept(FoodItems.MASHED_POTATO.get());
                        output.accept(FoodItems.MILK_BOWL.get());
                        output.accept(FoodItems.CHEESE.get());
                        output.accept(FoodItems.ONION.get());
                        output.accept(FoodItems.SALAD.get());
                        output.accept(FoodItems.MANURE.get());
                    })
                    .build()
    );
}