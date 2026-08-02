package com.equilibrium.item.tools_attribute.metal;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SilverSword extends MetalSword implements AdditionalAttribute{
    public SilverSword(Tier toolMaterial, Properties settings) {
        super(toolMaterial, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack,context,tooltip,type);
        tooltip.add(Component.translatable("item.miteequilibrium.silver.tooltip1").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.miteequilibrium.silver.tooltip2").withStyle(ChatFormatting.AQUA));

    }
    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        if(target.isDeadOrDying() && (target.getType().is(EntityTypeTags.UNDEAD)))
            attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION,100,1));
    }
    // 从 CUSTOM_DATA 获取耐久等级

}
