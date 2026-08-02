package com.equilibrium.item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class ItemComponentModifier {
    public Random random = new Random();
    public static final FoodProperties GOLDEN_APPLE_FOOD_COMPONENT = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 400, 0), 1.0F)
            .alwaysEdible()
            .build();

    public static final FoodProperties ENCHANTING_GOLDEN_APPLE_FOOD_COMPONENT = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 800, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 800, 1), 1.0F)
            .alwaysEdible()
            .build();



    // 定义要修改的物品和对应的食物属性
    private static final Map<Item, FoodProperties> FOOD_MODIFICATIONS = Map.of(
            Items.GOLDEN_APPLE, GOLDEN_APPLE_FOOD_COMPONENT,
            Items.ENCHANTED_GOLDEN_APPLE, ENCHANTING_GOLDEN_APPLE_FOOD_COMPONENT,
            Items.BREAD, createFood(2, 8F,ItemStack.EMPTY,List.of()),
            Items.PUMPKIN_PIE, createFood(10, 12F,ItemStack.EMPTY,List.of()),
            Items.MELON_SLICE, createFood(1, 0F,ItemStack.EMPTY,List.of()),
            Items.BAKED_POTATO, createFood(2, 6F,ItemStack.EMPTY,List.of()),

            Items.WHEAT_SEEDS, createFood(0, 1F,ItemStack.EMPTY,List.of()),

            Items.PUMPKIN_SEEDS,createFood(3, 3F,ItemStack.EMPTY,List.of()),
            Items.MELON_SEEDS,createFood(0, 1F,ItemStack.EMPTY,List.of()),
            Items.SUGAR,createFood(1, 1F,ItemStack.EMPTY,List.of())

    );

    public static void foodComponentModify() {
        DefaultItemComponentEvents.MODIFY.register(context -> {
            for (Map.Entry<Item, FoodProperties> entry : FOOD_MODIFICATIONS.entrySet()) {
                Item item = entry.getKey();
                FoodProperties food = entry.getValue();
                context.modify(item, builder -> {
                    builder.set(DataComponents.FOOD, food);
//                    System.out.println("Modify: " + Registries.ITEM.getId(item) + " attributes");
                });
            }
        });
    }




    private static FoodProperties createFood(int nutrition, float saturation, ItemStack usingConvertsTo, List<FoodProperties.PossibleEffect> effects) {
        return new FoodProperties(nutrition,saturation,false,1.6F, Optional.of(usingConvertsTo), effects);
    }


}