package com.equilibrium.item.tools_attribute.metal;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;
import java.util.Map;

public interface AdditionalAttribute {

    default void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        int durabilityLevel = getDurabilityLevel(stack);

        Map<Integer, String> qualityKeys = Map.of(
                1, "miteequilibrium.tooltip.quality.excellent",
                2, "miteequilibrium.tooltip.quality.refined",
                3, "miteequilibrium.tooltip.quality.epic",
                4, "miteequilibrium.tooltip.quality.master"
        );
        String qualityKey = qualityKeys.getOrDefault(durabilityLevel, "miteequilibrium.tooltip.quality.average");

        tooltip.add(Component.translatable(qualityKey).withStyle(ChatFormatting.DARK_GRAY));

        if (durabilityLevel != 0) {
            int bonusPercent = durabilityLevel * 50;
            tooltip.add(Component.translatable("miteequilibrium.tooltip.durability_bonus", bonusPercent)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    // 从 CUSTOM_DATA 获取耐久等级
    default int getDurabilityLevel(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("DurabilityLevel")) {
            return customData.getUnsafe().getInt("DurabilityLevel");
        }
        return 0;
    }

    // 设置耐久等级到 CUSTOM_DATA
    default void setDurabilityLevel(ItemStack stack, int level) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, nbt -> {
            nbt.putInt("DurabilityLevel", level);
        });
    }

    // 可选：在创建新工具时初始化耐久等级
    abstract ItemStack getDefaultInstance();



    static double getTotalExperience(Player player) {
        double level = player.experienceLevel;
        double progress = player.experienceProgress;

        // 从0级到当前等级所需的累计经验: 5 × level × (level + 1)
        double cumulativeExperience = 5 * level * (level + 1);

        // 当前等级内已获得的经验: progress × 10 × (level + 1)
        double currentLevelExperience = (int)(progress * 10 * (level + 1));

        // 总经验 = 累计等级经验 + 当前等级内经验
        return cumulativeExperience + currentLevelExperience;
    }


    default boolean canCraftByPlayer(Tier toolMaterial, Player player, int durabilityLevel){
        return getTotalExperience(player) > this.xpCost(toolMaterial , durabilityLevel);
    }



    default int xpCost(Tier toolMaterial, int durabilityLevel){
        int xp = (int) (toolMaterial.getUses()*0.1*(durabilityLevel));
        return xp;
    }


    default double maxPlayerDurabilityBoost(Tier toolMaterial, Player player){
        double i = getTotalExperience(player);
        double j=  xpCost(toolMaterial,1);
        return i/j;
    }

//when using :
//    @Override
//    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
//        AdditionalAttribute.super.appendTooltip(stack,context,tooltip,type);
//    }
//
//    // 从 CUSTOM_DATA 获取耐久等级
//    @Override
//    public int getDurabilityLevel(ItemStack stack) {
//        return AdditionalAttribute.super.getDurabilityLevel(stack);
//    }
//    @Override
//    // 设置耐久等级到 CUSTOM_DATA
//    public void setDurabilityLevel(ItemStack stack, int level) {
//        AdditionalAttribute.super.setDurabilityLevel(stack, level);
//    }
//
//    // 可选：在创建新工具时初始化耐久等级
//    @Override
//    public ItemStack getDefaultStack() {
//        ItemStack stack = super.getDefaultStack();
//        // 为新创建的工具设置默认耐久等级
//        if (getDurabilityLevel(stack) == 0) {
//            setDurabilityLevel(stack, 0);
//        }
//        return stack;
//    }
//
//    @Override
//    public boolean canCraftByPlayer(ToolMaterial toolMaterial,PlayerEntity player,int durabilityLevel){
//        return AdditionalAttribute.super.canCraftByPlayer(toolMaterial, player,durabilityLevel);
//    }
//
//    @Override
//    public int xpCost(ToolMaterial toolMaterial, int durabilityLevel) {
//        return AdditionalAttribute.super.xpCost(toolMaterial, durabilityLevel);
//    }
//    @Override
//    public int maxPlayerDurabilityBoost(ToolMaterial toolMaterial,PlayerEntity player){
//        return AdditionalAttribute.super.maxPlayerDurabilityBoost(toolMaterial,player);
//    }
}
