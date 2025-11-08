package com.equilibrium.item;

import com.equilibrium.MITEequilibrium;

import com.equilibrium.block.ModBlocks;
import com.equilibrium.item.extend_item.CoinItems;
import com.equilibrium.item.food.FoodItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    //自定义物品栏
    public static final ItemGroup modGroup = Registry.register(Registries.ITEM_GROUP, Identifier.of(MITEequilibrium.MOD_ID,"testgroup"),
            //注册名小写
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.testgroup"))
                    //itemgroup.testgroup是不加翻译前的物品栏名字
                    .icon(()->new ItemStack(ModItems.test)).entries((displayContext, entries) ->
                    //这里开始添加物品
                            {
                                entries.add(ModItems.test);
                                entries.add(Items.BOOK);//可以加原版物品
                                entries.add(ModBlocks.EXAMPLE_BLOCK);

                            }
                    ).build());

    //工具栏
    public static final ItemGroup modTools = Registry.register(Registries.ITEM_GROUP, Identifier.of(MITEequilibrium.MOD_ID,"toolsgroup"),
            FabricItemGroup.builder().displayName(Text.of("Metal Craft"))
                    .icon(()->new ItemStack(Tools.ADAMANTIUM_AXE)).entries((displayContext, entries) ->
                            {
                            }
                    ).build());

    //金属栏
    public static final ItemGroup modIngots = Registry.register(Registries.ITEM_GROUP, Identifier.of(MITEequilibrium.MOD_ID,"metalgroup"),
            FabricItemGroup.builder().displayName(Text.of("Mod Craft"))
                    .icon(()->new ItemStack(Metal.adamantium)).entries((displayContext, entries) ->
                            {
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

                                entries.add(ModBlocks.SILVER_BLOCK);
                                entries.add(ModBlocks.COPPER_BLOCK);
                                entries.add(ModBlocks.ADAMANTIUM_BLOCK);
                                entries.add(ModBlocks.ANCIENT_METAL_BLOCK);
                                entries.add(ModBlocks.MITHRIL_BLOCK);
                                entries.add(ModBlocks.GOLD_BLOCK);

                                entries.add(ModBlocks.GOLD_ORE);
                                entries.add(ModBlocks.ADAMANTIUM_ORE);
                                entries.add(ModBlocks.COPPER_ORE);
                                entries.add(ModBlocks.MITHRIL_ORE);
                                entries.add(ModBlocks.SILVER_ORE);

                                entries.add(Tools.FLINT_HATCHET);
                                entries.add(Tools.FLINT_AXE);
                                entries.add(Tools.FLINT_KNIFE);
                                entries.add(Tools.FLINT_SHOVEL);

                                entries.add(Tools.ADAMANTIUM_AXE);
                                entries.add(Tools.COPPER_AXE);
                                entries.add(Tools.GOLD_AXE);
                                entries.add(Tools.MITHRIL_AXE);
                                entries.add(Tools.SILVER_AXE);
                                entries.add(Tools.IRON_AXE);

                                entries.add(Tools.ADAMANTIUM_HOE);
                                entries.add(Tools.COPPER_HOE);
                                entries.add(Tools.GOLD_HOE);
                                entries.add(Tools.MITHRIL_HOE);
                                entries.add(Tools.SILVER_HOE);
                                entries.add(Tools.IRON_HOE);

                                entries.add(Tools.ADAMANTIUM_SHOVEL);
                                entries.add(Tools.COPPER_SHOVEL);
                                entries.add(Tools.GOLD_SHOVEL);
                                entries.add(Tools.MITHRIL_SHOVEL);
                                entries.add(Tools.SILVER_SHOVEL);
                                entries.add(Tools.IRON_SHOVEL);

                                entries.add(Tools.ADMANTIUM_SWORD);
                                entries.add(Tools.COPPER_SWORD);
                                entries.add(Tools.GOLD_SWORD);
                                entries.add(Tools.MITHRIL_SWORD);
                                entries.add(Tools.SILVER_SWORD);
                                entries.add(Tools.IRON_SWORD);

                                entries.add(Tools.ADAMANTIUM_PICKAXE);
                                entries.add(Tools.GOLD_PICKAXE);
                                entries.add(Tools.COPPER_PICKAXE);
                                entries.add(Tools.IRON_PICKAXE);
                                entries.add(Tools.MITHRIL_PICKAXE);
                                entries.add(Tools.SILVER_PICKAXE);

                                entries.add(Tools.ADMANTIUM_DAGGER);
                                entries.add(Tools.GOLD_DAGGER);
                                entries.add(Tools.COPPER_DAGGER);
                                entries.add(Tools.IRON_DAGGER);
                                entries.add(Tools.MITHRIL_DAGGER);
                                entries.add(Tools.SILVER_DAGGER);


                                entries.add(Metal.ADAMANTIUM_RAW);
                                entries.add(Metal.MITHRIL_RAW);
                                entries.add(Metal.SILVER_RAW);

                                entries.add(Armors.COPPER_HELMET);
                                entries.add(Armors.COPPER_BOOTS);
                                entries.add(Armors.COPPER_CHEST_PLATE);
                                entries.add(Armors.COPPER_LEGGINGS);

                                entries.add(Armors.MITHRIL_HELMET);
                                entries.add(Armors.MITHRIL_BOOTS);
                                entries.add(Armors.MITHRIL_CHEST_PLATE);
                                entries.add(Armors.MITHRIL_LEGGINGS);


                                entries.add(CoinItems.COPPER_COIN);
                                entries.add(CoinItems.IRON_COIN);

                                entries.add(Armors.ANCIENT_METAL_CHAINMAIL_BOOTS);
                                entries.add(Armors.ANCIENT_METAL_CHAINMAIL_LEGGINGS);
                                entries.add(Armors.ANCIENT_METAL_CHAINMAIL_CHEST_PLATE);
                                entries.add(Armors.ANCIENT_METAL_CHAINMAIL_HELMET);

                                entries.add(ModItems.PUDDING_SLIME_BALL);
                                entries.add(FoodItems.PUMPKIN_SOUP);
                                entries.add(FoodItems.WATER_BOWL);
                                entries.add(FoodItems.VEGETABLE_SOUP);
                                entries.add(FoodItems.BEEF_SOUP);
                                entries.add(FoodItems.ONION);
                                entries.add(ModBlocks.EMERALD_ENCHANTING_TABLE);
                                entries.add(ModItems.MANURE);
                            }
                    ).build());






    public static void registerModItemGroup(){

    }
}
