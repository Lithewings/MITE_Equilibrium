package com.equilibrium.mixin.crafttime;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.util.SharedConstant.INVALID_CRAFTING_TEXT;

@Mixin(InventoryScreen.class)
//不再涉及合成耗时操作
public abstract class InventoryCraftingLevelLimit extends AbstractInventoryScreen<PlayerScreenHandler> {

	public InventoryCraftingLevelLimit(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Shadow
	private final RecipeBookWidget recipeBook = new RecipeBookWidget();

	@Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
	public void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType,
			CallbackInfo info) {
		if (slot != null) {
			invSlot = slot.id;
		}
		if (invSlot == 0) {
			ItemStack resultItemStack = this.handler.getSlot(0).getStack();
			if (resultItemStack.get(DataComponentTypes.LORE) != null) {
				for (Text text : resultItemStack.get(DataComponentTypes.LORE).lines()) {
					if (text.contains(INVALID_CRAFTING_TEXT)) {
						info.cancel();
						return;
					}
				}
			}
		}
		this.recipeBook.slotClicked(slot);
	}
}
