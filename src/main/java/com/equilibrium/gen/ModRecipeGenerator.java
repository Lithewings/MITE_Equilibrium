package com.equilibrium.gen;

import com.equilibrium.block.ModBlocks;
import com.equilibrium.craft_time_register.BlockInit;
import com.equilibrium.item.Armors;
import com.equilibrium.item.Metal;
import com.equilibrium.item.Tools;
import com.equilibrium.item.extend_item.CoinItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.equilibrium.MITEequilibrium.MOD_ID;

public class ModRecipeGenerator extends FabricRecipeProvider {
    private static final List<ItemConvertible> ADAMANTIUM_RAW=List.of(Metal.ADAMANTIUM_RAW);
    private static final List<ItemConvertible> COPPER_INGOT=List.of(Items.COPPER_INGOT);
    private static final List<ItemConvertible> MITHRIL_RAW=List.of(Metal.MITHRIL_RAW);
    private static final List<ItemConvertible> SILVER_RAW=List.of(Metal.SILVER_RAW);

    public ModRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, CoinItems.COPPER_COIN).
                pattern("X").
                input('X', Metal.copper_nugget).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper_nugget)).
                offerTo(exporter, Identifier.of(MOD_ID,"copper_coin"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, CoinItems.IRON_COIN).
                pattern("X").
                input('X', Items.IRON_NUGGET).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.IRON_NUGGET)).
                offerTo(exporter, Identifier.of(MOD_ID,"iron_coin"));


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.GOLDEN_APPLE).
                pattern("XXX").
                pattern("XYX").
                pattern("XXX").
                input('X', Items.GOLD_NUGGET).
                input('Y', Items.APPLE).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.GOLD_NUGGET)).
                offerTo(exporter, Identifier.of("miteequilibrium","golden_apple"));

        //armors
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Armors.COPPER_BOOTS).
                pattern("X X").
                pattern("X X").
                input('X', Metal.copper).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_boots"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Armors.COPPER_HELMET).
                pattern("XXX").
                pattern("X X").
                input('X', Metal.copper).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_helmet"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Armors.COPPER_CHEST_PLATE).
                pattern("X X").
                pattern("XXX").
                pattern("XXX").
                input('X', Metal.copper).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_chest_plate"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Armors.COPPER_LEGGINGS).
                pattern("XXX").
                pattern("X X").
                pattern("X X").
                input('X', Metal.copper).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_leggings"));


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Armors.MITHRIL_BOOTS).
                pattern("X X").
                pattern("X X").
                input('X', Metal.mithril).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_boots"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Armors.MITHRIL_HELMET).
                pattern("XXX").
                pattern("X X").
                input('X', Metal.mithril).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_helmet"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Armors.MITHRIL_CHEST_PLATE).
                pattern("X X").
                pattern("XXX").
                pattern("XXX").
                input('X', Metal.mithril).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_chest_plate"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Armors.MITHRIL_LEGGINGS).
                pattern("XXX").
                pattern("X X").
                pattern("X X").
                input('X', Metal.mithril).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_leggings"));

        //shield

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.SHIELD).
                pattern("XYX").
                pattern("XXX").
                pattern(" X ").
                input('X', ItemTags.PLANKS).
                input('Y', Metal.copper).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","shield"));



        //furnace

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BlockInit.CLAY_FURNACE).
                pattern("XX").
                pattern("XX").
                input('X', Blocks.CLAY).
                criterion("has_item", RecipeProvider.conditionsFromItem(Blocks.CLAY)).
                offerTo(exporter, Identifier.of("miteequilibrium","clay_furnace"));



        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BlockInit.OBSIDIAN_FURNACE).
                pattern("XXX").
                pattern("X X").
                pattern("XXX").
                input('X', Blocks.OBSIDIAN).
                criterion("has_item", RecipeProvider.conditionsFromItem(Blocks.OBSIDIAN)).
                offerTo(exporter, Identifier.of("miteequilibrium","obsidian_furnace"));


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BlockInit.NETHERRACK_FURNACE).
                pattern("XXX").
                pattern("X X").
                pattern("XXX").
                input('X', Blocks.NETHERRACK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Blocks.NETHERRACK)).
                offerTo(exporter, Identifier.of("miteequilibrium","netherrack_furnace"));


        //craftTables

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BlockInit.FLINT_CRAFTING_TABLE).
                pattern("X").
                pattern("Y").
                input('X', Tools.FLINT_KNIFE).
                input('Y', ItemTags.LOGS).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.FLINT)).
                offerTo(exporter, Identifier.of("miteequilibrium","flint_craft_table"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BlockInit.COPPER_CRAFTING_TABLE).
                pattern("XZ").
                pattern("YW").
                input('X', Metal.copper).
                input('Y', Items.STICK).
                input('Z', Items.LEATHER).
                input('W', ItemTags.LOGS).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_crafting_table"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BlockInit.SILVER_CRAFTING_TABLE).
                pattern("XZ").
                pattern("YW").
                input('X', Metal.silver).
                input('Y', Items.STICK).
                input('Z', Items.LEATHER).
                input('W', ItemTags.LOGS).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver_crafting_table"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BlockInit.IRON_CRAFTING_TABLE).
                pattern("XZ").
                pattern("YW").
                input('X', Items.IRON_INGOT).
                input('Y', Items.STICK).
                input('Z', Items.LEATHER).
                input('W', ItemTags.LOGS).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).
                offerTo(exporter, Identifier.of("miteequilibrium","iron_crafting_table"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BlockInit.DIAMOND_CRAFTING_TABLE).
                pattern("XZ").
                pattern("YW").
                input('X', Items.DIAMOND).
                input('Y', Items.STICK).
                input('Z', Items.LEATHER).
                input('W', ItemTags.LOGS).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.DIAMOND)).
                offerTo(exporter, Identifier.of("miteequilibrium","diamond_crafting_table"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BlockInit.NETHERITE_CRAFTING_TABLE).
                pattern("XZ").
                pattern("YW").
                input('X', Items.NETHERITE_INGOT).
                input('Y', Items.STICK).
                input('Z', Items.LEATHER).
                input('W', ItemTags.LOGS).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.NETHERITE_INGOT)).
                offerTo(exporter, Identifier.of("miteequilibrium","netherite_crafting_table"));




        offerSmelting(exporter,ADAMANTIUM_RAW,RecipeCategory.MISC,Metal.adamantium,100,200,"adamantium_raw");
        offerSmelting(exporter,COPPER_INGOT,RecipeCategory.MISC,Metal.copper,20,200,"copper_ingot");
        offerSmelting(exporter,MITHRIL_RAW,RecipeCategory.MISC,Metal.mithril,80,200,"mithril_raw");
        offerSmelting(exporter,SILVER_RAW,RecipeCategory.MISC,Metal.silver,30,200,"silver_raw");

        //piece->flint
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.FLINT).
                pattern("XX").
                pattern("XX").
                input('X', Metal.FLINT).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.FLINT)).
                offerTo(exporter, Identifier.of("miteequilibrium","piece_to_flint"));

        //flint->piece
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Metal.FLINT,4).
                pattern("X").
                input('X', Items.FLINT).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.FLINT)).
                offerTo(exporter, Identifier.of("miteequilibrium","flint_to_piece"));


        //hatchet
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.FLINT_HATCHET).
                pattern("XY").
                pattern(" Y").
                input('X', Items.FLINT).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.FLINT)).
                offerTo(exporter, Identifier.of("miteequilibrium","flint_hatchet"));



        //sword

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.ADMANTIUM_SWORD).
                pattern("X").
                pattern("X").
                pattern("Y").
                input('X', Metal.adamantium).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.adamantium)).
                offerTo(exporter, Identifier.of("miteequilibrium","admantium_sword"));




        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.COPPER_SWORD).
                pattern("X").
                pattern("X").
                pattern("Y").
                input('X', Metal.copper).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_sword"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.SILVER_SWORD).
                pattern("X").
                pattern("X").
                pattern("Y").
                input('X', Metal.silver).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.silver)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver_sword"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.MITHRIL_SWORD).
                pattern("X").
                pattern("X").
                pattern("Y").
                input('X', Metal.mithril).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_sword"));

        //axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.FLINT_AXE).
                pattern("XX").
                pattern("XY").
                pattern(" Y").
                input('X', Items.FLINT).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.FLINT)).
                offerTo(exporter, Identifier.of("miteequilibrium","flint_axe"));


        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.ADAMANTIUM_AXE).
                pattern("XX").
                pattern("XY").
                pattern(" Y").
                input('X', Metal.adamantium).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.adamantium)).
                offerTo(exporter, Identifier.of("miteequilibrium","adamantium_axe"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.COPPER_AXE).
                pattern("XX").
                pattern("XY").
                pattern(" Y").
                input('X', Metal.copper).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_axe"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.SILVER_AXE).
                pattern("XX").
                pattern("XY").
                pattern(" Y").
                input('X', Metal.silver).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.silver)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver_axe"));
