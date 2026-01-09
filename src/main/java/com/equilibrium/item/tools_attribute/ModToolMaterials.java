package com.equilibrium.item.tools_attribute;

import com.equilibrium.item.Metal;
import com.google.common.base.Suppliers;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Supplier;

public enum ModToolMaterials implements ToolMaterial {

    //燧石武器无法附魔
    FLINT_HATCHET(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 360, 2.0F, 0.0F, 0, () -> Ingredient.ofItems(Metal.FLINT)),
    FLINT_AXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1600, 8.0F, 0.0F, 0, () -> Ingredient.ofItems(Metal.FLINT)),
    FLINT_SHOVEL(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 360, 3F, 0.0F, 0, () -> Ingredient.ofItems(Metal.FLINT)),
    FLINT_KNIFE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 360, 4.0F, 0.0F, 0, () -> Ingredient.ofItems(Items.FLINT)),

    COPPER_AXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 4800, 12.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    COPPER_SHOVEL(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1600, 4.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    COPPER_DAGGER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1600, 12.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    COPPER_PICKAXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 4800, 12.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    COPPER_SWORD(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 3200, 12.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    COPPER_HOE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 3200, 12.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    COPPER_HAMMER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 11200, 4.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),


    SILVER_AXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 4800, 12.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    SILVER_SHOVEL(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1600, 4.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    SILVER_DAGGER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1600, 12.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    SILVER_PICKAXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 4800, 12.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    SILVER_SWORD(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 3200, 12.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    SILVER_HOE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 3200, 12.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    SILVER_HAMMER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 11200, 4.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),



    //金武器更容易获得高级附魔,采集速度也更快
    GOLD_AXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 4800, 18.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    GOLD_SHOVEL(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1600, 15.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    GOLD_DAGGER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1600, 18.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    GOLD_PICKAXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 4800, 18.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    GOLD_SWORD(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 3200, 18.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    GOLD_HOE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 3200, 18.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    GOLD_HAMMER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 11200, 15.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),


    IRON_AXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 9600, 16.0F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    IRON_SHOVEL(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 3200, 8.0F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    IRON_DAGGER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 3200, 16.0F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    IRON_PICKAXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 9600, 16.0F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    IRON_SWORD(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 6400, 16.0F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    IRON_HOE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 6400, 16.0F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    IRON_HAMMER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 22400, 8.0F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),


    MITHRIL_AXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 76800, 32.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    MITHRIL_SHOVEL(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 25600, 12.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    MITHRIL_DAGGER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 25600, 32.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    MITHRIL_PICKAXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 76800, 32.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    MITHRIL_SWORD(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 51200, 32.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    MITHRIL_HOE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 51200, 32.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    MITHRIL_HAMMER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 179200, 24.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),

    ADAMANTIUM_AXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 309600, 32.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),
    ADAMANTIUM_SHOVEL(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 102400, 16.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),
    ADAMANTIUM_DAGGER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 102400, 32.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),
    ADAMANTIUM_PICKAXE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 309600, 48.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),
    ADAMANTIUM_SWORD(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 204800, 32.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),
    ADAMANTIUM_HOE(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 204800, 32.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),
    ADAMANTIUM_HAMMER(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 179200, 32.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget));


    private final TagKey<Block> inverseTag;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    private ModToolMaterials(
            final TagKey<Block> inverseTag,
            final int itemDurability,
            final float miningSpeed,
            final float attackDamage,
            final int enchantability,
            final Supplier<Ingredient> repairIngredient
    ) {
        this.inverseTag = inverseTag;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get);
    }

    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public TagKey<Block> getInverseTag() {
        return this.inverseTag;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return (Ingredient)this.repairIngredient.get();
    }
}
