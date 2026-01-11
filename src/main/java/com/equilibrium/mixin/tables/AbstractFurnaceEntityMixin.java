package com.equilibrium.mixin.tables;



import com.equilibrium.block.ModBlocksRegistry2;
import com.equilibrium.tags.ModItemTags;
import com.google.common.collect.Maps;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Map;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceEntityMixin extends BlockEntity {


    @Shadow @Final private RecipeManager.MatchGetter<SingleStackRecipeInput, ? extends AbstractCookingRecipe> matchGetter;

    @Shadow
    int burnTime;




    @Shadow
    protected DefaultedList<ItemStack> inventory;

    private AbstractFurnaceEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Inject(at = @At("RETURN"), method = "<init>")
    public void Constructor(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType, CallbackInfo info) {

    }

    @Shadow
    int fuelTime;
    @Shadow
    int cookTime;
    @Shadow
    int cookTimeTotal;














    @Shadow
    private static volatile Map<Item, Integer> fuelTimes;




    private static int getBlockItemStackLevel(ItemStack blockItemStack){
        //接下来是获取燃料和物品的燃烧等级
        int itemNeedFuelLevel = 0;
        if(blockItemStack.isIn(ModItemTags.BLOCK_NEED_FUEL_LEVEL1)||blockItemStack.isIn(ModItemTags.ITEM_NEED_FUEL_LEVEL1))
            itemNeedFuelLevel = 1;
        else if (blockItemStack.isIn(ModItemTags.BLOCK_NEED_FUEL_LEVEL2)||blockItemStack.isIn(ModItemTags.ITEM_NEED_FUEL_LEVEL2))
            itemNeedFuelLevel = 2;
        else if (blockItemStack.isIn(ModItemTags.BLOCK_NEED_FUEL_LEVEL3)||blockItemStack.isIn(ModItemTags.ITEM_NEED_FUEL_LEVEL3))
            itemNeedFuelLevel = 3;
        else if (blockItemStack.isIn(ModItemTags.BLOCK_NEED_FUEL_LEVEL4)||blockItemStack.isIn(ModItemTags.ITEM_NEED_FUEL_LEVEL4))
            itemNeedFuelLevel = 4;
        else
            itemNeedFuelLevel = 0;

        return itemNeedFuelLevel;

    }
    private static int getFuelItemStackLevel(ItemStack fuelItemStack) {
        int fuelLevel = 0;
        if (fuelItemStack.isIn(ModItemTags.FUEL_LEVEL1))
            fuelLevel = 1;
        else if (fuelItemStack.isIn(ModItemTags.FUEL_LEVEL2))
            fuelLevel = 2;
        else if (fuelItemStack.isIn(ModItemTags.FUEL_LEVEL3))
            fuelLevel = 3;
        else if (fuelItemStack.isIn(ModItemTags.FUEL_LEVEL4))
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



    @Inject(at = @At("HEAD"), method = "getFuelTime", cancellable = true)
    public void getFuelTime(ItemStack fuel, CallbackInfoReturnable<Integer> ca) {
        ca.cancel();
        if (fuel.isEmpty()) {
            ca.setReturnValue(0);
        } else {
            Item item = fuel.getItem();

            //此处world必须判断是否为null，否则熔炉数据无法保存。
            if (this.getWorld() != null) {
                Block block = this.world.getBlockState(this.pos).getBlock();

                //这是熔炉空间的物品
                ItemStack blockItemStack = this.inventory.getFirst();
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
                    ca.setReturnValue(createFuelTimeMap().getOrDefault(item, 0));}
            }






        }
        //为0时燃烧条始终不动,也就达成了不燃烧的效果


    }

    @Inject(at = @At("TAIL"),method = "tick")
    private static void tick(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        //燃烧过程的合法性判断
        if (blockEntity.isBurning()){
            //在熔炉燃烧时,获取物品和物品燃烧所需热值等级
            ItemStack item=blockEntity.getStack(0);
            //获取物品燃烧等级
            int itemLevel = getBlockItemStackLevel(item);

            //在熔炉燃烧时,获取熔炉等级
            Block furnace = state.getBlock();
            int furnaceLevel = getFurnaceLevel(furnace);
            //物品燃烧所需热值大于熔炉等级,燃烧进度归0
            if(itemLevel>furnaceLevel){
                blockEntity.cookTime = 0;
//                MITEequilibrium.LOGGER.info("can not continue melting");
        }}
    }




    @Inject(at = @At("HEAD"), method = "getCookTime", cancellable = true)
    private static void getCookTime(World world, AbstractFurnaceBlockEntity furnace, CallbackInfoReturnable<Integer> ca)  {
        Item item;
        if (world != null) {
            Block block = world.getBlockState(furnace.getPos()).getBlock();
            AbstractFurnaceEntityMixin mixin = (AbstractFurnaceEntityMixin) (Object) furnace;
            //物品
            ItemStack itemStack0 = (ItemStack) mixin.inventory.get(0);
            Item item0 = itemStack0.getItem();
            String name0 = Registries.ITEM.getId(item0).toString();

            //燃料
            ItemStack itemStack1 = mixin.inventory.get(1);
            item = itemStack1.getItem();
            String name = Registries.ITEM.getId(item).toString();

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




    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Map<Item, Integer> createFuelTimeMap() {
        Map<Item, Integer> map = fuelTimes;
        if (map != null) {
            return map;
        } else {
            Map<Item, Integer> map2 = Maps.<Item, Integer>newLinkedHashMap();
            addFuel(map2, Items.LAVA_BUCKET, 20000);
            addFuel(map2, Blocks.COAL_BLOCK, 16000);
            addFuel(map2, Items.BLAZE_ROD, 2400);
            addFuel(map2, Items.COAL, 1600);
            addFuel(map2, Items.CHARCOAL, 1600);
            addFuel(map2, ItemTags.LOGS, 300);
            addFuel(map2, ItemTags.BAMBOO_BLOCKS, 300);
            addFuel(map2, ItemTags.PLANKS, 300);
            addFuel(map2, Blocks.BAMBOO_MOSAIC, 300);
            addFuel(map2, ItemTags.WOODEN_STAIRS, 300);
            addFuel(map2, Blocks.BAMBOO_MOSAIC_STAIRS, 300);
            addFuel(map2, ItemTags.WOODEN_SLABS, 150);
            addFuel(map2, Blocks.BAMBOO_MOSAIC_SLAB, 150);
            addFuel(map2, ItemTags.WOODEN_TRAPDOORS, 300);
            addFuel(map2, ItemTags.WOODEN_PRESSURE_PLATES, 300);
            addFuel(map2, ItemTags.WOODEN_FENCES, 300);
            addFuel(map2, ItemTags.FENCE_GATES, 300);
            addFuel(map2, Blocks.NOTE_BLOCK, 300);
            addFuel(map2, Blocks.BOOKSHELF, 300);
            addFuel(map2, Blocks.CHISELED_BOOKSHELF, 300);
            addFuel(map2, Blocks.LECTERN, 300);
            addFuel(map2, Blocks.JUKEBOX, 300);
            addFuel(map2, Blocks.CHEST, 300);
            addFuel(map2, Blocks.TRAPPED_CHEST, 300);
            addFuel(map2, Blocks.CRAFTING_TABLE, 300);
            addFuel(map2, Blocks.DAYLIGHT_DETECTOR, 300);
            addFuel(map2, ItemTags.BANNERS, 300);
            addFuel(map2, Items.BOW, 300);
            addFuel(map2, Items.FISHING_ROD, 300);
            addFuel(map2, Blocks.LADDER, 300);
            addFuel(map2, ItemTags.SIGNS, 200);
            addFuel(map2, ItemTags.HANGING_SIGNS, 800);
            addFuel(map2, Items.WOODEN_SHOVEL, 200);
            addFuel(map2, Items.WOODEN_SWORD, 200);
            addFuel(map2, Items.WOODEN_HOE, 200);
            addFuel(map2, Items.WOODEN_AXE, 200);
            addFuel(map2, Items.WOODEN_PICKAXE, 200);
            addFuel(map2, ItemTags.WOODEN_DOORS, 200);
            addFuel(map2, ItemTags.BOATS, 1200);
            addFuel(map2, ItemTags.WOOL, 100);
            addFuel(map2, ItemTags.WOODEN_BUTTONS, 100);
            addFuel(map2, Items.STICK, 100);
            addFuel(map2, ItemTags.SAPLINGS, 100);
            addFuel(map2, Items.BOWL, 100);
            addFuel(map2, ItemTags.WOOL_CARPETS, 67);
            addFuel(map2, Blocks.DRIED_KELP_BLOCK, 4001);
            addFuel(map2, Items.CROSSBOW, 300);
            addFuel(map2, Blocks.BAMBOO, 50);
            addFuel(map2, Blocks.DEAD_BUSH, 100);
            addFuel(map2, Blocks.SCAFFOLDING, 50);
            addFuel(map2, Blocks.LOOM, 300);
            addFuel(map2, Blocks.BARREL, 300);
            addFuel(map2, Blocks.CARTOGRAPHY_TABLE, 300);
            addFuel(map2, Blocks.FLETCHING_TABLE, 300);
            addFuel(map2, Blocks.SMITHING_TABLE, 300);
            addFuel(map2, Blocks.COMPOSTER, 300);
            addFuel(map2, Blocks.AZALEA, 100);
            addFuel(map2, Blocks.FLOWERING_AZALEA, 100);
            addFuel(map2, Blocks.MANGROVE_ROOTS, 300);
            fuelTimes = map2;
            return map2;
        }
    }

    @Shadow
    private static boolean isNonFlammableWood(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

    @Shadow
    private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {
        Iterator var3 = Registries.ITEM.iterateEntries(tag).iterator();

        while (var3.hasNext()) {
            RegistryEntry<Item> registryEntry = (RegistryEntry) var3.next();
            if (!isNonFlammableWood((Item) registryEntry.value())) {
                fuelTimes.put((Item) registryEntry.value(), fuelTime);
            }
        }

    }

    @Shadow
    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        if (isNonFlammableWood(item2)) {
            if (SharedConstants.isDevelopment) {
                throw (IllegalStateException) Util.throwOrPause(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item2.getName((ItemStack) null).getString() + " a furnace fuel. That will not work!"));
            }
        } else {
            fuelTimes.put(item2, fuelTime);
        }
    }
}

