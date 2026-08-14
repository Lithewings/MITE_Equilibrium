package com.equilibrium.item;

import com.equilibrium.OnServerInitialize;

import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.block.ModBlocksRegistry2;
import com.equilibrium.item.extend_item.CoinItems;
import com.equilibrium.item.food.FoodOrFarmItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    //方块
    public static final ItemGroup BLOCKS = Registry.register(Registries.ITEM_GROUP, Identifier.of(OnServerInitialize.MOD_ID,"blockgroup"),
            FabricItemGroup.builder().displayName(Text.translatable("mod.itemGroup.blocks_and_metallic_items"))
                    .icon(()->new ItemStack(ModBlocksRegistry2.FLINT_CRAFTING_TABLE)).entries((displayContext, entries) ->
                            {
                                entries.add(ModBlocksRegistry.MUNDANE_GRAVEL);
                                entries.add(ModBlocksRegistry2.FLINT_CRAFTING_TABLE);
                                entries.add(ModBlocksRegistry2.COPPER_CRAFTING_TABLE);
                                entries.add(ModBlocksRegistry2.SILVER_CRAFTING_TABLE);
                                entries.add(ModBlocksRegistry2.IRON_CRAFTING_TABLE);
                                entries.add(ModBlocksRegistry2.MITHRIL_CRAFTING_TABLE);
                                entries.add(ModBlocksRegistry2.ADAMANTIUM_CRAFTING_TABLE);

                                entries.add(ModBlocksRegistry2.CLAY_FURNACE);
                                entries.add(ModBlocksRegistry2.OBSIDIAN_FURNACE);
                                entries.add(ModBlocksRegistry2.NETHERRACK_FURNACE);


                                entries.add(ModBlocksRegistry.SILVER_BLOCK);
                                entries.add(ModBlocksRegistry.COPPER_BLOCK);
                                entries.add(ModBlocksRegistry.ADAMANTIUM_BLOCK);
                                entries.add(ModBlocksRegistry.ANCIENT_METAL_BLOCK);
                                entries.add(ModBlocksRegistry.MITHRIL_BLOCK);
                                entries.add(ModBlocksRegistry.GOLD_BLOCK);

                                entries.add(ModBlocksRegistry.GOLD_ORE);
                                entries.add(ModBlocksRegistry.ADAMANTIUM_ORE);
                                entries.add(ModBlocksRegistry.COPPER_ORE);
                                entries.add(ModBlocksRegistry.MITHRIL_ORE);
                                entries.add(ModBlocksRegistry.SILVER_ORE);
                                entries.add(ModBlocksRegistry.EMERALD_ENCHANTING_TABLE);
                                entries.add(ModBlocksRegistry.DIAMOND_ENCHANTING_TABLE);


                                entries.add(ModBlocksRegistry.IRON_ANVIL);
                                entries.add(ModBlocksRegistry.MITHRIL_ANVIL);
                                entries.add(ModBlocksRegistry.ADAMANTIUM_ANVIL);

                                entries.add(Metal.adamantium);
                                entries.add(Metal.copper);
                                entries.add(Metal.ancient_metal);
                                entries.add(Metal.gold);
                                entries.add(Metal.mithril);
                                entries.add(Metal.silver);
                                entries.add(Metal.FLINT);

                                entries.add(Metal.adamantium_nugget);
                                entries.add(Metal.ancient_metal_nugget);
                                entries.add(Metal.copper_nugget);
                                entries.add(Metal.gold_nugget);
                                entries.add(Metal.silver_nugget);
                                entries.add(Metal.mithril_nugget);





                                entries.add(Metal.ADAMANTIUM_RAW);
                                entries.add(Metal.MITHRIL_RAW);
                                entries.add(Metal.SILVER_RAW);



                                entries.add(CoinItems.COPPER_COIN);
                                entries.add(CoinItems.IRON_COIN);

                                entries.add(OtherItems.DIAMOND_SHARD);
                                entries.add(OtherItems.EMERALD_SHARD);
                                entries.add(OtherItems.GLASS_FRAGMENT);
                                entries.add(OtherItems.SINEW);
//                                entries.add(OtherItems.PUDDING_SLIME_BALL);

                            }
                    ).build());



    //工具栏
    public static final ItemGroup TOOLS = Registry.register(Registries.ITEM_GROUP, Identifier.of(OnServerInitialize.MOD_ID,"toolsgroup"),
            FabricItemGroup.builder().displayName(Text.translatable("mod.itemGroup.armors_and_tools"))
                    .icon(()->new ItemStack(Tools.ADAMANTIUM_PICKAXE)).entries((displayContext, entries) ->
                            {

                                entries.add(Tools.FLINT_AXE);
                                entries.add(Tools.FLINT_HATCHET);
                                entries.add(Tools.FLINT_KNIFE);
                                entries.add(Tools.FLINT_SHOVEL);

                                entries.add(Tools.COPPER_AXE);
                                entries.add(Tools.COPPER_DAGGER);
                                entries.add(Tools.COPPER_HAMMER);
                                entries.add(Tools.COPPER_HOE);
                                entries.add(Tools.COPPER_PICKAXE);
                                entries.add(Tools.COPPER_SHOVEL);
                                entries.add(Tools.COPPER_SWORD);

                                entries.add(Tools.GOLD_AXE);
                                entries.add(Tools.GOLD_DAGGER);
                                entries.add(Tools.GOLD_HAMMER);
                                entries.add(Tools.GOLD_HOE);
                                entries.add(Tools.GOLD_PICKAXE);
                                entries.add(Tools.GOLD_SHOVEL);
                                entries.add(Tools.GOLD_SWORD);

                                entries.add(Tools.SILVER_AXE);
                                entries.add(Tools.SILVER_DAGGER);
                                entries.add(Tools.SILVER_HAMMER);
                                entries.add(Tools.SILVER_HOE);
                                entries.add(Tools.SILVER_PICKAXE);
                                entries.add(Tools.SILVER_SHOVEL);
                                entries.add(Tools.SILVER_SWORD);

                                entries.add(Tools.IRON_AXE);
                                entries.add(Tools.IRON_DAGGER);
                                entries.add(Tools.IRON_HAMMER);
                                entries.add(Tools.IRON_HOE);
                                entries.add(Tools.IRON_PICKAXE);
                                entries.add(Tools.IRON_SHOVEL);
                                entries.add(Tools.IRON_SWORD);

                                entries.add(Tools.MITHRIL_AXE);
                                entries.add(Tools.MITHRIL_DAGGER);
                                entries.add(Tools.MITHRIL_HAMMER);
                                entries.add(Tools.MITHRIL_HOE);
                                entries.add(Tools.MITHRIL_SHOVEL);
                                entries.add(Tools.MITHRIL_SWORD);
                                entries.add(Tools.MITHRIL_PICKAXE);

                                entries.add(Tools.ADAMANTIUM_AXE);
                                entries.add(Tools.ADMANTIUM_DAGGER);
                                entries.add(Tools.ADAMANTIUM_HOE);
                                entries.add(Tools.ADAMANTIUM_HAMMER);
                                entries.add(Tools.ADAMANTIUM_SHOVEL);
                                entries.add(Tools.ADMANTIUM_SWORD);
                                entries.add(Tools.ADAMANTIUM_PICKAXE);

                                entries.add(Armors.COPPER_HELMET);
                                entries.add(Armors.COPPER_CHEST_PLATE);
                                entries.add(Armors.COPPER_LEGGINGS);
                                entries.add(Armors.COPPER_BOOTS);

                                entries.add(Armors.MITHRIL_HELMET);
                                entries.add(Armors.MITHRIL_CHEST_PLATE);
                                entries.add(Armors.MITHRIL_LEGGINGS);
                                entries.add(Armors.MITHRIL_BOOTS);

                                entries.add(Armors.ANCIENT_METAL_CHAINMAIL_HELMET);
                                entries.add(Armors.ANCIENT_METAL_CHAINMAIL_CHEST_PLATE);
                                entries.add(Armors.ANCIENT_METAL_CHAINMAIL_LEGGINGS);
                                entries.add(Armors.ANCIENT_METAL_CHAINMAIL_BOOTS);

                            }
                    ).build());






    //农业
    public static final ItemGroup FARM = Registry.register(Registries.ITEM_GROUP, Identifier.of(OnServerInitialize.MOD_ID,"farmgroup"),
            FabricItemGroup.builder().displayName(Text.translatable("mod.itemGroup.farm"))
                    .icon(()->new ItemStack(FoodOrFarmItems.SALAD)).entries((displayContext, entries) ->
                            {
                                entries.add(FoodOrFarmItems.BLUE_BERRY_BUSH);
                                entries.add(FoodOrFarmItems.BLUE_BERRY);
                                entries.add(FoodOrFarmItems.CHOCOLATE);
                                entries.add(FoodOrFarmItems.PUMPKIN_SOUP);
                                entries.add(FoodOrFarmItems.WATER_BOWL);
                                entries.add(FoodOrFarmItems.VEGETABLE_SOUP);
                                entries.add(FoodOrFarmItems.BEEF_SOUP);
                                entries.add(FoodOrFarmItems.MASHED_POTATO);
                                entries.add(FoodOrFarmItems.MILK_BOWL);
                                entries.add(FoodOrFarmItems.CHEESE);
                                entries.add(FoodOrFarmItems.ONION);
                                entries.add(FoodOrFarmItems.SALAD);
                                entries.add(FoodOrFarmItems.MANURE);
                            }
                    ).build());





    public static void registerModItemGroup(){

    }
}
