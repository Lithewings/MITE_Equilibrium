package com.equilibrium.mixin.crafttime;


import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.block.ModBlocksRegistry2;
import com.equilibrium.item.Tools;
import com.equilibrium.item.extend_item.CoinItems;
import com.equilibrium.item.tools_attribute.metal.*;
import com.equilibrium.network.C2SClickTimesPacket;
import com.equilibrium.tags.ModItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.*;
import static com.equilibrium.util.SharedConstant.*;


@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingRecipeInput, CraftingRecipe> {
	@Final
	@Shadow
	private ScreenHandlerContext context;
	@Final
	@Shadow
    private CraftingResultInventory result;
	@Final
	@Shadow
	private RecipeInputInventory input;
	@Final
	@Shadow
	private  PlayerEntity player;



	@Unique
	private static final Map<Block, Integer> TABLE_LEVELS = Map.of(
			ModBlocksRegistry2.FLINT_CRAFTING_TABLE, 1,
			ModBlocksRegistry2.COPPER_CRAFTING_TABLE, 2,
			ModBlocksRegistry2.SILVER_CRAFTING_TABLE, 2,
			ModBlocksRegistry2.IRON_CRAFTING_TABLE, 3,
			ModBlocksRegistry2.DIAMOND_CRAFTING_TABLE, 4,
			ModBlocksRegistry2.NETHERITE_CRAFTING_TABLE, 5
	);



	public CraftingScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
		super(screenHandlerType, i);
	}


	@Inject(at = @At("HEAD"), method = "canUse", cancellable = true)
	public void canUse(PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
		//自定义合成台一定可以被打开
		context.run((world, blockPos) -> {
			if (world.getBlockState(blockPos).getBlock() instanceof CraftingTableBlock) {
				info.setReturnValue(true);
			}
		});
		info.cancel();
	}


	@Shadow
	private boolean filling;

	@Inject(method = "onContentChanged",at = @At(value = "HEAD"), cancellable = true)
	public void onContentChanged(Inventory inventory, CallbackInfo ci) {
		ci.cancel();


		if (!this.filling) {
			this.context.run((world, pos) -> {


				Block currentBlock = world.getBlockState(pos).getBlock();
				//确定合成台的合成等级
				int craftTableLevel = TABLE_LEVELS.getOrDefault(currentBlock, 0);



				//确定9个输入物品的合成等级

				List<Integer> list = new ArrayList<>();

				for (int i = 0; i < 10; i++) {
					int craftLevel = 0;
					ItemStack itemStack = this.input.getStack(i);
					if (itemStack.isIn(ModItemTags.CRAFT_LEVEL1))
						craftLevel = 1;
					else if (itemStack.isIn(ModItemTags.CRAFT_LEVEL2))
						craftLevel = 2;
					else if (itemStack.isIn(ModItemTags.CRAFT_LEVEL3))
						craftLevel = 3;
					else if (itemStack.isIn(ModItemTags.CRAFT_LEVEL4))
						craftLevel = 4;
					else if (itemStack.isIn(ModItemTags.CRAFT_LEVEL5))
						craftLevel = 5;
					else
						craftLevel = 0;
					list.add(craftLevel);
				}
				int maxCraftLevel =  Collections.max(list);



				//无条件输出物品
				updateResult(this, world, this.player, this.input, this.result, (RecipeEntry) null);



				//是否在合成工作台
				if(this.result.getStack(0).isIn(ModItemTags.CRAFT_TABLE))
					maxCraftLevel--;
				//等级是否合法?如果游戏规则不检查合成等级,则等级永远合法


				boolean isLevelValid = (!world.getGameRules().getBoolean(ENABLE_CRAFTING_TIME_AND_LEVEL)) || maxCraftLevel<=craftTableLevel;





				if(!isLevelValid){
					List<Text> list1 = List.of(INVALID_CRAFTING_TEXT);

					LoreComponent loreComponent = new LoreComponent(list1);
					ItemStack itemStack = this.result.getStack(0);
					itemStack.set(DataComponentTypes.LORE,loreComponent);
				}

			});
		}
	}


	@Shadow
	protected static void updateResult(
			ScreenHandler handler,
			World world,
			PlayerEntity player,
			RecipeInputInventory craftingInventory,
			CraftingResultInventory resultInventory,
			@Nullable RecipeEntry<CraftingRecipe> recipe
	) {
		if (!world.isClient) {
			CraftingRecipeInput craftingRecipeInput = craftingInventory.createRecipeInput();
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
			ItemStack itemStack = ItemStack.EMPTY;
			Optional<RecipeEntry<CraftingRecipe>> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingRecipeInput, world, recipe);
			if (optional.isPresent()) {
				RecipeEntry<CraftingRecipe> recipeEntry = (RecipeEntry<CraftingRecipe>)optional.get();
				CraftingRecipe craftingRecipe = recipeEntry.value();
				if (resultInventory.shouldCraftRecipe(world, serverPlayerEntity, recipeEntry)) {
					ItemStack itemStack2 = craftingRecipe.craft(craftingRecipeInput, world.getRegistryManager());
					if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
						itemStack = itemStack2;
					}
				}
			}

			resultInventory.setStack(0, itemStack);
			handler.setPreviousTrackedSlot(0, itemStack);
			serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack));
		}
	}


	@Shadow public abstract boolean matches(RecipeEntry<CraftingRecipe> recipe);




	@Unique
	//根据混合的颜色,来判断是何种药水,然后施加自定义属性
	private static ItemStack createPotion(int color) {

		StatusEffectInstance NIGHT_VISION = new StatusEffectInstance(StatusEffects.NIGHT_VISION, 2400);
		StatusEffectInstance MINING_FATIGUE = new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 2400);

		//添加药水
		//1、冷萃夜视药水
		ItemStack coldBrewNightVisionPotion = new ItemStack(Items.POTION, 1);
		coldBrewNightVisionPotion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(
				Optional.empty(), Optional.of(3145968), List.of(NIGHT_VISION, MINING_FATIGUE))
		);
		coldBrewNightVisionPotion.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.effect.miteequilibrium.sub_night_vision"));


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

	@Inject(method = "updateResult",at = @At(value = "HEAD"),cancellable = true)
	private static void updateResults(
			ScreenHandler handler, World world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory, @Nullable RecipeEntry<CraftingRecipe> recipe, CallbackInfo ci
	) {
		ci.cancel();



		if (!world.isClient) {
			CraftingRecipeInput craftingRecipeInput = craftingInventory.createRecipeInput();
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
			ItemStack itemStack = ItemStack.EMPTY;
			Optional<RecipeEntry<CraftingRecipe>> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingRecipeInput, world, recipe);
			if (optional.isPresent()) {
				RecipeEntry<CraftingRecipe> recipeEntry = (RecipeEntry<CraftingRecipe>)optional.get();
				CraftingRecipe craftingRecipe = recipeEntry.value();
				if (resultInventory.shouldCraftRecipe(world, serverPlayerEntity, recipeEntry)) {
					ItemStack itemStack2 = craftingRecipe.craft(craftingRecipeInput, world.getRegistryManager());
					if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
						itemStack = itemStack2;
					}
				}
			}




			//合成表过滤器,按照物品频率排序




			//先删除要移除的物品,直接返回
			if(itemStack.isIn(ModItemTags.REMOVEITEM)) {
				itemStack = ItemStack.EMPTY;

			}

			if(itemStack.isOf(Items.POTION)){
				itemStack = createPotion(itemStack.getComponents().get(DataComponentTypes.POTION_CONTENTS).getColor());

			};


			//金苹果至少需要200xp才能合成
