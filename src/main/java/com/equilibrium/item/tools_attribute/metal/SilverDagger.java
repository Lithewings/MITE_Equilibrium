package com.equilibrium.item.tools_attribute.metal;

import com.equilibrium.entity.mob.GhoulEntity;
import com.equilibrium.event.CraftingMetalPickAxeCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.registry.tag.EntityTypeTags.UNDEAD;

public class SilverDagger extends MetalDagger {
    public SilverDagger(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack,context,tooltip,type);
        tooltip.add(Text.translatable("item.miteequilibrium.silver.tooltip1").formatted(Formatting.AQUA));
        tooltip.add(Text.translatable("item.miteequilibrium.silver.tooltip2").formatted(Formatting.AQUA));
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postDamageEntity(stack, target, attacker);

        if(target.isDead() && (target.getType().isIn(EntityTypeTags.UNDEAD)))
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,50,1));
    }






}
