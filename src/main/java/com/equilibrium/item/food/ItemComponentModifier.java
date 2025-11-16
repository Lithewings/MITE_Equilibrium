package com.equilibrium.item.food;

import com.equilibrium.item.EnchantedAppleItem;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemComponentModifier {
    public static final FoodComponent GOLDEN_APPLE = new FoodComponent.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 0), 1.0F)
            .alwaysEdible()
            .build();

    public static final FoodComponent ENCHANTING_GOLDEN_APPLE = new FoodComponent.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 800, 1), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0), 1.0F)
            .alwaysEdible()
            .build();


    // 定义要修改的物品和对应的食物属性
    private static final Map<Item, FoodComponent> FOOD_MODIFICATIONS = Map.of(
            Items.GOLDEN_APPLE, GOLDEN_APPLE,
            Items.BREAD, createFood(2, 8F,ItemStack.EMPTY,List.of()),
            Items.PUMPKIN_PIE, createFood(10, 12F,ItemStack.EMPTY,List.of()),
            Items.MELON_SLICE, createFood(1, 0F,ItemStack.EMPTY,List.of()),
            Items.BAKED_POTATO, createFood(2, 6F,ItemStack.EMPTY,List.of()),

            Items.WHEAT_SEEDS, createFood(1, 0F,ItemStack.EMPTY,List.of()),
            Items.PUMPKIN_SEEDS,createFood(1, 1F,ItemStack.EMPTY,List.of()),
            Items.MELON_SEEDS,createFood(0, 1F,ItemStack.EMPTY,List.of()),
            Items.SUGAR,createFood(0, 1F,ItemStack.EMPTY,List.of())

    );

    public static void foodComponentModify() {
        DefaultItemComponentEvents.MODIFY.register(context -> {
            for (Map.Entry<Item, FoodComponent> entry : FOOD_MODIFICATIONS.entrySet()) {
                Item item = entry.getKey();
                FoodComponent food = entry.getValue();
                context.modify(item, builder -> {
                    builder.add(DataComponentTypes.FOOD, food);
//                    System.out.println("Modify: " + Registries.ITEM.getId(item) + " attributes");
                });
            }
        });
    }




    private static FoodComponent createFood(int nutrition, float saturation, ItemStack usingConvertsTo, List<FoodComponent.StatusEffectEntry> effects) {
        return new FoodComponent(nutrition,saturation,false,1.6F, Optional.of(usingConvertsTo), effects);
    }


}