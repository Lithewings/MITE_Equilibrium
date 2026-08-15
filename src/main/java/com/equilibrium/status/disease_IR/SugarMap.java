package com.equilibrium.status.disease_IR;

import java.util.Map;

import com.equilibrium.item.food.FoodItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class SugarMap {
    public final static Map<Item ,Integer> SUGAR_MAP =Map.of(
            Items.SUGAR,4800,
            Items.BREAD,4800,
            Items.PUMPKIN_PIE,4800,
            Items.APPLE,4800,
            Items.GOLDEN_APPLE,4800,
            Items.ENCHANTED_GOLDEN_APPLE,4800,
            Items.SWEET_BERRIES,4800,
            Items.GLOW_BERRIES,4800,
            Items.MELON_SLICE,1200,
            FoodItems.BLUEBERRY.asItem(),4800
    );
}
