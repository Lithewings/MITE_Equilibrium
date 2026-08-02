package com.equilibrium.mixin.crafttime;


import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.block.ModBlocksRegistry2;
import com.equilibrium.item.Tools;
import com.equilibrium.item.extend_item.CoinItems;
import com.equilibrium.item.tools_attribute.metal.*;
import com.equilibrium.network.C2SClickTimesPacket;
import com.equilibrium.tags.ModItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
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
import net.minecraft.world.level.block.CraftingTableBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.*;
import static com.equilibrium.util.SharedConstant.INVALID_CRAFTING_TEXT;


@Mixin(CraftingMenu.class)
public abstract class CraftingScreenHandlerMixin extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
	@Final
	@Shadow
	private ContainerLevelAccess access;
	@Final
	@Shadow
    private ResultContainer resultSlots;
	@Final
	@Shadow
	private CraftingContainer craftSlots;
	@Final
	@Shadow
	private  Player player;



	@Unique
	private static final Map<Block, Integer> TABLE_LEVELS = Map.of(
			ModBlocksRegistry2.FLINT_CRAFTING_TABLE, 1,
			ModBlocksRegistry2.COPPER_CRAFTING_TABLE, 2,
			ModBlocksRegistry2.SILVER_CRAFTING_TABLE, 2,
			ModBlocksRegistry2.IRON_CRAFTING_TABLE, 3,
			ModBlocksRegistry2.DIAMOND_CRAFTING_TABLE, 4,
			ModBlocksRegistry2.NETHERITE_CRAFTING_TABLE, 5
	);



	public CraftingScreenHandlerMixin(MenuType<?> screenHandlerType, int i) {
		super(screenHandlerType, i);
	}


	@Inject(at = @At("HEAD"), method = "stillValid", cancellable = true)
	public void canUse(Player player, CallbackInfoReturnable<Boolean> info) {
		//自定义合成台一定可以被打开
		access.execute((world, blockPos) -> {
			if (world.getBlockState(blockPos).getBlock() instanceof CraftingTableBlock) {
				info.setReturnValue(true);
			}
		});
		info.cancel();
	}


	@Shadow
	private boolean placingRecipe;

	@Inject(method = "slotsChanged",at = @At(value = "HEAD"), cancellable = true)
	public void onContentChanged(Container inventory, CallbackInfo ci) {
		ci.cancel();


		if (!this.placingRecipe) {
			this.access.execute((world, pos) -> {


				Block currentBlock = world.getBlockState(pos).getBlock();
				//确定合成台的合成等级
				int craftTableLevel = TABLE_LEVELS.getOrDefault(currentBlock, 0);



				//确定9个输入物品的合成等级

				List<Integer> list = new ArrayList<>();

				for (int i = 0; i < 10; i++) {
					int craftLevel = 0;
					ItemStack itemStack = this.craftSlots.getItem(i);
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
				slotChangedCraftingGrid(this, world, this.player, this.craftSlots, this.resultSlots, (RecipeHolder) null);



				//是否在合成工作台
				if(this.resultSlots.getItem(0).is(ModItemTags.CRAFT_TABLE))
					maxCraftLevel--;
				//等级是否合法?如果游戏规则不检查合成等级,则等级永远合法


				boolean isLevelValid = (!world.getGameRules().getBoolean(ENABLE_CRAFTING_TIME_AND_LEVEL)) || maxCraftLevel<=craftTableLevel;





				if(!isLevelValid){
					List<Component> list1 = List.of(INVALID_CRAFTING_TEXT);

					ItemLore loreComponent = new ItemLore(list1);
					ItemStack itemStack = this.resultSlots.getItem(0);
					itemStack.set(DataComponents.LORE,loreComponent);
				}

			});
		}
	}


	@Shadow
	protected static void slotChangedCraftingGrid(
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

			resultInventory.setItem(0, itemStack);
			handler.setRemoteSlot(0, itemStack);
			serverPlayerEntity.connection.send(new ClientboundContainerSetSlotPacket(handler.containerId, handler.incrementStateId(), 0, itemStack));
		}
	}


	@Shadow public abstract boolean recipeMatches(RecipeHolder<CraftingRecipe> recipe);




	@Unique
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

