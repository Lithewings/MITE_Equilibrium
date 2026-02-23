package com.equilibrium.mixin.vanilla_itemsmixin.tool;


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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

@Mixin(ArmorMaterials.class)
public class ArmorMaterialsMixin {
    @Shadow
    private static RegistryEntry<ArmorMaterial> register(
            String id,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantability,
            RegistryEntry<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient
    ) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(Identifier.ofVanilla(id)));
        return register(id, defense, enchantability, equipSound, toughness, knockbackResistance, repairIngredient, list);
    }
    @Shadow
    private static RegistryEntry<ArmorMaterial> register(
            String id,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantability,
            RegistryEntry<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient,
            List<ArmorMaterial.Layer> layers
    ) {
        EnumMap<ArmorItem.Type, Integer> enumMap = new EnumMap(ArmorItem.Type.class);

        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            enumMap.put(type, (Integer)defense.get(type));
        }

        return Registry.registerReference(
                Registries.ARMOR_MATERIAL,
                Identifier.ofVanilla(id),
                new ArmorMaterial(enumMap, enchantability, equipSound, repairIngredient, layers, toughness, knockbackResistance)
        );
    }

        @Shadow
        @Final
        public static RegistryEntry<ArmorMaterial> LEATHER;

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ArmorMaterials;register(Ljava/lang/String;Ljava/util/EnumMap;ILnet/minecraft/registry/entry/RegistryEntry;FFLjava/util/function/Supplier;)Lnet/minecraft/registry/entry/RegistryEntry;"
            )
    )
    private static RegistryEntry<ArmorMaterial> modifyArmorValues(
            String id, EnumMap<ArmorItem.Type, Integer> defensePoints, int enchantability, RegistryEntry<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        // 修改铁盔甲
        if ("iron".equals(id)) {
            defensePoints.put(ArmorItem.Type.BOOTS, 2);
            defensePoints.put(ArmorItem.Type.LEGGINGS, 5);
            defensePoints.put(ArmorItem.Type.CHESTPLATE, 6);
            defensePoints.put(ArmorItem.Type.HELMET, 2);
            repairIngredient = () -> Ingredient.ofItems(Items.IRON_NUGGET);
        }
        // 修改黄金盔甲
        else if ("gold".equals(id)) {
            defensePoints.put(ArmorItem.Type.BOOTS, 1);
            defensePoints.put(ArmorItem.Type.LEGGINGS, 3);
            defensePoints.put(ArmorItem.Type.CHESTPLATE, 5);
            defensePoints.put(ArmorItem.Type.HELMET, 2);
            repairIngredient = () -> Ingredient.ofItems(Items.GOLD_NUGGET);
        }
        // 添加其他材质的修改...
        // 调用原版注册方法
        return register(
                id, defensePoints, enchantability, equipSound,
                toughness, knockbackResistance, repairIngredient);

    }











}





//    @Shadow
//    @Final
//    public static final RegistryEntry<ArmorMaterial> IRON = register("iron", Util.make(new EnumMap(ArmorItem.Type.class), map -> {
//        map.put(ArmorItem.Type.BOOTS, 2);
//        map.put(ArmorItem.Type.LEGGINGS, 5);
//        map.put(ArmorItem.Type.CHESTPLATE, 6);
//        map.put(ArmorItem.Type.HELMET, 2);
//        map.put(ArmorItem.Type.BODY, 5);
//    }), 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.IRON_NUGGET));
//    @Shadow
//    @Final
//    public static final RegistryEntry<ArmorMaterial> GOLD = register("gold", Util.make(new EnumMap(ArmorItem.Type.class), map -> {
//        map.put(ArmorItem.Type.BOOTS, 1);
//        map.put(ArmorItem.Type.LEGGINGS, 3);
//        map.put(ArmorItem.Type.CHESTPLATE, 5);
//        map.put(ArmorItem.Type.HELMET, 2);
//        map.put(ArmorItem.Type.BODY, 7);
//    }), 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> Ingredient.ofItems(Items.GOLD_NUGGET));
//    @Shadow
//    @Final
//    public static final RegistryEntry<ArmorMaterial> DIAMOND = register("diamond", Util.make(new EnumMap(ArmorItem.Type.class), map -> {
//        map.put(ArmorItem.Type.BOOTS, 3);
//        map.put(ArmorItem.Type.LEGGINGS, 6);
//        map.put(ArmorItem.Type.CHESTPLATE, 8);
//        map.put(ArmorItem.Type.HELMET, 3);
//        map.put(ArmorItem.Type.BODY, 11);
//    }), 1, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> Ingredient.ofItems(Items.DIAMOND));

