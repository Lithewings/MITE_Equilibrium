package com.equilibrium.item.tools_attribute.metal;

import com.equilibrium.server_and_client.server.event.CraftingMetalPickAxeCallback;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MetalHoe extends TieredItem implements AdditionalAttribute{
    /**
     * A map of input blocks to predicate-consumer action pairs.
     *
     * <p>Tilling works so that if the predicate succeeds, the consumer (the real action)
     * is executed, and the hoe is damaged.
     */
    protected static final Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLING_ACTIONS = Maps.<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>>newHashMap(
            ImmutableMap.of(
                    Blocks.GRASS_BLOCK,
                    Pair.of(HoeItem::onlyIfAirAbove, createTillAction(Blocks.FARMLAND.defaultBlockState())),
                    Blocks.DIRT_PATH,
                    Pair.of(HoeItem::onlyIfAirAbove, createTillAction(Blocks.FARMLAND.defaultBlockState())),
                    Blocks.DIRT,
                    Pair.of(HoeItem::onlyIfAirAbove, createTillAction(Blocks.FARMLAND.defaultBlockState())),
                    Blocks.COARSE_DIRT,
                    Pair.of(HoeItem::onlyIfAirAbove, createTillAction(Blocks.DIRT.defaultBlockState())),
                    Blocks.ROOTED_DIRT,
                    Pair.of(itemUsageContext -> true, createTillAndDropAction(Blocks.DIRT.defaultBlockState(), Items.HANGING_ROOTS))
            )
    );


    public MetalHoe(Tier toolMaterial, Item.Properties settings) {
        super(toolMaterial,settings.component(DataComponents.TOOL, toolMaterial.createToolProperties(BlockTags.MINEABLE_WITH_HOE)));
    }



    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = (Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>)TILLING_ACTIONS.get(
                world.getBlockState(blockPos).getBlock()
        );
        if (pair == null) {
            return InteractionResult.PASS;
        } else {
            Predicate<UseOnContext> predicate = pair.getFirst();
            Consumer<UseOnContext> consumer = pair.getSecond();
            if (predicate.test(context)) {
                Player playerEntity = context.getPlayer();
                world.playSound(playerEntity, blockPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!world.isClientSide) {
                    consumer.accept(context);
                    if (playerEntity != null) {
                        context.getItemInHand().hurtAndBreak(50, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
                    }
                }

                return InteractionResult.sidedSuccess(world.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    /**
     * {@return a tilling action that sets a block state}
     *
     * @param result the tilled block state
     */
    public static Consumer<UseOnContext> createTillAction(BlockState result) {
        return context -> {
            context.getLevel().setBlock(context.getClickedPos(), result, Block.UPDATE_ALL_IMMEDIATE);
            context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, context.getClickedPos(), GameEvent.Context.of(context.getPlayer(), result));
        };
    }

    /**
     * {@return a tilling action that sets a block state and drops an item}
     *
     * @param droppedItem the item to drop
     * @param result the tilled block state
     */
    public static Consumer<UseOnContext> createTillAndDropAction(BlockState result, ItemLike droppedItem) {
        return context -> {
            context.getLevel().setBlock(context.getClickedPos(), result, Block.UPDATE_ALL_IMMEDIATE);
            context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, context.getClickedPos(), GameEvent.Context.of(context.getPlayer(), result));
            Block.popResourceFromFace(context.getLevel(), context.getClickedPos(), context.getClickedFace(), new ItemStack(droppedItem));
        };
    }

    /**
     * {@return whether the used block can be tilled into farmland}
     * This method is used as the tilling predicate for most vanilla blocks except rooted dirt.
     */
    public static boolean canTillFarmland(UseOnContext context) {
        return context.getClickedFace() != Direction.DOWN && context.getLevel().getBlockState(context.getClickedPos().above()).isAir();
    }
    //集中实体是否调用物品损伤?
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        AdditionalAttribute.super.appendTooltip(stack,context,tooltip,type);
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
