package com.equilibrium.server_and_client.server.event;

import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.block.ModBlocksRegistry2;
import com.equilibrium.item.Metal;
import com.equilibrium.tags.ModBlockTags;
import com.equilibrium.util.BlockToItemConverter;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static com.equilibrium.block.reference.BlocksHardnessList.BLOCKS_HARDNESS_HASHMAP;
import static com.equilibrium.block.reference.BlocksHardnessList.getStandardBlockName;


public class BreakBlockEvent implements PlayerBlockBreakEvents.After {
    public static BlockToItemConverter blockToItemConverter = new BlockToItemConverter();
    public static int guarantee = 0;

    /**
     * Called after a block is successfully broken.
     *
     * @param world       the world where the block was broken
     * @param player      the player who broke the block
     * @param pos         the position where the block was broken
     * @param state       the block state <strong>before</strong> the block was broken
     * @param blockEntity the block entity of the broken block, can be {@code null}
     */

    //最多12次沙砾必然不掉落自身,全服务器共享进度,重启时归零

    @Override
    public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if(player.isCreative())
            return;

        ItemStack itemStack = player.getMainHandStack();
        itemStack.damage(BLOCKS_HARDNESS_HASHMAP.getOrDefault(getStandardBlockName(state.getBlock()),0), player, EquipmentSlot.MAINHAND);
        //提前结束
        if (
            !
            (
            (state.isIn(BlockTags.LEAVES))||
            (state.getBlock() == Blocks.GRAVEL)||
            (state.isIn(ModBlockTags.ORE))
            )

        )
            return;
        Random random = new Random();
        //时运附魔等级
        int furtuneLevel = EnchantmentHelper.getLevel(world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.FORTUNE).get(), itemStack);
        //精准采集等级
        int slikTouch = EnchantmentHelper.getLevel(world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.SILK_TOUCH).get(), itemStack);

        if (state.isIn(BlockTags.LEAVES)) {
            ItemEntity itemDrop;


            int randomNumber = random.nextInt(100 - furtuneLevel * 30);
            if (randomNumber <= 10) {
                itemDrop = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(Items.STICK));
                world.spawnEntity(itemDrop);
            }

        }
        if (state.getBlock() == Blocks.GRAVEL) {


            if (slikTouch == 1) {
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(Items.GRAVEL)));
                return;
            }

            int randomNumber1 = random.nextInt(100);
            if (randomNumber1 < 75 - furtuneLevel * 15 && guarantee < 12) {
                guarantee++;
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(Blocks.GRAVEL)));
                return;
            } else {
                guarantee = 0;
            }


            int randomNumber2 = random.nextInt(1000);


            ItemEntity itemDrop;
            if (randomNumber2 == 0) {
                //0,就1个,0.1%
                itemDrop = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(Items.REDSTONE));
                world.spawnEntity(itemDrop);

            } else if (randomNumber2 <= 100) {
                //1-100,共100个 10%
                itemDrop = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(Metal.silver_nugget));
                world.spawnEntity(itemDrop);

            } else if (randomNumber2 <= 240) {
                //101-240,共140个 14%
                itemDrop = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(Items.FLINT));
                world.spawnEntity(itemDrop);

            } else if (randomNumber2 <= 400) {
                //241-400,共160个 16%
                itemDrop = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(Metal.copper_nugget));
                world.spawnEntity(itemDrop);

            } else {
                //401-999,共599个 59.9%
                itemDrop = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(Metal.FLINT));
                world.spawnEntity(itemDrop);
            }
        }
        if (state.isIn(ModBlockTags.ORE) ) {
            if(slikTouch==1){
                Item item = state.getBlock().asItem();
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new ItemStack(item)));
            }else{
                //掉落个数,比如红石就应该多次掉落
                int dropTime = 1;
                //获取矿石掉落物
                Item item = blockToItemConverter.convertBlockToItem(state.getBlock());
                if (item == Items.LAPIS_LAZULI || item == Items.REDSTONE || item == Items.GOLD_NUGGET)
                    //4~7次掉落
                    dropTime = 4 + random.nextInt(4);


                if (random.nextInt(10) >= (10 - furtuneLevel)) {
                    //若时运为3,则表示随机的数字 0 1 2 3 4 5 6 7 8 9 中大于等于7的概率,即0.3
                    //时运触发时,相当于本次产出翻倍
                    dropTime *= 2;
                }
                //掉落1次还是4次
                for (int i = 0; i < dropTime; i++) {
                    world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                            new ItemStack(item)));


                }
            }
        }
    }
}























