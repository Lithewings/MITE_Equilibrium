package com.equilibrium.block.crafting_table;

public interface CraftTimeController {

	void setCraftingStatus(boolean isCrafting);

	boolean isCrafting();

	void setCraftTimeCost(float time);

	float getCraftTimeCost();

	void setCraftStage(float stage);

	float getCraftStage();
	
	void stopCraft();
	
	void startCraftWithNewStage(float stage);

	boolean isCraftTickFinished();

}
