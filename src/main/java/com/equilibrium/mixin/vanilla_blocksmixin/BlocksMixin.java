package com.equilibrium.mixin.vanilla_blocksmixin;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.sound.BlockSoundGroup;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.ToIntFunction;

@Mixin(Blocks.class)
public class BlocksMixin {

    /**
     * 仅在 short_grass 注册代码段中，将 breakInstantly() 替换为 strength(0.6F)
     */
    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=short_grass"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;SHORT_GRASS:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;breakInstantly()Lnet/minecraft/block/AbstractBlock$Settings;")
    )
    private static AbstractBlock.Settings shortGrassHardness(AbstractBlock.Settings settings) {
        // 将 breakInstantly() 替换为强度 0.1
        return settings.strength(0.1F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=tall_grass"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;TALL_GRASS:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;breakInstantly()Lnet/minecraft/block/AbstractBlock$Settings;")
    )
    private static AbstractBlock.Settings tallGrassHardness(AbstractBlock.Settings settings) {
        // 将 breakInstantly() 替换为强度 0.1
        return settings.strength(0.1F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=sugar_cane"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;SUGAR_CANE:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;breakInstantly()Lnet/minecraft/block/AbstractBlock$Settings;")
    )
    private static AbstractBlock.Settings sugarCaneHardness(AbstractBlock.Settings settings) {
        // 将 breakInstantly() 替换为强度 0.1
        return settings.strength(0.1F);
    }


}




