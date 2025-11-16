package com.equilibrium.item.tools_attribute.metal;

import com.equilibrium.MITEequilibrium;
import com.equilibrium.event.CraftingMetalPickAxeCallback;
import com.equilibrium.item.Metal;
import com.equilibrium.item.ModArmorMaterials;
import com.equilibrium.item.tools_attribute.ModToolMaterials;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;


public class MetalPickAxe extends ToolItem {


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

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
        if (toolComponent == null) {
            return false;
        } else {
            if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
                stack.damage(stack.isSuitableFor(state)? 0 : 480 , miner, EquipmentSlot.MAINHAND);
            }

            return true;
        }
    }
    private static ToolComponent createToolComponent() {
        return new ToolComponent(
                List.of(ToolComponent.Rule.ofAlwaysDropping(BlockTags.PICKAXE_MINEABLE, 4F)), 1.0F, 0
        );
    }


    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        int durabilityLevel = getDurabilityLevel(stack);
        Map<Integer,String> map = Map.of(1,"耐久 I",2,"耐久 II",3,"耐久 III",4,"耐久 IV");
        tooltip.add(Text.literal(map.getOrDefault(durabilityLevel,"品质：普通")).formatted(Formatting.DARK_GRAY));
    }

    // 从 CUSTOM_DATA 获取耐久等级
    public int getDurabilityLevel(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null && customData.contains("DurabilityLevel")) {
            return customData.getNbt().getInt("DurabilityLevel");
        }
        return 0;
    }

    // 设置耐久等级到 CUSTOM_DATA
    public void setDurabilityLevel(ItemStack stack, int level) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            nbt.putInt("DurabilityLevel", level);
        });
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
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
//        attacker.sendMessage(Text.of("击中实体的工具损伤"));
        stack.damage(300, attacker, EquipmentSlot.MAINHAND);
    }

    public static int getTotalExperience(PlayerEntity player) {
        int level = player.experienceLevel;
        float progress = player.experienceProgress;

        // 从0级到当前等级所需的累计经验: 5 × level × (level + 1)
        int cumulativeExperience = 5 * level * (level + 1);

        // 当前等级内已获得的经验: progress × 10 × (level + 1)
        int currentLevelExperience = (int)(progress * 10 * (level + 1));

        // 总经验 = 累计等级经验 + 当前等级内经验
        return 10 + cumulativeExperience + currentLevelExperience;
    }


    public boolean canCraftByPlayer(PlayerEntity player,int durabilityLevel){
        return getTotalExperience(player) > this.xpCost(durabilityLevel);
    }


    public int xpCost(int durabilityLevel){
        int xp = (int) (this.getMaterial().getDurability()*0.25*(durabilityLevel));
        return xp;
    }

    public int maxPlayerDurabilityBoost(PlayerEntity player){
        int i =getTotalExperience(player);
        int j =  xpCost(1);
        return i/j;
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        ActionResult result = CraftingMetalPickAxeCallback.EVENT.invoker().interact(world,player);
        if(!player.getWorld().isClient())
            player.addExperience(-xpCost(getDurabilityLevel(stack)));
//        if(getDurabilityLevel()==1)
//        player.sendMessage(Text.of("This DurabilityLevel is :"+getDurabilityLevel(stack)));

        if(result == ActionResult.FAIL) {
            return;
        }

    }

}
