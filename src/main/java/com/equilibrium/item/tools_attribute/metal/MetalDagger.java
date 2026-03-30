package com.equilibrium.item.tools_attribute.metal;

import com.equilibrium.server_and_client.server.event.CraftingMetalPickAxeCallback;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.equilibrium.item.tools_attribute.ExtraDamageFromExperienceLevel.getDamageLevel;

public class MetalDagger extends ToolItem implements AdditionalAttribute{
    public MetalDagger(ToolMaterial toolMaterial, Item.Settings settings) {
        super(toolMaterial, settings.component(DataComponentTypes.TOOL,toolMaterial.createComponent(BlockTags.SWORD_EFFICIENT)));
    }


    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        AdditionalAttribute.super.appendTooltip(stack,context,tooltip,type);
        tooltip.add(Text.translatable("item.miteequilibrium.dagger_tooltip").formatted(Formatting.GRAY));
        //see:PlayerEntityMixin.attackStart(Entity target)
    }



    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }



    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        stack.damage(50, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        return super.finishUsing(stack, world, user);
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
    public double maxPlayerDurabilityBoost(ToolMaterial toolMaterial, PlayerEntity player){
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
