package com.equilibrium.mixin.vanilla_itemsmixin.tool;


import com.equilibrium.item.miscellaneous.MiscellaneousItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

@Mixin(ArmorMaterials.class)
public class ArmorMaterialsMixin {

    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ArmorMaterials;register(Ljava/lang/String;Ljava/util/EnumMap;ILnet/minecraft/core/Holder;FFLjava/util/function/Supplier;)Lnet/minecraft/core/Holder;"
            )
    )
    private static void modifyRepairIngredient(Args args) {
        String id = args.get(0);
        if ("iron".equals(id)) {
            // 铁盔甲 → 铁粒
            args.set(6, (Supplier<Ingredient>) () -> Ingredient.of(Items.IRON_NUGGET));
        } else if ("gold".equals(id)) {
            // 金盔甲 → 金粒
            args.set(6, (Supplier<Ingredient>) () -> Ingredient.of(Items.GOLD_NUGGET));
        } else if ("leather".equals(id)) {
            // 皮革装备->皮革线
            args.set(6, (Supplier<Ingredient>) () -> Ingredient.of(MiscellaneousItems.SINEW.get()));
        }

    }

    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ArmorMaterials;register(Ljava/lang/String;Ljava/util/EnumMap;ILnet/minecraft/core/Holder;FFLjava/util/function/Supplier;Ljava/util/List;)Lnet/minecraft/core/Holder;"
            )
    )
    private static void modifyRepairIngredient1(Args args) {
        String id = args.get(0);
        if ("leather".equals(id)) {
            // 皮革装备->皮革线
            args.set(6, (Supplier<Ingredient>) () -> Ingredient.of(MiscellaneousItems.SINEW.get()));
        }
    }
}