//			if(itemStack.isOf(Items.GOLDEN_APPLE) && player.totalExperience<200 && !player.isCreative())
//				itemStack = ItemStack.EMPTY;


			//铜硬币至少需要足额经验才能合成
			if(itemStack.isOf(CoinItems.COPPER_COIN) && player.totalExperience<CoinItems.COPPER_COIN_EXPERIENCE_COST && !player.isCreative())
				itemStack = ItemStack.EMPTY;

			//铁硬币至少需要足额经验才能合成
			if(itemStack.isOf(CoinItems.IRON_COIN) && player.totalExperience<CoinItems.IRON_COIN_EXPERIENCE_COST && !player.isCreative())
				itemStack = ItemStack.EMPTY;



			//合成台需要玩家至少拥有200级经验才能合成,用以合成自动合成器
			if(itemStack.isOf(Items.CRAFTING_TABLE) && player.experienceLevel<50 && !player.isCreative()){
				itemStack = ItemStack.EMPTY;
				player.sendMessage(Text.of("你需要至少50级经验来合成该工作台"));
			}

			if(itemStack.isOf(Items.ANVIL) && getGameBooleanRuleFromServer(ENABLE_ANVIL_LEVEL,world.getServer())){
				itemStack = ModBlocksRegistry.IRON_ANVIL.asItem().getDefaultStack();
			}

			if(itemStack.isOf(Items.ENCHANTING_TABLE) && getGameBooleanRuleFromServer(ENABLE_ADVANCED_ENCHANTING_TABLE,world.getServer())){
				itemStack = ModBlocksRegistry.DIAMOND_ENCHANTING_TABLE.asItem().getDefaultStack();
			}

			//斧子中,替换铁,金
			if(itemStack.isIn(ModItemTags.AXES)){
				if(itemStack.isOf(Items.IRON_AXE))
					itemStack = Tools.IRON_AXE.getDefaultStack();

				if(itemStack.isOf(Items.GOLDEN_AXE))
					itemStack = Tools.GOLD_AXE.getDefaultStack();

				int clickTimes = C2SClickTimesPacket.getClickTimes(player);
				if(!itemStack.isOf(Tools.FLINT_AXE)) {
					MetalAxe metalAxe = (MetalAxe) itemStack.getItem();
					rightClickLogicForAdditionalAttribute(metalAxe.maxPlayerDurabilityBoost(metalAxe.material, player), clickTimes, itemStack);
				}



			}



			if(itemStack.isIn(ModItemTags.DAGGERS)){
				int clickTimes = C2SClickTimesPacket.getClickTimes(player);
				if(!itemStack.isOf(Tools.FLINT_KNIFE)) {
					MetalDagger metalDagger = (MetalDagger) itemStack.getItem();
					rightClickLogicForAdditionalAttribute(metalDagger.maxPlayerDurabilityBoost(metalDagger.material, player), clickTimes, itemStack);
				}
			}





			if(itemStack.isIn(ModItemTags.HOES)){
				if(itemStack.isOf(Items.IRON_HOE))
					itemStack = Tools.IRON_HOE.getDefaultStack();

				if(itemStack.isOf(Items.GOLDEN_HOE))
					itemStack = Tools.GOLD_HOE.getDefaultStack();

				int clickTimes = C2SClickTimesPacket.getClickTimes(player);
				MetalHoe metalHoe = (MetalHoe)itemStack.getItem();
				rightClickLogicForAdditionalAttribute(metalHoe.maxPlayerDurabilityBoost(metalHoe.material, player), clickTimes, itemStack);






			}

			if(itemStack.isIn(ModItemTags.SHOVELS)){
				if(!itemStack.isOf(Tools.FLINT_SHOVEL) && !itemStack.isOf(Items.WOODEN_SHOVEL)) {
					if (itemStack.isOf(Items.IRON_SHOVEL))
						itemStack = Tools.IRON_SHOVEL.getDefaultStack();

					if (itemStack.isOf(Items.GOLDEN_SHOVEL))
						itemStack = Tools.GOLD_SHOVEL.getDefaultStack();
					int clickTimes = C2SClickTimesPacket.getClickTimes(player);
					MetalShovel metalShovel = (MetalShovel) itemStack.getItem();
					rightClickLogicForAdditionalAttribute(metalShovel.maxPlayerDurabilityBoost(metalShovel.material, player), clickTimes, itemStack);

				}


			}

			if(itemStack.isIn(ModItemTags.SWORDS)) {
					if (itemStack.isOf(Items.IRON_SWORD))
						itemStack = Tools.IRON_SWORD.getDefaultStack();

					if (itemStack.isOf(Items.GOLDEN_SWORD))
						itemStack = Tools.GOLD_SWORD.getDefaultStack();
					int clickTimes = C2SClickTimesPacket.getClickTimes(player);
					MetalSword metalSword = (MetalSword) itemStack.getItem();
					rightClickLogicForAdditionalAttribute(metalSword.maxPlayerDurabilityBoost(metalSword.material, player), clickTimes, itemStack);
			}

			if (itemStack.isIn(ModItemTags.PICKAXES)) {

				if (itemStack.isOf(Items.IRON_PICKAXE)) {
					itemStack = Tools.IRON_PICKAXE.getDefaultStack();


				}
				if (itemStack.isOf(Items.GOLDEN_PICKAXE)) {
					itemStack = Tools.GOLD_PICKAXE.getDefaultStack();

				}
				int clickTimes = C2SClickTimesPacket.getClickTimes(player);
				MetalPickAxe metalPickAxe = (MetalPickAxe) itemStack.getItem();
				rightClickLogicForAdditionalAttribute(metalPickAxe.maxPlayerDurabilityBoost(metalPickAxe.material, player), clickTimes, itemStack);

			}

			if (itemStack.isIn(ModItemTags.HAMMERS)) {


				int clickTimes = C2SClickTimesPacket.getClickTimes(player);
				MetalHammer metalHammer = (MetalHammer) itemStack.getItem();
				rightClickLogicForAdditionalAttribute(metalHammer.maxPlayerDurabilityBoost(metalHammer.material, player), clickTimes, itemStack);

			}

			resultInventory.setStack(0, itemStack);
			handler.setPreviousTrackedSlot(0, itemStack);
			serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack));
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

		NbtCompound nbt = new NbtCompound();
		nbt.putInt("DurabilityLevel", function);
		itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
		int maxDamage = (int) (itemStack.getMaxDamage() * (1 + 0.5f * function));
		itemStack.set(DataComponentTypes.MAX_DAMAGE, maxDamage);
	}
}
