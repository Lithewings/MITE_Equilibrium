package com.equilibrium.mixin.vanilla_itemsmixin;

import net.minecraft.world.item.Tiers;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Tiers.class)   // 注意类名改为 Tiers
public class WoodenMaterialsModify {

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Tiers;<init>(Ljava/lang/String;ILnet/minecraft/tags/TagKey;IFFILjava/util/function/Supplier;)V",
                    ordinal = 0   // 对应 WOOD 枚举的构造调用
            ),
            index = 4        // 修改第5个参数（speed，即挖掘速度）
    )
    private static float modifyWoodParameters(float par5) {
        return 0.5F;     // 将 WOOD 的挖掘速度改为 0.5
    }
}