//
//	@Override
//	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
//		super.onSlotClick(slotIndex, button, actionType, player);
//	}

	@Inject(method = "slotChangedCraftingGrid",at = @At(value = "HEAD"),cancellable = true)
	private static void updateResults(
			AbstractContainerMenu handler, Level world, Player player, CraftingContainer craftingInventory, ResultContainer resultInventory, @Nullable RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci
	) {
		ci.cancel();



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



			//合成台需要玩家至少拥有200级经验才能合成,用以合成自动合成器
			if(itemStack.is(Items.CRAFTING_TABLE) && player.experienceLevel<50 && !player.isCreative()){
				itemStack = ItemStack.EMPTY;
				player.sendSystemMessage(Component.nullToEmpty("你需要至少50级经验来合成该工作台"));
			}

			if(itemStack.is(Items.ANVIL) && getGameBooleanRuleFromServer(ENABLE_ANVIL_LEVEL,world.getServer())){
				itemStack = ModBlocksRegistry.IRON_ANVIL.asItem().getDefaultInstance();
			}

			if(itemStack.is(Items.ENCHANTING_TABLE) && getGameBooleanRuleFromServer(ENABLE_ADVANCED_ENCHANTING_TABLE,world.getServer())){
				itemStack = ModBlocksRegistry.DIAMOND_ENCHANTING_TABLE.asItem().getDefaultInstance();
			}

			//斧子中,替换铁,金
			if(itemStack.is(ModItemTags.AXES)){
				if(itemStack.is(Items.IRON_AXE))
					itemStack = Tools.IRON_AXE.getDefaultInstance();

				if(itemStack.is(Items.GOLDEN_AXE))
					itemStack = Tools.GOLD_AXE.getDefaultInstance();

				int clickTimes = C2SClickTimesPacket.getClickTimes(player);
				if(!itemStack.is(Tools.FLINT_AXE)) {
					MetalAxe metalAxe = (MetalAxe) itemStack.getItem();
					rightClickLogicForAdditionalAttribute(metalAxe.maxPlayerDurabilityBoost(metalAxe.getTier(), player), clickTimes, itemStack);
				}



			}



			if(itemStack.is(ModItemTags.DAGGERS)){
				int clickTimes = C2SClickTimesPacket.getClickTimes(player);
				if(!itemStack.is(Tools.FLINT_KNIFE)) {
					MetalDagger metalDagger = (MetalDagger) itemStack.getItem();
					rightClickLogicForAdditionalAttribute(metalDagger.maxPlayerDurabilityBoost(metalDagger.getTier(), player), clickTimes, itemStack);
				}
			}





			if(itemStack.is(ModItemTags.HOES)){
				if(itemStack.is(Items.IRON_HOE))
					itemStack = Tools.IRON_HOE.getDefaultInstance();

				if(itemStack.is(Items.GOLDEN_HOE))
					itemStack = Tools.GOLD_HOE.getDefaultInstance();

				int clickTimes = C2SClickTimesPacket.getClickTimes(player);
				MetalHoe metalHoe = (MetalHoe)itemStack.getItem();
				rightClickLogicForAdditionalAttribute(metalHoe.maxPlayerDurabilityBoost(metalHoe.getTier(), player), clickTimes, itemStack);






			}

			if(itemStack.is(ModItemTags.SHOVELS)){
				if(!itemStack.is(Tools.FLINT_SHOVEL) && !itemStack.is(Items.WOODEN_SHOVEL)) {
					if (itemStack.is(Items.IRON_SHOVEL))
						itemStack = Tools.IRON_SHOVEL.getDefaultInstance();

					if (itemStack.is(Items.GOLDEN_SHOVEL))
						itemStack = Tools.GOLD_SHOVEL.getDefaultInstance();
					int clickTimes = C2SClickTimesPacket.getClickTimes(player);
					MetalShovel metalShovel = (MetalShovel) itemStack.getItem();
					rightClickLogicForAdditionalAttribute(metalShovel.maxPlayerDurabilityBoost(metalShovel.getTier(), player), clickTimes, itemStack);

				}


			}

			if(itemStack.is(ModItemTags.SWORDS)) {
					if (itemStack.is(Items.IRON_SWORD))
						itemStack = Tools.IRON_SWORD.getDefaultInstance();

					if (itemStack.is(Items.GOLDEN_SWORD))
						itemStack = Tools.GOLD_SWORD.getDefaultInstance();
					int clickTimes = C2SClickTimesPacket.getClickTimes(player);
					MetalSword metalSword = (MetalSword) itemStack.getItem();
					rightClickLogicForAdditionalAttribute(metalSword.maxPlayerDurabilityBoost(metalSword.getTier(), player), clickTimes, itemStack);
			}

			if (itemStack.is(ModItemTags.PICKAXES)) {

				if (itemStack.is(Items.IRON_PICKAXE)) {
					itemStack = Tools.IRON_PICKAXE.getDefaultInstance();


				}
				if (itemStack.is(Items.GOLDEN_PICKAXE)) {
					itemStack = Tools.GOLD_PICKAXE.getDefaultInstance();

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

	@Unique
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
}
