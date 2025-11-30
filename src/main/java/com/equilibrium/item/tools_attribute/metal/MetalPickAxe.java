package com.equilibrium.item.tools_attribute.metal;

import com.equilibrium.event.CraftingMetalPickAxeCallback;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;


public class MetalPickAxe extends ToolItem implements AdditionalAttribute {


    public MetalPickAxe(ToolMaterial material, Settings settings) {
        super(material,settings.component(DataComponentTypes.TOOL, createToolComponent()));

    }

//    @Override
//    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
//        if(stack.getItem().getName().getString().contains("copper")||ingredient.isOf(Metal.copper_nugget))
//            return true;
//        else
//            return false;
//    }


    private static ToolComponent createToolComponent() {
        return new ToolComponent(
                List.of(ToolComponent.Rule.ofAlwaysDropping(BlockTags.PICKAXE_MINEABLE, 1F)), 1.0F, 0
        );
    }


    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
//        attacker.sendMessage(Text.of("击中实体的工具损伤"));
        stack.damage(300, attacker, EquipmentSlot.MAINHAND);
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        AdditionalAttribute.super.appendTooltip(stack,context,tooltip,type);
    }

    // 从 CUSTOM_DATA 获取耐久等级
    @Override
    public int getDurabilityLevel(ItemStack stack) {
        return AdditionalAttribute.super.getDurabilityLevel(stack);
    }
    @Override
    // 设置耐久等级到 CUSTOM_DATA
    public void setDurabilityLevel(ItemStack stack, int level) {
        AdditionalAttribute.super.setDurabilityLevel(stack, level);
    }

    // 可选：在创建新工具时初始化耐久等级
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        // 为新创建的工具设置默认耐久等级
        if (getDurabilityLevel(stack) == 0) {
            setDurabilityLevel(stack, 0);
        }
        return stack;
    }

    @Override
    public boolean canCraftByPlayer(ToolMaterial toolMaterial,PlayerEntity player,int durabilityLevel){
        return AdditionalAttribute.super.canCraftByPlayer(toolMaterial, player,durabilityLevel);
    }

    @Override
    public int xpCost(ToolMaterial toolMaterial, int durabilityLevel) {
        return AdditionalAttribute.super.xpCost(toolMaterial, durabilityLevel);
    }
    @Override
    public int maxPlayerDurabilityBoost(ToolMaterial toolMaterial,PlayerEntity player){
        return AdditionalAttribute.super.maxPlayerDurabilityBoost(toolMaterial,player);
    }


    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        ActionResult result = CraftingMetalPickAxeCallback.EVENT.invoker().interact(world,player);
        if(!player.getWorld().isClient())
            player.addExperience(-xpCost(this.material,getDurabilityLevel(stack)));
//        if(getDurabilityLevel()==1)
//        player.sendMessage(Text.of("This DurabilityLevel is :"+getDurabilityLevel(stack)));

        if(result == ActionResult.FAIL) {
            return;
        }

    }

}
