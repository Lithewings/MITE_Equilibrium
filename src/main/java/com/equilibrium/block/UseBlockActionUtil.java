package com.equilibrium.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class UseBlockActionUtil {


    public static boolean isTableBlocked(World world , BlockPos blockPos , @Nullable PlayerEntity player){
        boolean isTableBlocked = !world.getBlockState(blockPos.up()).isAir();
        if(player!=null && isTableBlocked){
            player.sendMessage(Text.of("该方块已被阻挡"),true);
        }
        return isTableBlocked;
    }

    public static ActionResult canUseVanillaCraftingTable(PlayerEntity player, World world, Hand hand , BlockHitResult hitResult){
        Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();
        if (!player.getWorld().isClient) {
            if (!player.isCreative()) {
                if (block == Blocks.CRAFTING_TABLE) {
                    world.removeBlock(hitResult.getBlockPos(), true);
                }
            }
        }
        return ActionResult.PASS;
    }
}

