package com.equilibrium.block.crafting_table;

import java.util.*;

import com.equilibrium.block.anvil.AnvilBlocks;
import com.equilibrium.block.enchanting_table.EnchantingTableBlocks;
import com.equilibrium.item.coin.CoinItems;
import com.equilibrium.item.material.MaterialItems;
import com.equilibrium.item.miscellaneous.MiscellaneousItems;
import com.equilibrium.item.tool.ToolItems;
import com.equilibrium.item.tool.metal.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import com.equilibrium.block.ModBlockScreenTypesRegister;
import com.equilibrium.network.C2SClickTimesPacket;
import com.equilibrium.tags.ModItemTags;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.*;
import static com.equilibrium.util.SharedConstant.INVALID_CRAFTING_TEXT;

public class ModCraftingScreenHandler extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    public static final int RESULT_ID = 0;
    private static final int INPUT_START = 1;
    private static final int INPUT_END = 10;
    private static final int INVENTORY_START = 10;
    private static final int INVENTORY_END = 37;
    private static final int HOTBAR_START = 37;
    private static final int HOTBAR_END = 46;
    public final CraftingContainer input = new TransientCraftingContainer(this, 3, 3);
    public final ResultContainer result = new ResultContainer();
    private final ContainerLevelAccess context;
    private final Player player;
    private boolean filling;

    //___________________________________________________
    private static final Map<Block, Integer> TABLE_LEVELS = Map.of(
            CraftingTableBlocks.FLINT_CRAFTING_TABLE.get(), 1,
            CraftingTableBlocks.COPPER_CRAFTING_TABLE.get(), 2,
            CraftingTableBlocks.SILVER_CRAFTING_TABLE.get(), 2,
            CraftingTableBlocks.IRON_CRAFTING_TABLE.get(), 3,
            CraftingTableBlocks.MITHRIL_CRAFTING_TABLE.get(), 4,
            CraftingTableBlocks.ADAMANTIUM_CRAFTING_TABLE.get(), 5
    );




    private static void rightClickLogicForAdditionalAttribute(double maxPlayerDurabilityBoostTime, int clickTimes, ItemStack itemStack) {
        double maxDurabilityBoost = Math.min((int)maxPlayerDurabilityBoostTime, 4);

        int function = (int) (clickTimes % (maxDurabilityBoost + 1));

        //7200经验,可供强化3次
        //右键0次,输出0%(3+1)=0等级
        //右键1次,输出1%(3+1)=1等级
        //右键2次,输出2%(3+1)=2等级
        //右键3次,输出3%(3+1)=3等级
        //右键4次,输出4%(3+1)=0等级

        CompoundTag nbt = new CompoundTag();
        nbt.putInt("DurabilityLevel", function);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        int maxDamage = (int) (itemStack.getMaxDamage() * (1 + 0.5f * function));
        itemStack.set(DataComponents.MAX_DAMAGE, maxDamage);
    }
    //根据混合的颜色,来判断是何种药水,然后施加自定义属性
    private static ItemStack createPotion(int color) {
        MobEffectInstance NIGHT_VISION = new MobEffectInstance(MobEffects.NIGHT_VISION, 2400);
        MobEffectInstance MINING_FATIGUE = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2400);

        //添加药水
        //1、冷萃夜视药水
        ItemStack coldBrewNightVisionPotion = new ItemStack(Items.POTION, 1);
        coldBrewNightVisionPotion.set(DataComponents.POTION_CONTENTS, new PotionContents(
                Optional.empty(), Optional.of(3145968), List.of(NIGHT_VISION, MINING_FATIGUE))
        );
        coldBrewNightVisionPotion.set(DataComponents.ITEM_NAME, Component.translatable("item.effect.miteequilibrium.sub_night_vision"));


        //根据混合的颜色,来判断最初是何种药水
        Map<Integer, ItemStack> potionMap = Map.of(
                -7954370, coldBrewNightVisionPotion
        );
        return potionMap.getOrDefault(color, ItemStack.EMPTY);
    }
    //___________________________________________________



    public ModCraftingScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }
    public ModCraftingScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(ModBlockScreenTypesRegister.MOD_CRAFTING_SCREEN_HANDLER_SCREEN_HANDLER_TYPE, syncId);
        this.context = context;
        this.player = playerInventory.player;
        this.addSlot(new ResultSlot(playerInventory.player, this.input, this.result, 0, 124, 35));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new Slot(this.input, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @ModifiedFromVanilla("ModCraftingScreenHandler")
    public static void updateResult(
            AbstractContainerMenu handler,
            Level world,
            Player player,
            CraftingContainer craftingInventory,
            ResultContainer resultInventory,
            @Nullable RecipeHolder<CraftingRecipe> recipe
    ) {
        if (!world.isClientSide) {
            CraftingInput craftingRecipeInput = craftingInventory.asCraftInput();
            ServerPlayer serverPlayerEntity = (ServerPlayer)player;
            ItemStack itemStack = ItemStack.EMPTY;
            Optional<RecipeHolder<CraftingRecipe>> optional = world.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingRecipeInput, world, recipe);
            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> recipeEntry = (RecipeHolder<CraftingRecipe>)optional.get();
                CraftingRecipe craftingRecipe = recipeEntry.value();
                if (resultInventory.setRecipeUsed(world, serverPlayerEntity, recipeEntry)) {
                    ItemStack itemStack2 = craftingRecipe.assemble(craftingRecipeInput, world.registryAccess());
                    if (itemStack2.isItemEnabled(world.enabledFeatures())) {
                        itemStack = itemStack2;
                    }
                }
            }




            //合成表过滤器,按照物品频率排序




            //先删除要移除的物品,直接返回
            if(itemStack.is(ModItemTags.REMOVEITEM)) {
                itemStack = ItemStack.EMPTY;

            }

            if(itemStack.is(Items.POTION)){
                itemStack = createPotion(itemStack.getComponents().get(DataComponents.POTION_CONTENTS).getColor());

            };


            //金苹果至少需要200xp才能合成
//			if(itemStack.isOf(Items.GOLDEN_APPLE) && player.totalExperience<200 && !player.isCreative())
//				itemStack = ItemStack.EMPTY;


            //铜硬币至少需要足额经验才能合成
            if(itemStack.is(CoinItems.COPPER_COIN) && player.totalExperience<CoinItems.COPPER_COIN_EXPERIENCE_COST && !player.isCreative())
                itemStack = ItemStack.EMPTY;

            //铁硬币至少需要足额经验才能合成
            if(itemStack.is(CoinItems.IRON_COIN) && player.totalExperience<CoinItems.IRON_COIN_EXPERIENCE_COST && !player.isCreative())
                itemStack = ItemStack.EMPTY;



            if(itemStack.is(Items.ANVIL) && getGameBooleanRuleFromServer(ENABLE_ANVIL_LEVEL,world.getServer())){
                itemStack = AnvilBlocks.IRON_ANVIL.asItem().getDefaultInstance();
            }

            if(itemStack.is(Items.ENCHANTING_TABLE) && getGameBooleanRuleFromServer(ENABLE_ADVANCED_ENCHANTING_TABLE,world.getServer())){
                itemStack = EnchantingTableBlocks.DIAMOND_ENCHANTING_TABLE.asItem().getDefaultInstance();
            }

            //斧子中,替换铁,金
            if(itemStack.is(ModItemTags.AXES)){
                if(itemStack.is(Items.IRON_AXE))
                    itemStack = ToolItems.IRON_AXE.get().getDefaultInstance();

                if(itemStack.is(Items.GOLDEN_AXE))
                    itemStack = ToolItems.GOLD_AXE.get().getDefaultInstance();

                int clickTimes = C2SClickTimesPacket.getClickTimes(player);
                if(!itemStack.is(ToolItems.FLINT_AXE.get())) {
                    MetalAxe metalAxe = (MetalAxe) itemStack.getItem();
                    rightClickLogicForAdditionalAttribute(metalAxe.maxPlayerDurabilityBoost(metalAxe.getTier(), player), clickTimes, itemStack);
                }



            }



            if(itemStack.is(ModItemTags.DAGGERS)){
                int clickTimes = C2SClickTimesPacket.getClickTimes(player);
                if(!itemStack.is(ToolItems.FLINT_KNIFE.get())) {
                    MetalDagger metalDagger = (MetalDagger) itemStack.getItem();
                    rightClickLogicForAdditionalAttribute(metalDagger.maxPlayerDurabilityBoost(metalDagger.getTier(), player), clickTimes, itemStack);
                }
            }





            if(itemStack.is(ModItemTags.HOES)){
                if(itemStack.is(Items.IRON_HOE))
                    itemStack = ToolItems.IRON_HOE.get().getDefaultInstance();

                if(itemStack.is(Items.GOLDEN_HOE))
                    itemStack = ToolItems.GOLD_HOE.get().getDefaultInstance();

                int clickTimes = C2SClickTimesPacket.getClickTimes(player);
                MetalHoe metalHoe = (MetalHoe)itemStack.getItem();
                rightClickLogicForAdditionalAttribute(metalHoe.maxPlayerDurabilityBoost(metalHoe.getTier(), player), clickTimes, itemStack);






            }

            if(itemStack.is(ModItemTags.SHOVELS)){
                if(!itemStack.is(ToolItems.FLINT_SHOVEL.get()) && !itemStack.is(Items.WOODEN_SHOVEL)) {
                    if (itemStack.is(Items.IRON_SHOVEL))
                        itemStack = ToolItems.IRON_SHOVEL.get().getDefaultInstance();

                    if (itemStack.is(Items.GOLDEN_SHOVEL))
                        itemStack = ToolItems.GOLD_SHOVEL.get().getDefaultInstance();
                    int clickTimes = C2SClickTimesPacket.getClickTimes(player);
                    MetalShovel metalShovel = (MetalShovel) itemStack.getItem();
                    rightClickLogicForAdditionalAttribute(metalShovel.maxPlayerDurabilityBoost(metalShovel.getTier(), player), clickTimes, itemStack);

                }


            }

            if(itemStack.is(ModItemTags.SWORDS)) {
                if (itemStack.is(Items.IRON_SWORD))
                    itemStack = ToolItems.IRON_SWORD.get().getDefaultInstance();

                if (itemStack.is(Items.GOLDEN_SWORD))
                    itemStack = ToolItems.GOLD_SWORD.get().getDefaultInstance();
                int clickTimes = C2SClickTimesPacket.getClickTimes(player);
                MetalSword metalSword = (MetalSword) itemStack.getItem();
                rightClickLogicForAdditionalAttribute(metalSword.maxPlayerDurabilityBoost(metalSword.getTier(), player), clickTimes, itemStack);
            }

            if (itemStack.is(ModItemTags.PICKAXES)) {

                if (itemStack.is(Items.IRON_PICKAXE)) {
                    itemStack = ToolItems.IRON_PICKAXE.get().getDefaultInstance();


                }
                if (itemStack.is(Items.GOLDEN_PICKAXE)) {
                    itemStack = ToolItems.GOLD_PICKAXE.get().getDefaultInstance();

                }
                int clickTimes = C2SClickTimesPacket.getClickTimes(player);
                MetalPickAxe metalPickAxe = (MetalPickAxe) itemStack.getItem();
                rightClickLogicForAdditionalAttribute(metalPickAxe.maxPlayerDurabilityBoost(metalPickAxe.getTier(), player), clickTimes, itemStack);

            }

            if (itemStack.is(ModItemTags.HAMMERS)) {


                int clickTimes = C2SClickTimesPacket.getClickTimes(player);
                MetalHammer metalHammer = (MetalHammer) itemStack.getItem();
                rightClickLogicForAdditionalAttribute(metalHammer.maxPlayerDurabilityBoost(metalHammer.getTier(), player), clickTimes, itemStack);

            }

            resultInventory.setItem(0, itemStack);
            handler.setRemoteSlot(0, itemStack);
            serverPlayerEntity.connection.send(new ClientboundContainerSetSlotPacket(handler.containerId, handler.incrementStateId(), 0, itemStack));
        }
    }

    @ModifiedFromVanilla("ModCraftingScreenHandler")
    @Override
    public void slotsChanged(Container inventory) {
        if (!this.filling) {
            this.context.execute((world, pos) -> {


                Block currentBlock = world.getBlockState(pos).getBlock();
                //确定合成台的合成等级
                int craftTableLevel = TABLE_LEVELS.getOrDefault(currentBlock, 0);



                //确定9个输入物品的合成等级

                List<Integer> list = new ArrayList<>();

                for (int i = 0; i < 10; i++) {
                    int craftLevel = 0;
                    ItemStack itemStack = this.input.getItem(i);
                    if (itemStack.is(ModItemTags.CRAFT_LEVEL1))
                        craftLevel = 1;
                    else if (itemStack.is(ModItemTags.CRAFT_LEVEL2))
                        craftLevel = 2;
                    else if (itemStack.is(ModItemTags.CRAFT_LEVEL3))
                        craftLevel = 3;
                    else if (itemStack.is(ModItemTags.CRAFT_LEVEL4))
                        craftLevel = 4;
                    else if (itemStack.is(ModItemTags.CRAFT_LEVEL5))
                        craftLevel = 5;
                    else
                        craftLevel = 0;
                    list.add(craftLevel);
                }
                int maxCraftLevel =  Collections.max(list);



                //无条件输出物品
                updateResult(this, world, this.player, this.input, this.result, (RecipeHolder) null);



                //是否在合成工作台
                if(this.result.getItem(0).is(ModItemTags.CRAFT_TABLE)&&!(this.input.hasAnyOf(Set.of(MaterialItems.MITHRIL_INGOT.asItem()))))
                    maxCraftLevel--;
                //等级是否合法?如果游戏规则不检查合成等级,则等级永远合法


                boolean isLevelValid = (!world.getGameRules().getBoolean(ENABLE_CRAFTING_TIME_AND_LEVEL)) || maxCraftLevel<=craftTableLevel;





                if(!isLevelValid){
                    List<Component> list1 = List.of(INVALID_CRAFTING_TEXT);

                    ItemLore loreComponent = new ItemLore(list1);
                    ItemStack itemStack = this.result.getItem(0);
                    itemStack.set(DataComponents.LORE,loreComponent);
                }

            });
        }
    }

    @Override
    public void beginPlacingRecipe() {
        this.filling = true;
    }

    @Override
    public void finishPlacingRecipe(RecipeHolder<CraftingRecipe> recipe) {
        this.filling = false;
        this.context.execute((world, pos) -> updateResult(this, world, this.player, this.input, this.result, recipe));
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents finder) {
        this.input.fillStackedContents(finder);
    }

    @Override
    public void clearCraftingContent() {
        this.input.clearContent();
        this.result.clearContent();
    }

    @Override
    public boolean recipeMatches(RecipeHolder<CraftingRecipe> recipe) {
        return recipe.value().matches(this.input.asCraftInput(), this.player.level());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.context.execute((world, pos) -> this.clearContainer(player, this.input));
    }


    @Override
    @ModifiedFromVanilla("ModCraftingScreenHandler")
    public boolean stillValid(Player player) {
        //自定义合成台一定可以被打开
        return canUse(this.context, player);
    }

    protected static boolean canUse(ContainerLevelAccess context, Player player) {
        return context.evaluate((world, pos) -> player.canInteractWithBlock(pos, 4.0), true);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slot == 0) {
                this.context.execute((world, pos) -> itemStack2.getItem().onCraftedBy(itemStack2, world, player));
                if (!this.moveItemStackTo(itemStack2, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot2.onQuickCraft(itemStack2, itemStack);
            } else if (slot >= 10 && slot < 46) {
                if (!this.moveItemStackTo(itemStack2, 1, 10, false)) {
                    if (slot < 37) {
                        if (!this.moveItemStackTo(itemStack2, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemStack2, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemStack2, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setByPlayer(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTake(player, itemStack2);
            if (slot == 0) {
                player.drop(itemStack2, false);
            }
        }

        return itemStack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.result && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return this.input.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.input.getHeight();
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int index) {
        return index != this.getResultSlotIndex();
    }
}
