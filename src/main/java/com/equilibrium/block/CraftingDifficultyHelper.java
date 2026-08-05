package com.equilibrium.block;

import com.equilibrium.block.material.MaterialBlocks;
import com.equilibrium.item.food.FoodItems;
import com.equilibrium.item.material.MaterialItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class
CraftingDifficultyHelper {

	public static float getCraftingDifficultyFromMatrix(RecipeBookMenu<CraftingInput, CraftingRecipe> handler, boolean is_craft_table, Screen screen) {
		ArrayList<Slot> slots = new ArrayList<Slot>();
		int index = is_craft_table? 10 : 5;
		for (int i = 1; i < index; i++) {
			slots.add(handler.getSlot(i));
		}

		Component text = screen.getTitle();
		float p = 1.0f;
		if(text.equals(Component.translatable("container.copper_crafting"))){
			p -= 0.3f;//铜工作台减30%
		}
		if(text.equals(Component.translatable("container.silver_crafting"))){
			p -= 0.3f;//银工作台减30%
		}
		if(text.equals(Component.translatable("container.iron_crafting"))){
			p -= 0.5f;//铁工作台减50%
		}
		if(text.equals(Component.translatable("container.diamond_crafting"))){
			p -= 0.7f;//钻石工作台减70%
		}
		if(text.equals(Component.translatable("container.netherite_crafting"))){
			p -= 0.9f;//下界合金工作台减90%
		}

		return Math.max(getCraftingDifficultyFromMatrix(slots) * p, 15F);
	}


	public static float getCraftingDifficultyFromMatrix(ArrayList<Slot> slots) {
		float item_difficulty = 5F;
		for (Slot s : slots) {
			Item item = s.getItem().getItem();
			if (item == Items.AIR)
				continue;
			//存在任意以下物品时
			if(ITEM_DIFFICULTY_CONSTANT.containsKey(item)) {
				//直接应用常量合成难度
				item_difficulty = ITEM_DIFFICULTY_CONSTANT.getOrDefault(item, 20F);
				break;
			}
			item_difficulty += getDifficulty(item);
		}

		int totalDifficulty1 = (int) item_difficulty;
		int totalDifficulty2 = (int) item_difficulty;

		totalDifficulty1 = (int) Math.log(totalDifficulty1);
		totalDifficulty2 = (int) Math.pow(totalDifficulty2,0.64);


		return totalDifficulty1*totalDifficulty2;
	}


	private static final Map<Item, Float> ITEM_DIFFICULTY_CONSTANT = new HashMap<>();

	private static final Map<Item, Float> ITEM_DIFFICULTY = new HashMap<>();

	public static void initCraftingDifficulties(){
		// 初始化哈希表
		// 金胡萝卜相关
		ITEM_DIFFICULTY.put(Items.GOLDEN_CARROT, 3200f * 9);

		// 铁相关
		ITEM_DIFFICULTY.put(Items.IRON_BLOCK, 3200f * 9);
		ITEM_DIFFICULTY.put(Items.IRON_INGOT, 3200f);
		ITEM_DIFFICULTY.put(Items.IRON_NUGGET, 3200f / 9f);

		// 金相关
		ITEM_DIFFICULTY.put(Items.GOLD_INGOT, 1600f);
		ITEM_DIFFICULTY.put(Items.GOLD_BLOCK, 1600f * 9);
		ITEM_DIFFICULTY.put(Items.GOLD_NUGGET, 1600f / 9f);

		// 秘银相关
		ITEM_DIFFICULTY.put(MaterialItems.MITHRIL_INGOT.get(), 25600f);
		ITEM_DIFFICULTY.put(MaterialItems.MITHRIL_NUGGET.get(), 25600f / 9f);
		ITEM_DIFFICULTY.put(MaterialBlocks.MITHRIL_BLOCK.asItem(),25600f * 9f);

		// 银相关
		ITEM_DIFFICULTY.put(MaterialItems.SILVER_INGOT.get(), 1600f);
		ITEM_DIFFICULTY.put(MaterialItems.SILVER_NUGGET.get(), 1600f / 9f);
		ITEM_DIFFICULTY.put(MaterialBlocks.SILVER_BLOCK.asItem(), 1600f * 9f);

		// 铜相关
		ITEM_DIFFICULTY.put(MaterialItems.COPPER_INGOT.get(), 1600f);
		ITEM_DIFFICULTY.put(MaterialItems.COPPER_NUGGET.get(), 1600f / 9f);
		ITEM_DIFFICULTY.put(MaterialBlocks.COPPER_BLOCK.asItem(), 1600f * 9f);

		// 钻石相关
		ITEM_DIFFICULTY.put(Items.DIAMOND, 25600f);
		ITEM_DIFFICULTY.put(Items.NETHERITE_INGOT, 25600f * 4f);

		// 艾德曼合金相关
		ITEM_DIFFICULTY.put(MaterialItems.ADAMANTIUM_INGOT.get(), 25600f * 4f);
		ITEM_DIFFICULTY.put(MaterialItems.ADAMANTIUM_NUGGET.get(), 25600f * 4f / 9f);
		ITEM_DIFFICULTY.put(MaterialBlocks.ADAMANTIUM_BLOCK.asItem(), 25600f * 4f * 9f);


		ITEM_DIFFICULTY_CONSTANT.put(FoodItems.MILK_BOWL.get(), 3200f);
		ITEM_DIFFICULTY_CONSTANT.put(Items.MILK_BUCKET, 3200f * 4);


	}


	public static float getDifficulty(Item item) {



		return ITEM_DIFFICULTY.getOrDefault(item,20F);



//
//		if(CommonConfig.craftItemTimeMap.containsKey(name)){
//			return CommonConfig.craftItemTimeMap.get(name);
//		}

	}
}
