package com.equilibrium.block.portalblock;

import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.item.Metal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


import static com.equilibrium.block.portalblock.PortalBlockFinder.replacePortalBlocks;

public class PortalBlockCast {

    private static final int NUGGET_COST = 16;
    private static final int MAX_PORTAL_SEARCH_DEPTH = 64;

    public static ActionResult tryCastOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(state.getBlock() instanceof NetherPortalBlock){
            Direction.Axis axis = state.get(NetherPortalBlock.AXIS);
            ItemStack handStack = player.getMainHandStack();
            if(handStack.isOf(Metal.mithril_nugget) && handStack.getCount()>=NUGGET_COST){
                handStack.decrementUnlessCreative(NUGGET_COST,player);
                replacePortalBlocks(world,pos, (NetherPortalBlock)Blocks.NETHER_PORTAL, (PortalBlock) ModBlocksRegistry.PORTAL_BLOCK,MAX_PORTAL_SEARCH_DEPTH,axis);
            }
        }
        return ActionResult.PASS;
    }
}
