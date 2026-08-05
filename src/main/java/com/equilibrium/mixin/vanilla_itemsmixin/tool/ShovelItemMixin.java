package com.equilibrium.mixin.vanilla_itemsmixin.tool;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShovelItem.class)
public class ShovelItemMixin extends DiggerItem {
    public ShovelItemMixin(Tier material, TagKey<Block> effectiveBlocks, Properties settings) {
        super(material, effectiveBlocks, settings);
    }
    @Unique
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Unique
    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(50, attacker, EquipmentSlot.MAINHAND);


    }
}
