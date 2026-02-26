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
    //金武器更容易获得高级附魔,采集速度也更快




    //BlockTags.AIR,只有空气无法被有效挖掘,实际含义是工具可以无惩罚地有效挖掘所有方块,如果方块适合该工具,还会进一步加速,而不是使用默认值

    FLINT_SHOVEL(BlockTags.AIR, 360, 0.5F, 0.0F, 0, () -> Ingredient.ofItems(Metal.FLINT)),
    COPPER_SHOVEL(BlockTags.AIR, 1600, 1.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    SILVER_SHOVEL(BlockTags.AIR, 1600, 1.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    GOLD_SHOVEL(BlockTags.AIR, 1600, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    IRON_SHOVEL(BlockTags.AIR, 3200, 1.5F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    MITHRIL_SHOVEL(BlockTags.AIR, 25600, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    ADAMANTIUM_SHOVEL(BlockTags.AIR, 102400, 4.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),



    FLINT_HATCHET(BlockTags.AIR, 360, 0.25F, 0.0F, 0, () -> Ingredient.ofItems(Metal.FLINT)),
    FLINT_AXE(BlockTags.AIR, 1600, 0.5F, 0.0F, 0, () -> Ingredient.ofItems(Metal.FLINT)),
    COPPER_AXE(BlockTags.AIR, 4800, 1.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    SILVER_AXE(BlockTags.AIR, 4800, 1.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    GOLD_AXE(BlockTags.AIR, 4800, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    IRON_AXE(BlockTags.AIR, 9600, 1.5F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    MITHRIL_AXE(BlockTags.AIR, 76800, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    ADAMANTIUM_AXE(BlockTags.AIR, 309600, 4.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),



    FLINT_KNIFE(BlockTags.AIR, 360, 0.5F, 0.0F, 0, () -> Ingredient.ofItems(Items.FLINT)),
    COPPER_DAGGER(BlockTags.AIR, 1600, 1.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    SILVER_DAGGER(BlockTags.AIR, 1600, 1.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    GOLD_DAGGER(BlockTags.AIR, 1600, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    IRON_DAGGER(BlockTags.AIR, 3200, 1.5F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    MITHRIL_DAGGER(BlockTags.AIR, 25600, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    ADAMANTIUM_DAGGER(BlockTags.AIR, 102400, 4.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),





    COPPER_PICKAXE(BlockTags.AIR, 4800, 0.5F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    SILVER_PICKAXE(BlockTags.AIR, 4800, 0.5F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    GOLD_PICKAXE(BlockTags.AIR, 4800,  1.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    IRON_PICKAXE(BlockTags.AIR, 9600, 0.75F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    MITHRIL_PICKAXE(BlockTags.AIR, 76800, 1.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    ADAMANTIUM_PICKAXE(BlockTags.AIR, 309600, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),


    COPPER_HAMMER(BlockTags.AIR, 11200, 0.25F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    SILVER_HAMMER(BlockTags.AIR, 11200, 0.25F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    GOLD_HAMMER(BlockTags.AIR, 11200, 0.5F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    IRON_HAMMER(BlockTags.AIR, 22400, 0.375F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    MITHRIL_HAMMER(BlockTags.AIR, 179200, 0.5F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    ADAMANTIUM_HAMMER(BlockTags.AIR, 179200, 1.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),








    COPPER_SWORD(BlockTags.AIR, 3200, 1.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    SILVER_SWORD(BlockTags.AIR, 3200, 1.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    GOLD_SWORD(BlockTags.AIR, 3200, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    IRON_SWORD(BlockTags.AIR, 6400, 1.5F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    MITHRIL_SWORD(BlockTags.AIR, 51200, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    ADAMANTIUM_SWORD(BlockTags.AIR, 204800, 4.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget)),



    COPPER_HOE(BlockTags.AIR, 3200, 1.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.copper_nugget)),
    SILVER_HOE(BlockTags.AIR, 3200, 1.0F, 0.0F, 15, () -> Ingredient.ofItems(Metal.silver_nugget)),
    GOLD_HOE(BlockTags.AIR, 3200, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Items.GOLD_NUGGET)),
    IRON_HOE(BlockTags.AIR, 6400, 1.5F, 0.0F, 15, () -> Ingredient.ofItems(Items.IRON_NUGGET)),
    MITHRIL_HOE(BlockTags.AIR, 51200, 2.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.mithril_nugget)),
    ADAMANTIUM_HOE(BlockTags.AIR, 204800, 4.0F, 0.0F, 25, () -> Ingredient.ofItems(Metal.adamantium_nugget));







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