//在合成表输出的时候替换掉原来的铁工具就好了
//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.IRON_AXE).
//                pattern("XX").
//                pattern("XY").
//                pattern(" Y").
//                input('X', Items.IRON_INGOT).
//                input('Y',Items.STICK).
//                criterion("has_item", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).
//                offerTo(exporter, Identifier.of("miteequilibrium","iron_axe"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.MITHRIL_AXE).
                pattern("XX").
                pattern("XY").
                pattern(" Y").
                input('X', Metal.mithril).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_axe"));

//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.GOLD_AXE).
//                pattern("XX").
//                pattern("XY").
//                pattern(" Y").
//                input('X', Metal.gold).
//                input('Y',Items.STICK).
//                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.gold)).
//                offerTo(exporter, Identifier.of("miteequilibrium","gold_axe"));





        //shovel
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.FLINT_SHOVEL).
                pattern("X").
                pattern("Y").
                pattern("Y").
                input('X', Items.FLINT).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.FLINT)).
                offerTo(exporter, Identifier.of("miteequilibrium","flint_shovel"));




        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.ADAMANTIUM_SHOVEL).
                pattern("X").
                pattern("Y").
                pattern("Y").
                input('X', Metal.adamantium).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.adamantium)).
                offerTo(exporter, Identifier.of("miteequilibrium","adamantium_shovel"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.COPPER_SHOVEL).
                pattern("X").
                pattern("Y").
                pattern("Y").
                input('X', Metal.copper).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_shovel"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.SILVER_SHOVEL).
                pattern("X").
                pattern("Y").
                pattern("Y").
                input('X', Metal.silver).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.silver)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver_shovel"));

