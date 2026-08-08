package com.equilibrium.status.disease_IR;

import com.equilibrium.item.food.FoodOrFarmItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Map;

public class SugarMap {
    public final static Map<Item ,Integer> SUGAR_MAP =Map.of(
            Items.SUGAR,6400,
            Items.BREAD,3200,
            Items.PUMPKIN_PIE,2400,
            Items.APPLE,1600,
            Items.GOLDEN_APPLE,1600,
            Items.ENCHANTED_GOLDEN_APPLE,1600,
            Items.SWEET_BERRIES,1600,
            Items.GLOW_BERRIES,1600,
            Items.MELON_SLICE,800,
            FoodOrFarmItems.BLUE_BERRY.asItem(),1600
    );
}
