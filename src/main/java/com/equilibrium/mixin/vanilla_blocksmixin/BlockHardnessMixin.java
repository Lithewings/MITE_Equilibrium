package com.equilibrium.mixin.vanilla_blocksmixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.objectweb.asm.Opcodes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

@Mixin(Blocks.class)
public class BlockHardnessMixin {

    // ========== 原 breakInstantly() → 改用 instabreak() ==========

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=short_grass"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;SHORT_GRASS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;instabreak()Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;")
    )
    private static BlockBehaviour.Properties shortGrassHardness(BlockBehaviour.Properties settings) {
        return settings.strength(0.01F);
    }
    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=tall_grass"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;TALL_GRASS:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;instabreak()Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;")
    )
    private static BlockBehaviour.Properties tallGrassHardness(BlockBehaviour.Properties settings) {
        return settings.strength(0.01F);
    }
    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=sugar_cane"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;SUGAR_CANE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;instabreak()Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;")
    )
    private static BlockBehaviour.Properties sugarCaneHardness(BlockBehaviour.Properties settings) {
        return settings.strength(0.01F);
    }

    // ========== 以下方法保持不变，NeoForge 未修改这些调用 ==========

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=chest"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;CHEST:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;strength(F)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;")
    )
    private static BlockBehaviour.Properties chestHardness(BlockBehaviour.Properties settings, float strength) {
        return settings.strength(0.1F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=anvil"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;ANVIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;strength(FF)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;")
    )
    private static BlockBehaviour.Properties anvilHardness(BlockBehaviour.Properties instance, float hardness, float resistance) {
        return instance.strength(0.1F, 1200F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=chipped_anvil"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;CHIPPED_ANVIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;strength(FF)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;")
    )
    private static BlockBehaviour.Properties chippedAnvilHardness(BlockBehaviour.Properties instance, float hardness, float resistance) {
        return instance.strength(0.1F, 1200F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=damaged_anvil"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;DAMAGED_ANVIL:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;strength(FF)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;")
    )
    private static BlockBehaviour.Properties damagedAnvilHardness(BlockBehaviour.Properties instance, float hardness, float resistance) {
        return instance.strength(0.1F, 1200F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=furnace"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;FURNACE:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;strength(F)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;")
    )
    private static BlockBehaviour.Properties furnaceHardness(BlockBehaviour.Properties instance, float hardness) {
        return instance.strength(0.1F);
    }

    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=obsidian"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;OBSIDIAN:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.PUTSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;strength(FF)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;")
    )
    private static BlockBehaviour.Properties obsidianHardness(BlockBehaviour.Properties instance, float hardness, float resistance) {
        return instance.strength(5.0F, 1200F);
    }
}