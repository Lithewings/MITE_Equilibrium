package com.equilibrium.item.tools_attribute.metal;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SilverDagger extends MetalDagger {
    public SilverDagger(Tier toolMaterial, Properties settings) {
        super(toolMaterial, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack,context,tooltip,type);
        tooltip.add(Component.translatable("item.miteequilibrium.silver.tooltip3").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.miteequilibrium.silver.tooltip2").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);

        if(target.isDeadOrDying() && (target.getType().is(EntityTypeTags.UNDEAD)))
            attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION,50,1));
    }






}