//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.IRON_SHOVEL).
//                pattern("X").
//                pattern("Y").
//                pattern("Y").
//                input('X', Items.IRON_INGOT).
//                input('Y',Items.STICK).
//                criterion("has_item", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).
//                offerTo(exporter, Identifier.of("miteequilibrium","iron_axe"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.MITHRIL_SHOVEL).
                pattern("X").
                pattern("Y").
                pattern("Y").
                input('X', Metal.mithril).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_shovel"));

//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.GOLD_SHOVEL).
//                pattern("X").
//                pattern("Y").
//                pattern("Y").
//                input('X', Metal.gold).
//                input('Y',Items.STICK).
//                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.gold)).
//                offerTo(exporter, Identifier.of("miteequilibrium","gold_shovel"));


        //dagger
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.FLINT_KNIFE).
                pattern("X").
                pattern("Y").
                input('X', Items.FLINT).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.FLINT)).
                offerTo(exporter, Identifier.of("miteequilibrium","flint_knife"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.ADMANTIUM_DAGGER).
                pattern("X").
                pattern("Y").
                input('X', Metal.adamantium).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.adamantium)).
                offerTo(exporter, Identifier.of("miteequilibrium","adamantium_dagger"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.COPPER_DAGGER).
                pattern("X").
                pattern("Y").
                input('X', Metal.copper).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_dagger"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.SILVER_DAGGER).
                pattern("X").
                pattern("Y").
                input('X', Metal.silver).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.silver)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver_dagger"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.IRON_DAGGER).
                pattern("X").
                pattern("Y").
                input('X', Items.IRON_INGOT).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).
                offerTo(exporter, Identifier.of("miteequilibrium","iron_dagger"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.MITHRIL_DAGGER).
                pattern("X").
                pattern("Y").
                input('X', Metal.mithril).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_dagger"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.GOLD_DAGGER).
                pattern("X").
                pattern("Y").
                input('X', Items.GOLD_INGOT).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.gold)).
                offerTo(exporter, Identifier.of("miteequilibrium","gold_dagger"));

        //hoe
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.ADAMANTIUM_HOE).
                pattern("XX").
                pattern(" Y").
                pattern(" Y").
                input('X', Metal.adamantium).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.adamantium)).
                offerTo(exporter, Identifier.of("miteequilibrium","adamantium_hoe"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.COPPER_HOE).
                pattern("XX").
                pattern(" Y").
                pattern(" Y").
                input('X', Metal.copper).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_hoe"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.SILVER_HOE).
                pattern("XX").
                pattern(" Y").
                pattern(" Y").
                input('X', Metal.silver).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.silver)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver_hoe"));

//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.IRON_HOE).
//                pattern("XX").
//                pattern(" Y").
//                pattern(" Y").
//                input('X', Items.IRON_INGOT).
//                input('Y',Items.STICK).
//                criterion("has_item", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).
//                offerTo(exporter, Identifier.of("miteequilibrium","iron_hoe"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.MITHRIL_HOE).
                pattern("XX").
                pattern(" Y").
                pattern(" Y").
                input('X', Metal.mithril).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_hoe"));

