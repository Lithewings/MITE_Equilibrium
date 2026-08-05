package com.equilibrium.item.tool.metal.adamantium;

import com.equilibrium.item.tool.metal.MetalHoe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class AdamantiumHoe extends MetalHoe {
    public AdamantiumHoe(Tier toolMaterial, Properties settings) {
        super(toolMaterial, settings);
    }
    private int count = 100;

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if(!world.isClientSide()){
            user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0, false, false, false));
            return InteractionResultHolder.success(user.getItemInHand(hand));
        }
        else
            return InteractionResultHolder.pass(user.getItemInHand(hand));

    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack,context,tooltip,type);
        tooltip.add(Component.translatable("item.miteequilibrium.adamantium_hoe.tooltip1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.miteequilibrium.adamantium_hoe.tooltip2").withStyle(ChatFormatting.AQUA));

    }








}
