package com.equilibrium.util;

import com.equilibrium.block.ModBlocksRegistry;
import com.equilibrium.item.Metal;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class CraftingDifficultyHelper {

	public static float getCraftingDifficultyFromMatrix(AbstractRecipeScreenHandler<CraftingRecipeInput, CraftingRecipe> handler, boolean is_craft_table, Screen screen) {
		ArrayList<Slot> slots = new ArrayList<Slot>();
		int index = is_craft_table? 10 : 5;
		for (int i = 1; i < index; i++) {
			slots.add(handler.getSlot(i));
		}

		Text text = screen.getTitle();
		float p = 1.0f;
		if(text.equals(Text.translatable("container.copper_crafting"))){
			p -= 0.3f;//铜工作台减30%
		}
		if(text.equals(Text.translatable("container.iron_crafting"))){
			p -= 0.5f;//铁工作台减50%
		}
		if(text.equals(Text.translatable("container.diamond_crafting"))){
			p -= 0.7f;//钻石工作台减70%
		}
		if(text.equals(Text.translatable("container.netherite_crafting"))){
			p -= 0.9f;//下界合金工作台减90%
		}

		return Math.max(getCraftingDifficultyFromMatrix(slots) * p, 15F);
	}


	public static float getCraftingDifficultyFromMatrix(ArrayList<Slot> slots) {
		float item_difficulty = 5F;
		for (Slot s : slots) {
			Item item = s.getStack().getItem();
			if (item == Items.AIR)
				continue;
			item_difficulty += getDifficulty(item);
		}
		int totalDifficulty1 = (int) item_difficulty;
		int totalDifficulty2 = (int) item_difficulty;

		totalDifficulty1 = (int) Math.log(totalDifficulty1);
		totalDifficulty2 = (int) Math.pow(totalDifficulty2,0.64);


		return totalDifficulty1*totalDifficulty2;
	}
	
	public static ArrayList<Item> getItemFromMatrix(AbstractRecipeScreenHandler<CraftingRecipeInput, CraftingRecipe> handler, boolean is_craft_table) {
		ArrayList<Item> items = new ArrayList<Item>();
		int index = is_craft_table ? 10 : 5;
		for (int i = 1; i < index; i++) {
			items.add(handler.getSlot(i).getStack().getItem());
		}
		return items;
	}

	public static float getDifficulty(Item item) {
		String name =  Registries.ITEM.getId(item).toString();

		//金胡萝卜合成冷萃夜视药水时使用
		if(item==Items.GOLDEN_CARROT){
			return 3200f*9;
		}
		if(item==Items.IRON_BLOCK){
			return 3200f*9;
		}
		if(item==Items.IRON_INGOT){
			return 3200F;
		}
		if(item==Items.IRON_NUGGET){
			return (3200f/9f);
		}

		if(item==Items.GOLD_INGOT){
			return 1600F;
		}
		if(item==Items.GOLD_BLOCK){
			return 1600F*9;
		}
		if(item==Items.GOLD_NUGGET){
			return 1600F/9f;
		}


		if(item== Metal.mithril){
			return 25600F;
		}
		if(item== Metal.mithril_nugget){
			return 25600F/9F;
		}
		if(item== ModBlocksRegistry.MITHRIL_BLOCK.asItem()){
			return 25600F*9F;
		}


		if(item== Metal.silver){
			return 1600F;
		}
		if(item== Metal.silver_nugget){
			return 1600F/9F;
		}
		if(item== ModBlocksRegistry.SILVER_BLOCK.asItem()){
			return 1600F*9F;
		}
		if(item== Metal.copper){
			return 1600F;
		}
		if(item== Metal.copper_nugget){
			return 1600F/9F;
		}
		if(item== ModBlocksRegistry.COPPER_BLOCK.asItem()){
			return 1600F*9F;
		}


		if(item==Items.DIAMOND){
			return 25600F;
		}
		if(item==Items.NETHERITE_INGOT){
			return 25600*4F;
		}


		if(item==Metal.adamantium){
			return 25600*4F;
		}
		if(item==Metal.adamantium_nugget){
			return 25600*4F/9f;
		}
		if(item== ModBlocksRegistry.ADAMANTIUM_BLOCK.asItem()){
			return 25600*4F*9F;
		}



//
//		if(CommonConfig.craftItemTimeMap.containsKey(name)){
//			return CommonConfig.craftItemTimeMap.get(name);
//		}

		return 20F;
	}
}
