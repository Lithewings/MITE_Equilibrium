package com.equilibrium.mixin.crafttime;

import com.equilibrium.ITimeCraftPlayer;
import com.equilibrium.OnServerInitialize;
import com.equilibrium.util.CraftingDifficultyHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> {

	@Unique
	private ITimeCraftPlayer player;

	public MixinInventoryScreen(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Unique
	private static final Identifier CRAFT_OVERLAY_TEXTURE = Identifier.of("miteequilibrium:textures/gui/inventory.png");

	@Inject(method = "keyPressed", at = @At("HEAD"))
	//在合成台界面,对按下的键盘指令做出反应
	public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (keyCode != GLFW.GLFW_KEY_LEFT_SHIFT && this.shouldCloseOnEsc()) {
			//一旦中途退出,就失去所有进度渲染
			OnServerInitialize.LOGGER.info("end crafting");
			player.craftTime$setCraftTime(0);
			this.close();
		}
	}


	@Inject(method = "drawBackground", at = @At("TAIL"))
	protected void timecraft$drawBackground(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		this.player = (ITimeCraftPlayer) this.client.player;

		RenderSystem.setShaderTexture(0,CRAFT_OVERLAY_TEXTURE);
		int i = this.x;
		int j = this.y;
		if (player.craftTime$isCrafting() && player.craftTime$getCraftPeriod() > 0) {
			int l = (int) ((player.craftTime$getCraftTime() * 17.0F / player.craftTime$getCraftPeriod()));
			context.drawTexture(CRAFT_OVERLAY_TEXTURE, i + 134, j + 29, 0, 0, l + 1, 14, 18, 15);
		}
	}

	@Inject(method = "handledScreenTick", at = @At("TAIL"))
	public void timecraft$tick(CallbackInfo info) {
        if (this.client != null) {
            this.player = (ITimeCraftPlayer) this.client.player;
        }

        ItemStack resultStack = this.handler.getSlot(0).getStack();

		boolean finished = player.craftTime$tick(resultStack);
		//可以在这里定义金苹果合成消耗经验的逻辑

		//problem:when mouse cursor has stack,this is not stop
		if (finished) {
			ArrayList<Item> old_recipe = CraftingDifficultyHelper.getItemFromMatrix(this.handler, false);

			super.onMouseClick(this.handler.getSlot(0), 0, 0, SlotActionType.THROW);

			ArrayList<Item> new_recipe = CraftingDifficultyHelper.getItemFromMatrix(this.handler, false);

			if (old_recipe.equals(new_recipe) ) {
				player.craftTime$setCraftPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.handler, false, this));
			}else {
				player.craftTime$stopCraft();
			}
		}
	}

	@Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
	public void timecraft$onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType,
			CallbackInfo info) {
		if (slot != null) {
			invSlot = slot.id;
		}
		if (invSlot > 0 && invSlot < 5) {
			player.craftTime$stopCraft();
		}
		if (invSlot == 0) {
			if (!player.craftTime$isCrafting()) {
				player.craftTime$startCraftWithNewPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.handler, false,this));
			}
			info.cancel();
		}
	}
}
