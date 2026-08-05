package com.equilibrium.mixin.vanilla_blocksmixin.tables;



import com.equilibrium.block.ModBlocksRegistry2;
import com.equilibrium.tags.ModItemTags;
import com.google.common.collect.Maps;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Map;

import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.getFuel;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceEntityMixin extends BlockEntity {






    @Shadow
    protected NonNullList<ItemStack> items;

    private AbstractFurnaceEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Inject(at = @At("RETURN"), method = "<init>")
    public void Constructor(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType, CallbackInfo info) {

    }

    @Shadow
    private static volatile Map<Item, Integer> fuelCache;




    private static int getBlockItemStackLevel(ItemStack blockItemStack){
        //接下来是获取燃料和物品的燃烧等级
        int itemNeedFuelLevel = 0;
        if(blockItemStack.is(ModItemTags.BLOCK_NEED_FUEL_LEVEL1)||blockItemStack.is(ModItemTags.ITEM_NEED_FUEL_LEVEL1))
            itemNeedFuelLevel = 1;
        else if (blockItemStack.is(ModItemTags.BLOCK_NEED_FUEL_LEVEL2)||blockItemStack.is(ModItemTags.ITEM_NEED_FUEL_LEVEL2))
            itemNeedFuelLevel = 2;
        else if (blockItemStack.is(ModItemTags.BLOCK_NEED_FUEL_LEVEL3)||blockItemStack.is(ModItemTags.ITEM_NEED_FUEL_LEVEL3))
            itemNeedFuelLevel = 3;
        else if (blockItemStack.is(ModItemTags.BLOCK_NEED_FUEL_LEVEL4)||blockItemStack.is(ModItemTags.ITEM_NEED_FUEL_LEVEL4))
            itemNeedFuelLevel = 4;
        else
            itemNeedFuelLevel = 0;

        return itemNeedFuelLevel;

    }
    private static int getFuelItemStackLevel(ItemStack fuelItemStack) {
        int fuelLevel = 0;
        if (fuelItemStack.is(ModItemTags.FUEL_LEVEL1))
            fuelLevel = 1;
        else if (fuelItemStack.is(ModItemTags.FUEL_LEVEL2))
            fuelLevel = 2;
        else if (fuelItemStack.is(ModItemTags.FUEL_LEVEL3))
            fuelLevel = 3;
        else if (fuelItemStack.is(ModItemTags.FUEL_LEVEL4))
            fuelLevel = 4;
        else
            fuelLevel = 0;
        //没有提到的物品热值都是0
        return fuelLevel;

    }
    private static int getFurnaceLevel(Block furnace) {
        int furnaceLevel = 0;
        //燃料等级大于该熔炉等级不可燃烧:原版熔炉和高炉为一级熔炉
        if (furnace == ModBlocksRegistry2.CLAY_FURNACE) {
            furnaceLevel = 0;
        } else if (furnace == Blocks.FURNACE || furnace == Blocks.BLAST_FURNACE) {
            //原版熔炉的最大承受热值为1
            furnaceLevel = 1;
        } else if (furnace == ModBlocksRegistry2.OBSIDIAN_FURNACE) {
            //黑曜石熔炉的最大承受热值为2
            furnaceLevel = 2;
        } else if (furnace == ModBlocksRegistry2.NETHERRACK_FURNACE) {
            furnaceLevel = 3;
        }
        return furnaceLevel;
    }
    private static boolean checkValidityForIgniting(Block furnace, ItemStack blockItemStack, ItemStack fuelItemStack){
        //熔炉等级
        int furnaceLevel = getFurnaceLevel(furnace);
        //物品燃烧所需热值
        int itemNeedFuelLevel = getBlockItemStackLevel(blockItemStack);
        //燃料热值
        int fuelLevel = getFuelItemStackLevel(fuelItemStack);

        //过滤器,热值不够无法燃烧
        if(itemNeedFuelLevel > fuelLevel)
            return false;

        //燃料热值或物品所需热值超过熔炉热值,也无法燃烧
        if(fuelLevel>furnaceLevel||itemNeedFuelLevel>furnaceLevel)
            return false;
        //只有与熔炉等级相等的燃料才能燃烧该熔炉获得燃烧速度增益
        if(fuelLevel==furnaceLevel)
            return true;
        else
            return false;
    };



    @Inject(at = @At("HEAD"), method = "getBurnDuration", cancellable = true)
    public void getFuelTime(ItemStack fuel, CallbackInfoReturnable<Integer> ca) {
        ca.cancel();
        if (fuel.isEmpty()) {
            ca.setReturnValue(0);
        } else {
            Item item = fuel.getItem();

            //此处world必须判断是否为null，否则熔炉数据无法保存。
            if (this.getLevel() != null) {
                Block block = this.level.getBlockState(this.worldPosition).getBlock();

                //这是熔炉空间的物品
                ItemStack blockItemStack = this.items.getFirst();
                //燃料物品
                ItemStack fuelItemStack = fuel;

                if(!checkValidityForIgniting(block,blockItemStack,fuelItemStack)){
                    ca.setReturnValue(0);
                    //造成这一原因的因素有:
                    //热值不够无法燃烧
                    //燃料热值或物品所需热值超过熔炉热值,也无法燃烧
                    //只有与熔炉等级相等的燃料才能燃烧该熔炉
                    //MITEequilibrium.LOGGER.info("illegal condition for melting");
                }


                else {
                    ca.setReturnValue(getFuel().getOrDefault(item, 0));}
            }






        }
        //为0时燃烧条始终不动,也就达成了不燃烧的效果


    }

    @Inject(at = @At("TAIL"),method = "serverTick")
    private static void tick(Level world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        //燃烧过程的合法性判断
        if (blockEntity.isLit()){
            //在熔炉燃烧时,获取物品和物品燃烧所需热值等级
            ItemStack item=blockEntity.getItem(0);
            //获取物品燃烧等级
            int itemLevel = getBlockItemStackLevel(item);

            //在熔炉燃烧时,获取熔炉等级
            Block furnace = state.getBlock();
            int furnaceLevel = getFurnaceLevel(furnace);
            //物品燃烧所需热值大于熔炉等级,燃烧进度归0
            if(itemLevel>furnaceLevel){
                blockEntity.cookingProgress = 0;
//                MITEequilibrium.LOGGER.info("can not continue melting");
        }}
    }




    @Inject(at = @At("HEAD"), method = "getTotalCookTime", cancellable = true)
    private static void getCookTime(Level world, AbstractFurnaceBlockEntity furnace, CallbackInfoReturnable<Integer> ca)  {
        Item item;
        if (world != null) {
            Block block = world.getBlockState(furnace.getBlockPos()).getBlock();
            AbstractFurnaceEntityMixin mixin = (AbstractFurnaceEntityMixin) (Object) furnace;
            //物品
            ItemStack itemStack0 = (ItemStack) mixin.items.get(0);
            Item item0 = itemStack0.getItem();
            String name0 = BuiltInRegistries.ITEM.getKey(item0).toString();

            //燃料
            ItemStack itemStack1 = mixin.items.get(1);
            item = itemStack1.getItem();
            String name = BuiltInRegistries.ITEM.getKey(item).toString();

            //燃烧速度
            int time =160;

//            //持续检查合法性,尤其是在燃料已经损耗的情况下,避免用煤炭点燃石头熔炉,然后迅速撤掉燃烧物换成艾德曼合金粗矿
//            if(){
//                time= (int) Double.POSITIVE_INFINITY;
//                MITEequilibrium.LOGGER.info("illegal condition for  continue melting");
//            }
//
//            if(CommonConfig.itemCooktimeMap.containsKey(name0)){
//                time = CommonConfig.itemCooktimeMap.get(name0);
//            }

            if (block == Blocks.FURNACE || block == ModBlocksRegistry2.CLAY_FURNACE) {
                ca.setReturnValue(time);
            }
            if (block == ModBlocksRegistry2.OBSIDIAN_FURNACE) {
                ca.setReturnValue(time/5);
            }
            if (block == ModBlocksRegistry2.NETHERRACK_FURNACE) {
                ca.setReturnValue(time/10);
            }

        }
    }
    @Shadow
    private static void add(java.util.function.ObjIntConsumer<com.mojang.datafixers.util.Either<Item, TagKey<Item>>> consumer, ItemLike item, int time) {
        consumer.accept(com.mojang.datafixers.util.Either.left(item.asItem()), time);
    }
    @Shadow
    private static void add(java.util.function.ObjIntConsumer<com.mojang.datafixers.util.Either<Item, TagKey<Item>>> consumer, TagKey<Item> tag, int time) {
        consumer.accept(com.mojang.datafixers.util.Either.right(tag), time);
    }


    /**
     * @author
     * @reason
     */
    @Overwrite
    @org.jetbrains.annotations.ApiStatus.Internal
    public static void buildFuels(java.util.function.ObjIntConsumer<com.mojang.datafixers.util.Either<Item, TagKey<Item>>> map2) {
            add(map2, Items.LAVA_BUCKET, 20000);
            add(map2, Blocks.COAL_BLOCK, 16000);
            add(map2, Items.BLAZE_ROD, 2400);
            add(map2, Items.COAL, 1600);
            add(map2, Items.CHARCOAL, 1600);
            add(map2, ItemTags.LOGS, 300);
            add(map2, ItemTags.BAMBOO_BLOCKS, 300);
            add(map2, ItemTags.PLANKS, 300);
            add(map2, Blocks.BAMBOO_MOSAIC, 300);
            add(map2, ItemTags.WOODEN_STAIRS, 300);
            add(map2, Blocks.BAMBOO_MOSAIC_STAIRS, 300);
            add(map2, ItemTags.WOODEN_SLABS, 150);
            add(map2, Blocks.BAMBOO_MOSAIC_SLAB, 150);
            add(map2, ItemTags.WOODEN_TRAPDOORS, 300);
            add(map2, ItemTags.WOODEN_PRESSURE_PLATES, 300);
            add(map2, ItemTags.WOODEN_FENCES, 300);
            add(map2, ItemTags.FENCE_GATES, 300);
            add(map2, Blocks.NOTE_BLOCK, 300);
            add(map2, Blocks.BOOKSHELF, 300);
            add(map2, Blocks.CHISELED_BOOKSHELF, 300);
            add(map2, Blocks.LECTERN, 300);
            add(map2, Blocks.JUKEBOX, 300);
            add(map2, Blocks.CHEST, 300);
            add(map2, Blocks.TRAPPED_CHEST, 300);
            add(map2, Blocks.CRAFTING_TABLE, 300);
            add(map2, Blocks.DAYLIGHT_DETECTOR, 300);
            add(map2, ItemTags.BANNERS, 300);
            add(map2, Items.BOW, 300);
            add(map2, Items.FISHING_ROD, 300);
            add(map2, Blocks.LADDER, 300);
            add(map2, ItemTags.SIGNS, 200);
            add(map2, ItemTags.HANGING_SIGNS, 800);
            add(map2, Items.WOODEN_SHOVEL, 200);
            add(map2, Items.WOODEN_SWORD, 200);
            add(map2, Items.WOODEN_HOE, 200);
            add(map2, Items.WOODEN_AXE, 200);
            add(map2, Items.WOODEN_PICKAXE, 200);
            add(map2, ItemTags.WOODEN_DOORS, 200);
            add(map2, ItemTags.BOATS, 1200);
            add(map2, ItemTags.WOOL, 100);
            add(map2, ItemTags.WOODEN_BUTTONS, 100);
            add(map2, Items.STICK, 100);
            add(map2, ItemTags.SAPLINGS, 100);
            add(map2, Items.BOWL, 100);
            add(map2, ItemTags.WOOL_CARPETS, 67);
            add(map2, Blocks.DRIED_KELP_BLOCK, 4001);
            add(map2, Items.CROSSBOW, 300);
            add(map2, Blocks.BAMBOO, 50);
            add(map2, Blocks.DEAD_BUSH, 100);
            add(map2, Blocks.SCAFFOLDING, 50);
            add(map2, Blocks.LOOM, 300);
            add(map2, Blocks.BARREL, 300);
            add(map2, Blocks.CARTOGRAPHY_TABLE, 300);
            add(map2, Blocks.FLETCHING_TABLE, 300);
            add(map2, Blocks.SMITHING_TABLE, 300);
            add(map2, Blocks.COMPOSTER, 300);
            add(map2, Blocks.AZALEA, 100);
            add(map2, Blocks.FLOWERING_AZALEA, 100);
            add(map2, Blocks.MANGROVE_ROOTS, 300);
    }
}

