package com.equilibrium.mixin.structure_and_dimension.portal;


import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {
    @Inject(method = "onBlockAdded",at=@At(value = "HEAD"), cancellable = true)
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        ci.cancel();
        if (!oldState.isOf(state.getBlock())) {
            if (world.getRegistryKey()!=World.END) {
                Optional<NetherPortal> optional = NetherPortal.getNewPortal(world, pos, Direction.Axis.X);
                if (optional.isPresent()) {
                    ((NetherPortal)optional.get()).createPortal();
                    ci.cancel();
                }
            }

            if (!state.canPlaceAt(world, pos)) {
                world.removeBlock(pos, false);
            }
        }
    }
}
