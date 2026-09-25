package com.equilibrium.block.portalblock;


import com.equilibrium.block.miscellaneous.MiscellaneousBlocks;
import com.equilibrium.item.material.MaterialItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import static com.equilibrium.block.portalblock.PortalBlockFinder.replacePortalBlocks;

public class PortalBlockCast {

    private static final int NUGGET_COST = 16;
    private static final int MAX_PORTAL_SEARCH_DEPTH = 64;

    public static InteractionResult tryCastOnUse(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if(state.getBlock() instanceof NetherPortalBlock){
            Direction.Axis axis = state.getValue(NetherPortalBlock.AXIS);
            ItemStack handStack = player.getMainHandItem();
            if(handStack.is(MaterialItems.MITHRIL_NUGGET) && handStack.getCount()>=NUGGET_COST){
                handStack.consume(NUGGET_COST,player);
                replacePortalBlocks(world,pos, (NetherPortalBlock)Blocks.NETHER_PORTAL, MiscellaneousBlocks.PORTAL_BLOCK.get(),MAX_PORTAL_SEARCH_DEPTH,axis);
            }
        }
        return InteractionResult.PASS;
    }
}
