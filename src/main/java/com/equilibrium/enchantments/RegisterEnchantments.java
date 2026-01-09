package com.equilibrium.enchantments;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.AttributeEnchantmentEffect;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class RegisterEnchantments {

    public static final RegistryKey<Enchantment> REACHING = of("reaching");

    public static final RegistryKey<Enchantment> ENCHANTED_FOOD= of("enchanted_food");

    public static final RegistryKey<Enchantment> SPEED= of("speed");


    public static final RegistryKey<Enchantment> EXTEND= of("extend");


    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.getValue()));
    }
    private static RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID,id));
    }
    public static void bootstrap(Registerable<Enchantment> registry){
        RegistryEntryLookup<Enchantment> registryEntryLookup2 = registry.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        RegistryEntryLookup<Item> registryEntryLookup3 = registry.getRegistryLookup(RegistryKeys.ITEM);

        register(
                registry,
                REACHING,
                Enchantment.builder(
                                Enchantment.definition(
                                        RegistryEntryList.of(Items.BUCKET.getRegistryEntry()),
                                        2,
                                        1,
                                        Enchantment.leveledCost(5, 9),
                                        Enchantment.leveledCost(65, 9),
                                        20,
                                        AttributeModifierSlot.MAINHAND
                                )

                        ).addEffect(
                        EnchantmentEffectComponentTypes.ATTRIBUTES,
                        new AttributeEnchantmentEffect(
                                Identifier.of(MOD_ID,"reaching"),
                                EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE,
                                new EnchantmentLevelBasedValue.LevelsSquared(4.0F),
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ))

        );
        register(
                registry,
                ENCHANTED_FOOD,
                Enchantment.builder(
                        Enchantment.definition(
                                RegistryEntryList.of(Items.BUCKET.getRegistryEntry()),
                                2,
                                1,
                                Enchantment.leveledCost(5, 9),
                                Enchantment.leveledCost(65, 9),
                                20,
                                AttributeModifierSlot.ARMOR
                        )

                )
        );
        register(
                registry,
                SPEED,
                Enchantment.builder(
                        Enchantment.definition(
                                RegistryEntryList.of(Items.LEATHER_BOOTS.getRegistryEntry()),
                                2,
                                1,
                                Enchantment.leveledCost(5, 9),
                                Enchantment.leveledCost(65, 9),
                                20,
                                AttributeModifierSlot.ARMOR
                        )

                )
        );

    }


    public static void registerEnchantments(){

    }
}
