package com.equilibrium.mixin.vanilla_itemsmixin;

import net.minecraft.item.ToolMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ToolMaterials.class)
public class WoodenMaterialsModify {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ToolMaterials;<init>(Ljava/lang/String;ILnet/minecraft/registry/tag/TagKey;IFFILjava/util/function/Supplier;)V",ordinal = 0),index = 4)
    private static float modifyWoodParameters(float par5) {
        return 0.5F;
    }
}
