package com.equilibrium.mixin.some_special_rules.fence_link_together;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.registry.tag.BlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PaneBlock.class)
public class PaneBlockMixin {
    @ModifyReturnValue(method = "connectsTo", at = @At("RETURN"))
    private boolean doConnectFences(boolean original, @Local BlockState state) {
        return original ? true : state.isIn(BlockTags.FENCES)
                || state.isIn(BlockTags.FENCE_GATES)
                || state.isIn(BlockTags.WALL_SIGNS);
    }
}
