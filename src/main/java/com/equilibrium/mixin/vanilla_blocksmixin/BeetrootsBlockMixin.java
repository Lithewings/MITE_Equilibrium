package com.equilibrium.mixin.vanilla_blocksmixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.OnServerInitialize.CROP_IS_ILLNESS;

import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

@Mixin(BeetrootBlock.class)
public class BeetrootsBlockMixin extends CropBlock {
    public BeetrootsBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "createBlockStateDefinition",at = @At("TAIL"))
    public void appendProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(CROP_IS_ILLNESS);
    }
}
