package com.equilibrium.item.tools_attribute.metal;

import com.equilibrium.server_and_client.server.event.CraftingMetalPickAxeCallback;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MetalAxe extends ToolItem implements AdditionalAttribute{
    protected static final Map<Block, Block> STRIPPED_BLOCKS = new ImmutableMap.Builder<Block, Block>()
            .put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD)
            .put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG)
            .put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD)
            .put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG)
            .put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD)
            .put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG)
            .put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD)
            .put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG)
            .put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD)
            .put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG)
            .put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD)
            .put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG)
            .put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD)
            .put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG)
            .put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM)
            .put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE)
            .put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM)
            .put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE)
            .put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD)
            .put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG)
            .put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK)
            .build();

    public MetalAxe(ToolMaterial material, Settings settings) {
        super(material, settings.component(DataComponentTypes.TOOL, material.createComponent(BlockTags.AXE_MINEABLE)));

    }
    //ofAlwaysDropping 可以将为工具添加一个正确挖掘的"标签"
    //ToolComponent(List<ToolComponent.Rule> rules, float defaultMiningSpeed, int damagePerBlock)
    //ToolComponent的第三个参数damagePerBlock指的是每次破坏方块降低的耐久值,这里设为0,以后统一在BreakBlockEvent上进行定义具体的值
    //List.of(ToolComponent.Rule.ofAlwaysDropping(BlockTags.AXE_MINEABLE, 4F),在挖掘正确方块时,永远掉落物品,然后进行多少倍的加速?统一两倍.基础值在ToolMaterials中定义
//    private static ToolComponent createToolComponent() {
//        return new ToolComponent(
//                List.of(ToolComponent.Rule.ofAlwaysDropping(BlockTags.AXE_MINEABLE, 2F)), 1.0F, 0
//        );
//    }





    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity playerEntity = context.getPlayer();
        if (shouldCancelStripAttempt(context)) {
            return ActionResult.PASS;
        } else {
            Optional<BlockState> optional = this.tryStrip(world, blockPos, playerEntity, world.getBlockState(blockPos));
            if (optional.isEmpty()) {
                return ActionResult.PASS;
            } else {
                ItemStack itemStack = context.getStack();
                if (playerEntity instanceof ServerPlayerEntity) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
                }

                world.setBlockState(blockPos, (BlockState)optional.get(), Block.NOTIFY_ALL_AND_REDRAW);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, (BlockState)optional.get()));
                if (playerEntity != null) {
                    itemStack.damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
                }

                return ActionResult.success(world.isClient);
            }
        }
    }



    private static boolean shouldCancelStripAttempt(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        return context.getHand().equals(Hand.MAIN_HAND) && playerEntity.getOffHandStack().isOf(Items.SHIELD) && !playerEntity.shouldCancelInteraction();
    }

    private Optional<BlockState> tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state) {
        Optional<BlockState> optional = this.getStrippedState(state);
        if (optional.isPresent()) {
            world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return optional;
        } else {
            Optional<BlockState> optional2 = Oxidizable.getDecreasedOxidationState(state);
            if (optional2.isPresent()) {
                world.playSound(player, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.syncWorldEvent(player, WorldEvents.BLOCK_SCRAPED, pos, 0);
                return optional2;
            } else {
                Optional<BlockState> optional3 = Optional.ofNullable((Block)((BiMap)HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).get(state.getBlock()))
                        .map(block -> block.getStateWithProperties(state));
                if (optional3.isPresent()) {
                    world.playSound(player, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.syncWorldEvent(player, WorldEvents.WAX_REMOVED, pos, 0);
                    return optional3;
                } else {
                    return Optional.empty();
                }
            }
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        AdditionalAttribute.super.appendTooltip(stack,context,tooltip,type);
    }

    private Optional<BlockState> getStrippedState(BlockState state) {
        return Optional.ofNullable((Block)STRIPPED_BLOCKS.get(state.getBlock()))
                .map(block -> block.getDefaultState().with(PillarBlock.AXIS, (Direction.Axis)state.get(PillarBlock.AXIS)));
    }






    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }
    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(300, attacker, EquipmentSlot.MAINHAND);
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
