package com.equilibrium.item;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.block.ModBlocksRegistry2;
import com.equilibrium.item.extend_item.CoinItems;
import com.equilibrium.item.food.FoodOrFarmItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = OnServerInitialize.MOD_ID)
public class ModItemGroup {

    // 只声明字段，不在此处初始化
    public static CreativeModeTab BLOCKS;
    public static CreativeModeTab TOOLS;
    public static CreativeModeTab FARM;

    @SubscribeEvent
    public static void registerCreativeTabs(RegisterEvent event) {
        event.register(BuiltInRegistries.CREATIVE_MODE_TAB.key(), helper -> {
            // 方块 / 金属 / 杂项
            BLOCKS = Registry.register(
                    BuiltInRegistries.CREATIVE_MODE_TAB,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "blockgroup"),
                    CreativeModeTab.builder()
                            .title(Component.translatable("mod.itemGroup.blocks_and_metallic_items"))
                            .icon(() -> new ItemStack(ModBlocksRegistry2.FLINT_CRAFTING_TABLE))
                            .displayItems((params, output) -> {
                                output.accept(ModBlocksRegistry.MUNDANE_GRAVEL);
                                output.accept(ModBlocksRegistry2.FLINT_CRAFTING_TABLE);
                                output.accept(ModBlocksRegistry2.COPPER_CRAFTING_TABLE);
                                output.accept(ModBlocksRegistry2.SILVER_CRAFTING_TABLE);
                                output.accept(ModBlocksRegistry2.IRON_CRAFTING_TABLE);
                                output.accept(ModBlocksRegistry2.DIAMOND_CRAFTING_TABLE);
                                output.accept(ModBlocksRegistry2.NETHERITE_CRAFTING_TABLE);

                                output.accept(ModBlocksRegistry2.CLAY_FURNACE);
                                output.accept(ModBlocksRegistry2.OBSIDIAN_FURNACE);
                                output.accept(ModBlocksRegistry2.NETHERRACK_FURNACE);

                                output.accept(ModBlocksRegistry.SILVER_BLOCK);
                                output.accept(ModBlocksRegistry.COPPER_BLOCK);
                                output.accept(ModBlocksRegistry.ADAMANTIUM_BLOCK);
                                output.accept(ModBlocksRegistry.ANCIENT_METAL_BLOCK);
                                output.accept(ModBlocksRegistry.MITHRIL_BLOCK);
                                output.accept(ModBlocksRegistry.GOLD_BLOCK);

                                output.accept(ModBlocksRegistry.GOLD_ORE);
                                output.accept(ModBlocksRegistry.ADAMANTIUM_ORE);
                                output.accept(ModBlocksRegistry.COPPER_ORE);
                                output.accept(ModBlocksRegistry.MITHRIL_ORE);
                                output.accept(ModBlocksRegistry.SILVER_ORE);
                                output.accept(ModBlocksRegistry.EMERALD_ENCHANTING_TABLE);
                                output.accept(ModBlocksRegistry.DIAMOND_ENCHANTING_TABLE);

                                output.accept(ModBlocksRegistry.IRON_ANVIL);
                                output.accept(ModBlocksRegistry.MITHRIL_ANVIL);
                                output.accept(ModBlocksRegistry.ADAMANTIUM_ANVIL);

                                output.accept(Metal.adamantium);
                                output.accept(Metal.copper);
                                output.accept(Metal.ancient_metal);
                                output.accept(Metal.gold);
                                output.accept(Metal.mithril);
                                output.accept(Metal.silver);
                                output.accept(Metal.FLINT);

                                output.accept(Metal.adamantium_nugget);
                                output.accept(Metal.ancient_metal_nugget);
                                output.accept(Metal.copper_nugget);
                                output.accept(Metal.gold_nugget);
                                output.accept(Metal.silver_nugget);
                                output.accept(Metal.mithril_nugget);

                                output.accept(Metal.ADAMANTIUM_RAW);
                                output.accept(Metal.MITHRIL_RAW);
                                output.accept(Metal.SILVER_RAW);

                                output.accept(CoinItems.COPPER_COIN);
                                output.accept(CoinItems.IRON_COIN);

                                output.accept(OtherItems.DIAMOND_SHARD);
                                output.accept(OtherItems.EMERALD_SHARD);
                                output.accept(OtherItems.GLASS_FRAGMENT);
                            })
                            .build()
            );

            // 工具栏
            TOOLS = Registry.register(
                    BuiltInRegistries.CREATIVE_MODE_TAB,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "toolsgroup"),
                    CreativeModeTab.builder()
                            .title(Component.translatable("mod.itemGroup.armors_and_tools"))
                            .icon(() -> new ItemStack(Tools.ADAMANTIUM_PICKAXE))
                            .displayItems((params, output) -> {
                                output.accept(Tools.FLINT_AXE);
                                output.accept(Tools.FLINT_HATCHET);
                                output.accept(Tools.FLINT_KNIFE);
                                output.accept(Tools.FLINT_SHOVEL);

                                output.accept(Tools.COPPER_AXE);
                                output.accept(Tools.COPPER_DAGGER);
                                output.accept(Tools.COPPER_HAMMER);
                                output.accept(Tools.COPPER_HOE);
                                output.accept(Tools.COPPER_PICKAXE);
                                output.accept(Tools.COPPER_SHOVEL);
                                output.accept(Tools.COPPER_SWORD);

                                output.accept(Tools.GOLD_AXE);
                                output.accept(Tools.GOLD_DAGGER);
                                output.accept(Tools.GOLD_HAMMER);
                                output.accept(Tools.GOLD_HOE);
                                output.accept(Tools.GOLD_PICKAXE);
                                output.accept(Tools.GOLD_SHOVEL);
                                output.accept(Tools.GOLD_SWORD);

                                output.accept(Tools.SILVER_AXE);
                                output.accept(Tools.SILVER_DAGGER);
                                output.accept(Tools.SILVER_HAMMER);
                                output.accept(Tools.SILVER_HOE);
                                output.accept(Tools.SILVER_PICKAXE);
                                output.accept(Tools.SILVER_SHOVEL);
                                output.accept(Tools.SILVER_SWORD);

                                output.accept(Tools.IRON_AXE);
                                output.accept(Tools.IRON_DAGGER);
                                output.accept(Tools.IRON_HAMMER);
                                output.accept(Tools.IRON_HOE);
                                output.accept(Tools.IRON_PICKAXE);
                                output.accept(Tools.IRON_SHOVEL);
                                output.accept(Tools.IRON_SWORD);

                                output.accept(Tools.MITHRIL_AXE);
                                output.accept(Tools.MITHRIL_DAGGER);
                                output.accept(Tools.MITHRIL_HAMMER);
                                output.accept(Tools.MITHRIL_HOE);
                                output.accept(Tools.MITHRIL_SHOVEL);
                                output.accept(Tools.MITHRIL_SWORD);
                                output.accept(Tools.MITHRIL_PICKAXE);

                                output.accept(Tools.ADAMANTIUM_AXE);
                                output.accept(Tools.ADMANTIUM_DAGGER);
                                output.accept(Tools.ADAMANTIUM_HOE);
                                output.accept(Tools.ADAMANTIUM_HAMMER);
                                output.accept(Tools.ADAMANTIUM_SHOVEL);
                                output.accept(Tools.ADMANTIUM_SWORD);
                                output.accept(Tools.ADAMANTIUM_PICKAXE);

                                output.accept(Armors.COPPER_HELMET);
                                output.accept(Armors.COPPER_CHEST_PLATE);
                                output.accept(Armors.COPPER_LEGGINGS);
                                output.accept(Armors.COPPER_BOOTS);

                                output.accept(Armors.MITHRIL_HELMET);
                                output.accept(Armors.MITHRIL_CHEST_PLATE);
                                output.accept(Armors.MITHRIL_LEGGINGS);
                                output.accept(Armors.MITHRIL_BOOTS);

                                output.accept(Armors.ANCIENT_METAL_CHAINMAIL_HELMET);
                                output.accept(Armors.ANCIENT_METAL_CHAINMAIL_CHEST_PLATE);
                                output.accept(Armors.ANCIENT_METAL_CHAINMAIL_LEGGINGS);
                                output.accept(Armors.ANCIENT_METAL_CHAINMAIL_BOOTS);
                            })
                            .build()
            );

            // 农业
            FARM = Registry.register(
                    BuiltInRegistries.CREATIVE_MODE_TAB,
                    ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "farmgroup"),
                    CreativeModeTab.builder()
                            .title(Component.translatable("mod.itemGroup.farm"))
                            .icon(() -> new ItemStack(FoodOrFarmItems.SALAD))
                            .displayItems((params, output) -> {
                                output.accept(FoodOrFarmItems.CHOCOLATE);
                                output.accept(FoodOrFarmItems.PUMPKIN_SOUP);
                                output.accept(FoodOrFarmItems.WATER_BOWL);
                                output.accept(FoodOrFarmItems.VEGETABLE_SOUP);
                                output.accept(FoodOrFarmItems.BEEF_SOUP);
                                output.accept(FoodOrFarmItems.MASHED_POTATO);
                                output.accept(FoodOrFarmItems.MILK_BOWL);
                                output.accept(FoodOrFarmItems.CHEESE);
                                output.accept(FoodOrFarmItems.ONION);
                                output.accept(FoodOrFarmItems.SALAD);
                                output.accept(FoodOrFarmItems.MANURE);
                            })
                            .build()
            );
        });
    }
}