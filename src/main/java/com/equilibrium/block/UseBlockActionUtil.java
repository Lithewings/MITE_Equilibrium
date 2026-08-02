package com.equilibrium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class UseBlockActionUtil {


    public static boolean isTableBlocked(Level world , BlockPos blockPos , @Nullable Player player){
        boolean isTableBlocked = !world.getBlockState(blockPos.above()).isAir();
        if(player!=null && isTableBlocked){
            player.displayClientMessage(Component.nullToEmpty("该方块已被阻挡"),true);
        }
        return isTableBlocked;
    }

    public static InteractionResult canUseVanillaCraftingTable(Player player, Level world, InteractionHand hand , BlockHitResult hitResult){
        Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();
        if (!player.level().isClientSide) {
            if (!player.isCreative()) {
                if (block == Blocks.CRAFTING_TABLE) {
                    world.removeBlock(hitResult.getBlockPos(), true);
                }
            }
        }
        return InteractionResult.PASS;
    }
}

