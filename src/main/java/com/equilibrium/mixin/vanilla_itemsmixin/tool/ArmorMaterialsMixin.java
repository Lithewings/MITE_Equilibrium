package com.equilibrium.mixin.vanilla_itemsmixin.tool;


import com.equilibrium.item.OtherItems;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

@Mixin(ArmorMaterials.class)
public class ArmorMaterialsMixin {
    //6个参数的register
    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ArmorMaterials;register(Ljava/lang/String;Ljava/util/EnumMap;ILnet/minecraft/registry/entry/RegistryEntry;FFLjava/util/function/Supplier;)Lnet/minecraft/registry/entry/RegistryEntry;"
            )
    )
    private static void modifyRepairIngredient(Args args) {
        String id = args.get(0);
        if ("iron".equals(id)) {
            // 铁盔甲 → 铁粒
            args.set(6, (Supplier<Ingredient>) () -> Ingredient.ofItems(Items.IRON_NUGGET));
        } else if ("gold".equals(id)) {
            // 金盔甲 → 金粒
            args.set(6, (Supplier<Ingredient>) () -> Ingredient.ofItems(Items.GOLD_NUGGET));
        } else if ("leather".equals(id)) {
            // 皮革装备->皮革线
            args.set(6, (Supplier<Ingredient>) () -> Ingredient.ofItems(OtherItems.SINEW));
        }

    }
    //7个参数的register
    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ArmorMaterials;register(Ljava/lang/String;Ljava/util/EnumMap;ILnet/minecraft/registry/entry/RegistryEntry;FFLjava/util/function/Supplier;Ljava/util/List;)Lnet/minecraft/registry/entry/RegistryEntry;"
            )
    )
    private static void modifyRepairIngredient1(Args args) {
        String id = args.get(0);
        if ("leather".equals(id)) {
            // 皮革装备->皮革线
            args.set(6, (Supplier<Ingredient>) () -> Ingredient.ofItems(OtherItems.SINEW));
        }
    }

//参数位置（args索引）	类型	含义
//0	String	盔甲材料 id（如 "iron"）
//1	EnumMap<ArmorItem.Type, Integer>	各部位防御值
//2	int	附魔能力
//3	RegistryEntry<SoundEvent>	装备音效
//4	float	韧性
//5	float	击退抗性
//6	Supplier<Ingredient>	修复材料
//修改盔甲属性的API:

    // ==================== LEATHER (method_48412) ====================
//    @ModifyConstant(method = "method_48412", constant = @Constant(intValue = 1, ordinal = 0))
//    private static int doubleLeatherBoots(int value) { return 2; }
//
//    @ModifyConstant(method = "method_48412", constant = @Constant(intValue = 2, ordinal = 0))
//    private static int doubleLeatherLeggings(int value) { return 4; }
//
//    @ModifyConstant(method = "method_48412", constant = @Constant(intValue = 3, ordinal = 0))
//    private static int doubleLeatherChestplate(int value) { return 6; }
//
//    @ModifyConstant(method = "method_48412", constant = @Constant(intValue = 1, ordinal = 1))
//    private static int doubleLeatherHelmet(int value) { return 2; }
//
//    @ModifyConstant(method = "method_48412", constant = @Constant(intValue = 3, ordinal = 1))
//    private static int doubleLeatherBody(int value) { return 6; }

    // ==================== CHAIN (method_48411) ====================
//    @ModifyConstant(method = "method_48411", constant = @Constant(intValue = 1, ordinal = 0))
//    private static int doubleChainBoots(int value) { return 2; }
//
//    @ModifyConstant(method = "method_48411", constant = @Constant(intValue = 4, ordinal = 0))
//    private static int doubleChainLeggings(int value) { return 8; }
//
//    @ModifyConstant(method = "method_48411", constant = @Constant(intValue = 5, ordinal = 0))
//    private static int doubleChainChestplate(int value) { return 10; }
//
//    @ModifyConstant(method = "method_48411", constant = @Constant(intValue = 2, ordinal = 0))
//    private static int doubleChainHelmet(int value) { return 4; }
//
//    @ModifyConstant(method = "method_48411", constant = @Constant(intValue = 4, ordinal = 1))
//    private static int doubleChainBody(int value) { return 8; }

    // ==================== IRON (method_48410) ====================
