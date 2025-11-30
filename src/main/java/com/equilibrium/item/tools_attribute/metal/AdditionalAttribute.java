package com.equilibrium.item.tools_attribute.metal;

import com.equilibrium.event.CraftingMetalPickAxeCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public interface AdditionalAttribute {

    default void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        int durabilityLevel = getDurabilityLevel(stack);
        Map<Integer,String> map = Map.of(1,"品质：优秀",2,"品质：精良",3,"品质：史诗",4,"品质：大师");
        tooltip.add(Text.literal(map.getOrDefault(durabilityLevel,"品质：普通")).formatted(Formatting.DARK_GRAY));
        if(durabilityLevel!=0)
            tooltip.add(Text.literal(("+"+durabilityLevel*50+"% 耐久")).formatted(Formatting.DARK_GRAY));
    }

    // 从 CUSTOM_DATA 获取耐久等级
    default int getDurabilityLevel(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null && customData.contains("DurabilityLevel")) {
            return customData.getNbt().getInt("DurabilityLevel");
        }
        return 0;
    }

    // 设置耐久等级到 CUSTOM_DATA
    default void setDurabilityLevel(ItemStack stack, int level) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            nbt.putInt("DurabilityLevel", level);
        });
    }

    // 可选：在创建新工具时初始化耐久等级
    abstract ItemStack getDefaultStack();



    static int getTotalExperience(PlayerEntity player) {
        int level = player.experienceLevel;
        float progress = player.experienceProgress;

        // 从0级到当前等级所需的累计经验: 5 × level × (level + 1)
        int cumulativeExperience = 5 * level * (level + 1);

        // 当前等级内已获得的经验: progress × 10 × (level + 1)
        int currentLevelExperience = (int)(progress * 10 * (level + 1));

        // 总经验 = 累计等级经验 + 当前等级内经验
        return 10 + cumulativeExperience + currentLevelExperience;
    }


    default boolean canCraftByPlayer(ToolMaterial toolMaterial, PlayerEntity player, int durabilityLevel){
        return getTotalExperience(player) > this.xpCost(toolMaterial , durabilityLevel);
    }



    default int xpCost(ToolMaterial toolMaterial, int durabilityLevel){
        int xp = (int) (toolMaterial.getDurability()*0.25*(durabilityLevel));
        return xp;
    }


    default int maxPlayerDurabilityBoost(ToolMaterial toolMaterial, PlayerEntity player){
        int i =getTotalExperience(player);
        int j =  xpCost(toolMaterial,1);
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
