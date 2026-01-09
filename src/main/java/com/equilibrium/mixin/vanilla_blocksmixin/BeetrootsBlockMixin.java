package com.equilibrium.mixin.vanilla_blocksmixin;

import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.OnServerInitialize.CROP_IS_ILLNESS;

@Mixin(BeetrootsBlock.class)
public class BeetrootsBlockMixin extends CropBlock {
    public BeetrootsBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "appendProperties",at = @At("HEAD"), cancellable = true)
    public void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        ci.cancel();
        builder.add(AGE);
        builder.add(CROP_IS_ILLNESS);
    }
}
