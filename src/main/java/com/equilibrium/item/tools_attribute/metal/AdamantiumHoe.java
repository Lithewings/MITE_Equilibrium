package com.equilibrium.item.tools_attribute.metal;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class AdamantiumHoe extends MetalHoe{
    public AdamantiumHoe(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }
    private int count = 100;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient()){
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0, false, false, false));
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        else
            return TypedActionResult.pass(user.getStackInHand(hand));

    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack,context,tooltip,type);
        tooltip.add(Text.translatable("item.miteequilibrium.adamantium_hoe.tooltip1").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.miteequilibrium.adamantium_hoe.tooltip2").formatted(Formatting.AQUA));

    }








}
