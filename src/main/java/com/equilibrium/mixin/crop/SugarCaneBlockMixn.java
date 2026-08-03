package com.equilibrium.mixin.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixn extends Block {
    public SugarCaneBlockMixn(Properties settings) {
        super(settings);
    }
    @Inject(method = "randomTick",at = @At("HEAD"),cancellable = true)
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if(random.nextInt(128/16)!=0)
            ci.cancel();
    }
}
