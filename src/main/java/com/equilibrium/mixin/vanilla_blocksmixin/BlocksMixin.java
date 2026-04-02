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
        return settings.strength(0.01F);
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
        return settings.strength(0.01F);
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
        return settings.strength(0.01F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=chest"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;CHEST:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;strength(F)Lnet/minecraft/block/AbstractBlock$Settings;")
    )
    private static AbstractBlock.Settings chestHardness(AbstractBlock.Settings settings, float strength) {
        return settings.strength(0.1F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=anvil"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;ANVIL:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;strength(FF)Lnet/minecraft/block/AbstractBlock$Settings;")
    )
    private static AbstractBlock.Settings anvilHardness(AbstractBlock.Settings instance, float hardness, float resistance) {
        return instance.strength(0.1F,1200F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=chipped_anvil"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;CHIPPED_ANVIL:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;strength(FF)Lnet/minecraft/block/AbstractBlock$Settings;")
    )
    private static AbstractBlock.Settings chippedAnvilHardness(AbstractBlock.Settings instance, float hardness, float resistance) {
        return instance.strength(0.1F,1200F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=damaged_anvil"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;DAMAGED_ANVIL:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;strength(FF)Lnet/minecraft/block/AbstractBlock$Settings;")
    )
    private static AbstractBlock.Settings damagedAnvilHardness(AbstractBlock.Settings instance, float hardness, float resistance) {
        return instance.strength(0.1F,1200F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=furnace"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;FURNACE:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;strength(F)Lnet/minecraft/block/AbstractBlock$Settings;")
    )
    private static AbstractBlock.Settings furnaceHardness(AbstractBlock.Settings instance, float hardness) {
        return instance.strength(0.1F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=obsidian"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;OBSIDIAN:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;strength(FF)Lnet/minecraft/block/AbstractBlock$Settings;")
    )
    private static AbstractBlock.Settings obsidianHardness(AbstractBlock.Settings instance, float hardness, float resistance) {
        return instance.strength(5F,1200F);
    }






}




