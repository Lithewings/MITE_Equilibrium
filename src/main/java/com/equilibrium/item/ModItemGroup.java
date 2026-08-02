package com.equilibrium.item;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.block.ModBlocksRegistry2;
import com.equilibrium.item.extend_item.CoinItems;
import com.equilibrium.item.food.FoodOrFarmItems;
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

    // 创意标签实例（供其他类引用）
    public static CreativeModeTab BLOCKS;
    public static CreativeModeTab TOOLS;
    public static CreativeModeTab FARM;

    @SubscribeEvent
    public static void registerCreativeTabs(RegisterEvent event) {
        event.register(BuiltInRegistries.CREATIVE_MODE_TAB.key(), helper -> {
            // 方块 / 金属 / 杂项
            BLOCKS = CreativeModeTab.builder()
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

                        output.accept(MaterialItems.ADAMANTIUM_INGOT);
                        output.accept(MaterialItems.COPPER_INGOT);
                        output.accept(MaterialItems.ANCIENT_METAL_INGOT);
                        output.accept(MaterialItems.GOLD_INGOT);
                        output.accept(MaterialItems.MITHRIL_INGOT);
                        output.accept(MaterialItems.SILVER_INGOT);
                        output.accept(MaterialItems.FLINT);

                        output.accept(MaterialItems.ADAMANTIUM_NUGGET);
                        output.accept(MaterialItems.ANCIENT_METAL_NUGGET);
                        output.accept(MaterialItems.COPPER_NUGGET);
                        output.accept(MaterialItems.GOLD_NUGGET);
                        output.accept(MaterialItems.SILVER_NUGGET);
                        output.accept(MaterialItems.MITHRIL_NUGGET);

                        output.accept(MaterialItems.RAW_ADAMANTIUM);
                        output.accept(MaterialItems.RAW_MITHRIL);
                        output.accept(MaterialItems.RAW_SILVER);

                        output.accept(CoinItems.COPPER_COIN);
                        output.accept(CoinItems.IRON_COIN);

                        output.accept(OtherItems.DIAMOND_SHARD);
                        output.accept(OtherItems.EMERALD_SHARD);
                        output.accept(OtherItems.GLASS_FRAGMENT);
                    })
                    .build();
            helper.register(ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "blockgroup"), BLOCKS);

            // 工具栏
            TOOLS = CreativeModeTab.builder()
                    .title(Component.translatable("mod.itemGroup.armors_and_tools"))
                    .icon(() -> new ItemStack(Tools.ADAMANTIUM_PICKAXE.get()))
                    .displayItems((params, output) -> {
                        output.accept(Tools.FLINT_AXE.get());
                        output.accept(Tools.FLINT_HATCHET.get());
                        output.accept(Tools.FLINT_KNIFE.get());
                        output.accept(Tools.FLINT_SHOVEL.get());

                        output.accept(Tools.COPPER_AXE.get());
                        output.accept(Tools.COPPER_DAGGER.get());
                        output.accept(Tools.COPPER_HAMMER.get());
                        output.accept(Tools.COPPER_HOE.get());
                        output.accept(Tools.COPPER_PICKAXE.get());      
                        output.accept(Tools.COPPER_SHOVEL.get());       
                        output.accept(Tools.COPPER_SWORD.get());        

                        output.accept(Tools.GOLD_AXE.get());            
                        output.accept(Tools.GOLD_DAGGER.get());         
                        output.accept(Tools.GOLD_HAMMER.get());         
                        output.accept(Tools.GOLD_HOE.get());            
                        output.accept(Tools.GOLD_PICKAXE.get());        
                        output.accept(Tools.GOLD_SHOVEL.get());         
                        output.accept(Tools.GOLD_SWORD.get());          

                        output.accept(Tools.SILVER_AXE.get());          
                        output.accept(Tools.SILVER_DAGGER.get());       
                        output.accept(Tools.SILVER_HAMMER.get());       
                        output.accept(Tools.SILVER_HOE.get());          
                        output.accept(Tools.SILVER_PICKAXE.get());      
                        output.accept(Tools.SILVER_SHOVEL.get());       
                        output.accept(Tools.SILVER_SWORD.get());        

                        output.accept(Tools.IRON_AXE.get());            
                        output.accept(Tools.IRON_DAGGER.get());         
                        output.accept(Tools.IRON_HAMMER.get());         
                        output.accept(Tools.IRON_HOE.get());            
                        output.accept(Tools.IRON_PICKAXE.get());        
                        output.accept(Tools.IRON_SHOVEL.get());         
                        output.accept(Tools.IRON_SWORD.get());          

                        output.accept(Tools.MITHRIL_AXE.get());         
                        output.accept(Tools.MITHRIL_DAGGER.get());      
                        output.accept(Tools.MITHRIL_HAMMER.get());      
                        output.accept(Tools.MITHRIL_HOE.get());         
                        output.accept(Tools.MITHRIL_SHOVEL.get());      
                        output.accept(Tools.MITHRIL_SWORD.get());       
                        output.accept(Tools.MITHRIL_PICKAXE.get());     

                        output.accept(Tools.ADAMANTIUM_AXE.get());      
                        output.accept(Tools.ADMANTIUM_DAGGER.get());
                        output.accept(Tools.ADAMANTIUM_HOE.get());      
                        output.accept(Tools.ADAMANTIUM_HAMMER.get());   
                        output.accept(Tools.ADAMANTIUM_SHOVEL.get());   
                        output.accept(Tools.ADMANTIUM_SWORD.get());     
                        output.accept(Tools.ADAMANTIUM_PICKAXE.get());  

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
                    .build();
            helper.register(ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "toolsgroup"), TOOLS);

            // 农业
            FARM = CreativeModeTab.builder()
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
                    .build();
            helper.register(ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID, "farmgroup"), FARM);
        });
    }
}