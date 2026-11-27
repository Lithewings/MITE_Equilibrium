package com.equilibrium.item.fishrod;


import com.equilibrium.item.material.MaterialItems;
import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public enum FishingRodMaterial {

    FLINT(3, 0, () -> Ingredient.of(MaterialItems.FLINT)),
    COPPER(8, 15, () -> Ingredient.of(MaterialItems.COPPER_NUGGET)),
    SILVER(8, 15, () -> Ingredient.of(MaterialItems.SILVER_NUGGET)),
    GOLD(8, 25, () -> Ingredient.of(Items.GOLD_NUGGET)),
    MITHRIL(128, 25, () -> Ingredient.of(MaterialItems.MITHRIL_NUGGET)),
    ADAMANTIUM(512, 18, () -> Ingredient.of(MaterialItems.MITHRIL_NUGGET));

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
