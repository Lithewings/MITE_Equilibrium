package com.equilibrium.item.tools_attribute.metal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class SilverSword extends MetalSword implements AdditionalAttribute{
    public SilverSword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack,context,tooltip,type);
        tooltip.add(Text.translatable("item.miteequilibrium.silver.tooltip1").formatted(Formatting.AQUA));
        tooltip.add(Text.translatable("item.miteequilibrium.silver.tooltip2").formatted(Formatting.AQUA));

    }
    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postDamageEntity(stack, target, attacker);
        if(target.isDead() && (target.getType().isIn(EntityTypeTags.UNDEAD)))
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,100,1));
    }
    // 从 CUSTOM_DATA 获取耐久等级

}