//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.GOLD_HOE).
//                pattern("XX").
//                pattern(" Y").
//                pattern(" Y").
//                input('X', Metal.gold).
//                input('Y',Items.STICK).
//                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.gold)).
//                offerTo(exporter, Identifier.of("miteequilibrium","gold_hoe"));


        //pickaxe
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.ADAMANTIUM_PICKAXE).
                pattern("XXX").
                pattern(" Y ").
                pattern(" Y ").
                input('X', Metal.adamantium).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.adamantium)).
                offerTo(exporter, Identifier.of("miteequilibrium","adamantium_pickaxe"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.COPPER_PICKAXE).
                pattern("XXX").
                pattern(" Y ").
                pattern(" Y ").
                input('X', Metal.copper).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_pickaxe"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.SILVER_PICKAXE).
                pattern("XXX").
                pattern(" Y ").
                pattern(" Y ").
                input('X', Metal.silver).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.silver)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver_pickaxe"));
//
//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.IRON_PICKAXE).
//                pattern("XXX").
//                pattern(" Y ").
//                pattern(" Y ").
//                input('X', Items.IRON_INGOT).
//                input('Y',Items.STICK).
//                criterion("has_item", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).
//                offerTo(exporter, Identifier.of("miteequilibrium","iron_pickaxe"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.MITHRIL_PICKAXE).
                pattern("XXX").
                pattern(" Y ").
                pattern(" Y ").
                input('X', Metal.mithril).
                input('Y',Items.STICK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_pickaxe"));

//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Tools.GOLD_PICKAXE).
//                pattern("XXX").
//                pattern(" Y ").
//                pattern(" Y ").
//                input('X', Metal.gold).
//                input('Y',Items.STICK).
//                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.gold)).
//                offerTo(exporter, Identifier.of("miteequilibrium","gold_pickaxe"));



        //nugget、ingot、block


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.ADAMANTIUM_BLOCK).
                pattern("XXX").
                pattern("XXX").
                pattern("XXX").
                input('X', Metal.adamantium).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.adamantium)).
                offerTo(exporter, Identifier.of("miteequilibrium","adamantium_block"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.COPPER_BLOCK).
                pattern("XXX").
                pattern("XXX").
                pattern("XXX").
                input('X', Metal.copper).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_block"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SILVER_BLOCK).
                pattern("XXX").
                pattern("XXX").
                pattern("XXX").
                input('X', Metal.silver).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.silver)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver_block"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.MITHRIL_BLOCK).
                pattern("XXX").
                pattern("XXX").
                pattern("XXX").
                input('X', Metal.mithril).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_block"));



        //nugget->ingot

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC,Metal.adamantium).
                pattern("XXX").
                pattern("XXX").
                pattern("XXX").
                input('X', Metal.adamantium_nugget).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.adamantium_nugget)).
                offerTo(exporter, Identifier.of("miteequilibrium","adamantium"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Metal.copper).
                pattern("XXX").
                pattern("XXX").
                pattern("XXX").
                input('X', Metal.copper_nugget).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Metal.silver).
                pattern("XXX").
                pattern("XXX").
                pattern("XXX").
                input('X', Metal.silver_nugget).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.silver)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Metal.mithril).
                pattern("XXX").
                pattern("XXX").
                pattern("XXX").
                input('X', Metal.mithril_nugget).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril"));

        //block->ingot

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC,Metal.adamantium,9).
                pattern("X").
                input('X', ModBlocks.ADAMANTIUM_BLOCK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.adamantium)).
                offerTo(exporter, Identifier.of("miteequilibrium","adamantium_from_block"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC,Metal.mithril,9).
                pattern("X").
                input('X', ModBlocks.MITHRIL_BLOCK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_from_block"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC,Metal.copper,9).
                pattern("X").
                input('X', ModBlocks.COPPER_BLOCK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_from_block"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC,Metal.silver,9).
                pattern("X").
                input('X', ModBlocks.SILVER_BLOCK).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.silver)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver_from_block"));






        //ingot->nugget

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC,Metal.adamantium_nugget,9).
                pattern("X").
                input('X', Metal.adamantium).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.adamantium)).
                offerTo(exporter, Identifier.of("miteequilibrium","adamantium_nugget"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC,Metal.mithril_nugget,9).
                pattern("X").
                input('X', Metal.mithril).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.mithril)).
                offerTo(exporter, Identifier.of("miteequilibrium","mithril_nugget"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC,Metal.copper_nugget,9).
                pattern("X").
                input('X', Metal.copper).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.copper)).
                offerTo(exporter, Identifier.of("miteequilibrium","copper_nugget"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC,Metal.silver,9).
                pattern("X").
                input('X', Metal.silver).
                criterion("has_item", RecipeProvider.conditionsFromItem(Metal.silver)).
                offerTo(exporter, Identifier.of("miteequilibrium","silver_nugget"));






    }






}
