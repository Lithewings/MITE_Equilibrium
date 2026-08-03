package com.equilibrium.item.tools_attribute;

import com.equilibrium.item.MaterialItems;
import com.google.common.base.Suppliers;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public enum ToolTiers implements Tier {

    FLINT_SHOVEL(BlockTags.AIR, 360, 0.5F, 0.0F, 0, () -> Ingredient.of(MaterialItems.FLINT)),
    COPPER_SHOVEL(BlockTags.AIR, 1600, 1.0F, 0.0F, 15, () -> Ingredient.of(MaterialItems.COPPER_NUGGET)),
    SILVER_SHOVEL(BlockTags.AIR, 1600, 1.0F, 0.0F, 15, () -> Ingredient.of(MaterialItems.SILVER_NUGGET)),
    GOLD_SHOVEL(BlockTags.AIR, 1600, 2.0F, 0.0F, 25, () -> Ingredient.of(Items.GOLD_NUGGET)),
    IRON_SHOVEL(BlockTags.AIR, 3200, 1.5F, 0.0F, 15, () -> Ingredient.of(Items.IRON_NUGGET)),
    MITHRIL_SHOVEL(BlockTags.AIR, 25600, 2.0F, 0.0F, 25, () -> Ingredient.of(MaterialItems.MITHRIL_NUGGET)),
    ADAMANTIUM_SHOVEL(BlockTags.AIR, 102400, 4.0F, 0.0F, 18, () -> Ingredient.of(MaterialItems.ADAMANTIUM_NUGGET)),

    FLINT_HATCHET(BlockTags.AIR, 360, 0.25F, 0.0F, 0, () -> Ingredient.of(MaterialItems.FLINT)),
    FLINT_AXE(BlockTags.AIR, 1600, 0.5F, 0.0F, 0, () -> Ingredient.of(MaterialItems.FLINT)),
    COPPER_AXE(BlockTags.AIR, 4800, 1.0F, 0.0F, 15, () -> Ingredient.of(MaterialItems.COPPER_NUGGET)),
    SILVER_AXE(BlockTags.AIR, 4800, 1.0F, 0.0F, 15, () -> Ingredient.of(MaterialItems.SILVER_NUGGET)),
    GOLD_AXE(BlockTags.AIR, 4800, 2.0F, 0.0F, 25, () -> Ingredient.of(Items.GOLD_NUGGET)),
    IRON_AXE(BlockTags.AIR, 9600, 1.5F, 0.0F, 15, () -> Ingredient.of(Items.IRON_NUGGET)),
    MITHRIL_AXE(BlockTags.AIR, 76800, 2.0F, 0.0F, 25, () -> Ingredient.of(MaterialItems.MITHRIL_NUGGET)),
    ADAMANTIUM_AXE(BlockTags.AIR, 309600, 4.0F, 0.0F, 18, () -> Ingredient.of(MaterialItems.ADAMANTIUM_NUGGET)),

    FLINT_KNIFE(BlockTags.AIR, 360, 0.5F, 0.0F, 0, () -> Ingredient.of(Items.FLINT)),
    COPPER_DAGGER(BlockTags.AIR, 1600, 1.0F, 0.0F, 15, () -> Ingredient.of(MaterialItems.COPPER_NUGGET)),
    SILVER_DAGGER(BlockTags.AIR, 1600, 1.0F, 0.0F, 15, () -> Ingredient.of(MaterialItems.SILVER_NUGGET)),
    GOLD_DAGGER(BlockTags.AIR, 1600, 2.0F, 0.0F, 25, () -> Ingredient.of(Items.GOLD_NUGGET)),
    IRON_DAGGER(BlockTags.AIR, 3200, 1.5F, 0.0F, 15, () -> Ingredient.of(Items.IRON_NUGGET)),
    MITHRIL_DAGGER(BlockTags.AIR, 25600, 2.0F, 0.0F, 25, () -> Ingredient.of(MaterialItems.MITHRIL_NUGGET)),
    ADAMANTIUM_DAGGER(BlockTags.AIR, 102400, 4.0F, 0.0F, 18, () -> Ingredient.of(MaterialItems.ADAMANTIUM_NUGGET)),

    COPPER_PICKAXE(BlockTags.AIR, 4800, 0.5F, 0.0F, 15, () -> Ingredient.of(MaterialItems.COPPER_NUGGET)),
    SILVER_PICKAXE(BlockTags.AIR, 4800, 0.5F, 0.0F, 15, () -> Ingredient.of(MaterialItems.SILVER_NUGGET)),
    GOLD_PICKAXE(BlockTags.AIR, 4800,  1.0F, 0.0F, 25, () -> Ingredient.of(Items.GOLD_NUGGET)),
    IRON_PICKAXE(BlockTags.AIR, 9600, 0.75F, 0.0F, 15, () -> Ingredient.of(Items.IRON_NUGGET)),
    MITHRIL_PICKAXE(BlockTags.AIR, 76800, 1.0F, 0.0F, 25, () -> Ingredient.of(MaterialItems.MITHRIL_NUGGET)),
    ADAMANTIUM_PICKAXE(BlockTags.AIR, 309600, 2.0F, 0.0F, 18, () -> Ingredient.of(MaterialItems.ADAMANTIUM_NUGGET)),

    COPPER_HAMMER(BlockTags.AIR, 11200, 0.25F, 0.0F, 15, () -> Ingredient.of(MaterialItems.COPPER_NUGGET)),
    SILVER_HAMMER(BlockTags.AIR, 11200, 0.25F, 0.0F, 15, () -> Ingredient.of(MaterialItems.SILVER_NUGGET)),
    GOLD_HAMMER(BlockTags.AIR, 11200, 0.5F, 0.0F, 25, () -> Ingredient.of(Items.GOLD_NUGGET)),
    IRON_HAMMER(BlockTags.AIR, 22400, 0.375F, 0.0F, 15, () -> Ingredient.of(Items.IRON_NUGGET)),
    MITHRIL_HAMMER(BlockTags.AIR, 179200, 0.5F, 0.0F, 25, () -> Ingredient.of(MaterialItems.MITHRIL_NUGGET)),
    ADAMANTIUM_HAMMER(BlockTags.AIR, 179200, 1.0F, 0.0F, 18, () -> Ingredient.of(MaterialItems.ADAMANTIUM_NUGGET)),

    COPPER_SWORD(BlockTags.AIR, 3200, 1.0F, 0.0F, 15, () -> Ingredient.of(MaterialItems.COPPER_NUGGET)),
    SILVER_SWORD(BlockTags.AIR, 3200, 1.0F, 0.0F, 15, () -> Ingredient.of(MaterialItems.SILVER_NUGGET)),
    GOLD_SWORD(BlockTags.AIR, 3200, 2.0F, 0.0F, 25, () -> Ingredient.of(Items.GOLD_NUGGET)),
    IRON_SWORD(BlockTags.AIR, 6400, 1.5F, 0.0F, 15, () -> Ingredient.of(Items.IRON_NUGGET)),
    MITHRIL_SWORD(BlockTags.AIR, 51200, 2.0F, 0.0F, 25, () -> Ingredient.of(MaterialItems.MITHRIL_NUGGET)),
    ADAMANTIUM_SWORD(BlockTags.AIR, 204800, 4.0F, 0.0F, 18, () -> Ingredient.of(MaterialItems.ADAMANTIUM_NUGGET)),

    COPPER_HOE(BlockTags.AIR, 3200, 1.0F, 0.0F, 15, () -> Ingredient.of(MaterialItems.COPPER_NUGGET)),
    SILVER_HOE(BlockTags.AIR, 3200, 1.0F, 0.0F, 15, () -> Ingredient.of(MaterialItems.SILVER_NUGGET)),
    GOLD_HOE(BlockTags.AIR, 3200, 2.0F, 0.0F, 25, () -> Ingredient.of(Items.GOLD_NUGGET)),
    IRON_HOE(BlockTags.AIR, 6400, 1.5F, 0.0F, 15, () -> Ingredient.of(Items.IRON_NUGGET)),
    MITHRIL_HOE(BlockTags.AIR, 51200, 2.0F, 0.0F, 25, () -> Ingredient.of(MaterialItems.MITHRIL_NUGGET)),
    ADAMANTIUM_HOE(BlockTags.AIR, 204800, 4.0F, 0.0F, 18, () -> Ingredient.of(MaterialItems.ADAMANTIUM_NUGGET));

    private final TagKey<Block> inverseTag;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;


    private ToolTiers(
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
    public int getUses() {
        return this.itemDurability;
    }

    @Override
    public float getSpeed() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.attackDamage;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.inverseTag;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return (Ingredient)this.repairIngredient.get();
    }
}
