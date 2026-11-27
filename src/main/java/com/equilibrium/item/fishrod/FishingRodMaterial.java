package com.equilibrium.item.fishrod;

import com.equilibrium.item.Metal;
import com.google.common.base.Suppliers;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;

import java.util.function.Supplier;

public enum FishingRodMaterial {

    FLINT(3, 0, () -> Ingredient.ofItems(Metal.FLINT)),
    COPPER(8, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    SILVER(8, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    GOLD(8, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    MITHRIL(128, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    ADAMANTIUM(512, 18, () -> Ingredient.ofItems(Metal.adamantium_nugget));

    private final int durability;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    FishingRodMaterial(int durability, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.durability = durability;
        this.enchantability = enchantability;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get);
    }


    public int getDurability() {
        return this.durability;
    }


    public int getEnchantability() {
        return this.enchantability;
    }


    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
