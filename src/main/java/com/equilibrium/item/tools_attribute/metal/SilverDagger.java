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
        tooltip.add(Text.literal("屠宰: 对消极生物造成1.5倍伤害").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("对亡灵生物造成1.5倍伤害").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("对亡灵生物给予最后一击后,获得2.5秒的再生II").formatted(Formatting.AQUA));
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postDamageEntity(stack, target, attacker);

        if(target.isDead() && (target.getType().isIn(EntityTypeTags.UNDEAD)))
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,50,1));
    }






}
