package com.equilibrium.item.tools_attribute.metal;

import com.equilibrium.server_and_client.server.event.CraftingMetalPickAxeCallback;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MetalAxe extends TieredItem implements AdditionalAttribute{
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

    public MetalAxe(Tier material, Properties settings) {
        super(material, settings.component(DataComponents.TOOL, material.createToolProperties(BlockTags.MINEABLE_WITH_AXE)));

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
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Player playerEntity = context.getPlayer();
        if (shouldCancelStripAttempt(context)) {
            return InteractionResult.PASS;
        } else {
            Optional<BlockState> optional = this.tryStrip(world, blockPos, playerEntity, world.getBlockState(blockPos));
            if (optional.isEmpty()) {
                return InteractionResult.PASS;
            } else {
                ItemStack itemStack = context.getItemInHand();
                if (playerEntity instanceof ServerPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)playerEntity, blockPos, itemStack);
                }

                world.setBlock(blockPos, (BlockState)optional.get(), Block.UPDATE_ALL_IMMEDIATE);
                world.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(playerEntity, (BlockState)optional.get()));
                if (playerEntity != null) {
                    itemStack.hurtAndBreak(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
                }

                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
    }



    private static boolean shouldCancelStripAttempt(UseOnContext context) {
        Player playerEntity = context.getPlayer();
        return context.getHand().equals(InteractionHand.MAIN_HAND) && playerEntity.getOffhandItem().is(Items.SHIELD) && !playerEntity.isSecondaryUseActive();
    }

    private Optional<BlockState> tryStrip(Level world, BlockPos pos, @Nullable Player player, BlockState state) {
        Optional<BlockState> optional = this.getStrippedState(state);
        if (optional.isPresent()) {
            world.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            return optional;
        } else {
            Optional<BlockState> optional2 = WeatheringCopper.getPrevious(state);
            if (optional2.isPresent()) {
                world.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.levelEvent(player, LevelEvent.PARTICLES_SCRAPE, pos, 0);
                return optional2;
            } else {
                Optional<BlockState> optional3 = Optional.ofNullable((Block)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).get(state.getBlock()))
                        .map(block -> block.withPropertiesOf(state));
                if (optional3.isPresent()) {
                    world.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                    world.levelEvent(player, LevelEvent.PARTICLES_WAX_OFF, pos, 0);
                    return optional3;
                } else {
                    return Optional.empty();
                }
            }
        }
    }
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        AdditionalAttribute.super.appendTooltip(stack,context,tooltip,type);
    }

    private Optional<BlockState> getStrippedState(BlockState state) {
        return Optional.ofNullable((Block)STRIPPED_BLOCKS.get(state.getBlock()))
                .map(block -> block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, (Direction.Axis)state.getValue(RotatedPillarBlock.AXIS)));
    }






    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }
    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(300, attacker, EquipmentSlot.MAINHAND);
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
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        // 为新创建的工具设置默认耐久等级
        if (getDurabilityLevel(stack) == 0) {
            setDurabilityLevel(stack, 0);
        }
        return stack;
    }

    @Override
    public boolean canCraftByPlayer(Tier toolMaterial,Player player,int durabilityLevel){
        return AdditionalAttribute.super.canCraftByPlayer(toolMaterial, player,durabilityLevel);
    }

    @Override
    public int xpCost(Tier toolMaterial, int durabilityLevel) {
        return AdditionalAttribute.super.xpCost(toolMaterial, durabilityLevel);
    }
    @Override
    public double maxPlayerDurabilityBoost(Tier toolMaterial, Player player){
        return AdditionalAttribute.super.maxPlayerDurabilityBoost(toolMaterial,player);
    }


    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        InteractionResult result = CraftingMetalPickAxeCallback.EVENT.invoker().interact(world,player);
        if(!player.level().isClientSide())
            player.giveExperiencePoints(-xpCost(this.getTier(),getDurabilityLevel(stack)));
//        if(getDurabilityLevel()==1)
//        player.sendMessage(Text.of("This DurabilityLevel is :"+getDurabilityLevel(stack)));

        if(result == InteractionResult.FAIL) {
            return;
        }

    }



}
