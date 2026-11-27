package com.equilibrium.item.fishrod;

import com.equilibrium.item.Metal;
import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public enum FishingRodMaterial {

    FLINT(3, 0, () -> Ingredient.of(Metal.FLINT)),
    COPPER(8, 15, () -> Ingredient.of(Metal.copper_nugget)),
    SILVER(8, 15, () -> Ingredient.of(Metal.silver_nugget)),
    GOLD(8, 25, () -> Ingredient.of(Items.GOLD_NUGGET)),
    MITHRIL(128, 25, () -> Ingredient.of(Metal.mithril_nugget)),
    ADAMANTIUM(512, 18, () -> Ingredient.of(Metal.adamantium_nugget));

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