//    @ModifyConstant(method = "method_48410", constant = @Constant(intValue = 2, ordinal = 0))
//    private static int doubleIronBoots(int value) { return 4; }
//
//    @ModifyConstant(method = "method_48410", constant = @Constant(intValue = 5, ordinal = 0))
//    private static int doubleIronLeggings(int value) { return 10; }
//
//    @ModifyConstant(method = "method_48410", constant = @Constant(intValue = 6, ordinal = 0))
//    private static int doubleIronChestplate(int value) { return 12; }
//
//    @ModifyConstant(method = "method_48410", constant = @Constant(intValue = 2, ordinal = 1))
//    private static int doubleIronHelmet(int value) { return 4; }
//
//    @ModifyConstant(method = "method_48410", constant = @Constant(intValue = 5, ordinal = 1))
//    private static int doubleIronBody(int value) { return 10; }
//
//    // ==================== GOLD (method_48409) ====================
//    @ModifyConstant(method = "method_48409", constant = @Constant(intValue = 1, ordinal = 0))
//    private static int doubleGoldBoots(int value) { return 2; }
//
//    @ModifyConstant(method = "method_48409", constant = @Constant(intValue = 3, ordinal = 0))
//    private static int doubleGoldLeggings(int value) { return 6; }
//
//    @ModifyConstant(method = "method_48409", constant = @Constant(intValue = 5, ordinal = 0))
//    private static int doubleGoldChestplate(int value) { return 10; }
//
//    @ModifyConstant(method = "method_48409", constant = @Constant(intValue = 2, ordinal = 0))
//    private static int doubleGoldHelmet(int value) { return 4; }
//
//    @ModifyConstant(method = "method_48409", constant = @Constant(intValue = 7, ordinal = 0))
//    private static int doubleGoldBody(int value) { return 14; }
//
//    // ==================== DIAMOND (method_48408) ====================
//    @ModifyConstant(method = "method_48408", constant = @Constant(intValue = 3, ordinal = 0))
//    private static int doubleDiamondBoots(int value) { return 6; }
//
//    @ModifyConstant(method = "method_48408", constant = @Constant(intValue = 6, ordinal = 0))
//    private static int doubleDiamondLeggings(int value) { return 12; }
//
//    @ModifyConstant(method = "method_48408", constant = @Constant(intValue = 8, ordinal = 0))
//    private static int doubleDiamondChestplate(int value) { return 16; }
//
//    @ModifyConstant(method = "method_48408", constant = @Constant(intValue = 3, ordinal = 1))
//    private static int doubleDiamondHelmet(int value) { return 6; }
//
//    @ModifyConstant(method = "method_48408", constant = @Constant(intValue = 11, ordinal = 0))
//    private static int doubleDiamondBody(int value) { return 22; }
//
//    // ==================== TURTLE (method_48407) ====================
//    @ModifyConstant(method = "method_48407", constant = @Constant(intValue = 2, ordinal = 0))
//    private static int doubleTurtleBoots(int value) { return 4; }
//
//    @ModifyConstant(method = "method_48407", constant = @Constant(intValue = 5, ordinal = 0))
//    private static int doubleTurtleLeggings(int value) { return 10; }
//
//    @ModifyConstant(method = "method_48407", constant = @Constant(intValue = 6, ordinal = 0))
//    private static int doubleTurtleChestplate(int value) { return 12; }
//
//    @ModifyConstant(method = "method_48407", constant = @Constant(intValue = 2, ordinal = 1))
//    private static int doubleTurtleHelmet(int value) { return 4; }
//
//    @ModifyConstant(method = "method_48407", constant = @Constant(intValue = 5, ordinal = 1))
//    private static int doubleTurtleBody(int value) { return 10; }
//
//    // ==================== NETHERITE (method_48406) ====================
//    @ModifyConstant(method = "method_48406", constant = @Constant(intValue = 3, ordinal = 0))
//    private static int doubleNetheriteBoots(int value) { return 6; }
//
//    @ModifyConstant(method = "method_48406", constant = @Constant(intValue = 6, ordinal = 0))
//    private static int doubleNetheriteLeggings(int value) { return 12; }
//
//    @ModifyConstant(method = "method_48406", constant = @Constant(intValue = 8, ordinal = 0))
//    private static int doubleNetheriteChestplate(int value) { return 16; }
//
//    @ModifyConstant(method = "method_48406", constant = @Constant(intValue = 3, ordinal = 1))
//    private static int doubleNetheriteHelmet(int value) { return 6; }
//
//    @ModifyConstant(method = "method_48406", constant = @Constant(intValue = 11, ordinal = 0))
//    private static int doubleNetheriteBody(int value) { return 22; }
//
//    // ==================== ARMADILLO (method_48405) ====================
//    @ModifyConstant(method = "method_48405", constant = @Constant(intValue = 3, ordinal = 0))
//    private static int doubleArmadilloBoots(int value) { return 6; }
//
//    @ModifyConstant(method = "method_48405", constant = @Constant(intValue = 6, ordinal = 0))
//    private static int doubleArmadilloLeggings(int value) { return 12; }
//
//    @ModifyConstant(method = "method_48405", constant = @Constant(intValue = 8, ordinal = 0))
//    private static int doubleArmadilloChestplate(int value) { return 16; }
//
//    @ModifyConstant(method = "method_48405", constant = @Constant(intValue = 3, ordinal = 1))
//    private static int doubleArmadilloHelmet(int value) { return 6; }
//
//    @ModifyConstant(method = "method_48405", constant = @Constant(intValue = 11, ordinal = 0))
//    private static int doubleArmadilloBody(int value) { return 22; }
}


