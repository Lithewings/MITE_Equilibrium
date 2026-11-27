package com.equilibrium.entity.arrow_attach;

import net.minecraft.world.item.ItemStack;


public class AttachedArrowData {

	private final ItemStack arrowStack;
	private final int count;

	public int getCount() {
		return count;
	}
	public ItemStack getArrowStack() {
		return arrowStack;
	}

	public AttachedArrowData(ItemStack arrowStack, int count) {
		this.arrowStack = arrowStack.copyWithCount(1);
		this.count=count;
	}

	public AttachedArrowData add() {
		return new AttachedArrowData(this.arrowStack, this.count + 1);
	}
}
