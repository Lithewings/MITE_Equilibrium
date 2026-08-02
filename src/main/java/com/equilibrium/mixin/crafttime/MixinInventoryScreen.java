package com.equilibrium.mixin.crafttime;

import com.equilibrium.block.CraftingDifficultyHelper;
import com.equilibrium.block.ITimeCraftPlayer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromClient;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_CRAFTING_TIME_AND_LEVEL;
import static com.equilibrium.network.C2STriggerContentChangePacket.sendTrigger;
import static com.equilibrium.util.SharedConstant.INVALID_CRAFTING_TEXT;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> {

	@Unique
	private ITimeCraftPlayer player;

	public MixinInventoryScreen(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
		super(screenHandler, playerInventory, text);
	}

	@Unique
	private static final ResourceLocation CRAFT_OVERLAY_TEXTURE = ResourceLocation.parse("miteequilibrium:textures/gui/inventory.png");

	@Inject(method = "keyPressed", at = @At("HEAD"))
	//在合成台界面,对按下的键盘指令做出反应
	public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (keyCode == GLFW.GLFW_KEY_E && this.shouldCloseOnEsc() ) {
			//一旦中途退出,就失去所有进度渲染
//			OnServerInitialize.LOGGER.info("end crafting");
			player.craftTime$setCraftTime(0);
			this.onClose();
		}
	}


	@Inject(method = "renderBg", at = @At("TAIL"))
	protected void timecraft$drawBackground(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		this.player = (ITimeCraftPlayer) this.minecraft.player;

		RenderSystem.setShaderTexture(0,CRAFT_OVERLAY_TEXTURE);
		int i = this.leftPos;
		int j = this.topPos;

		//可能存在溢出渲染问题, 用Math.min(l + 1,18)限制,其中的18是宽度
		if (player.craftTime$isCrafting() && player.craftTime$getCraftPeriod() > 0) {
			int l = (int) ((player.craftTime$getCraftTime() * 17.0F / player.craftTime$getCraftPeriod()));
			context.blit(CRAFT_OVERLAY_TEXTURE, i + 134, j + 29, 0, 0, Math.min(l + 1,18), 14, 18, 15);
		}
	}

	@Inject(method = "containerTick", at = @At("TAIL"))
	public void timecraft$tick(CallbackInfo info) {
        if (this.minecraft != null) {
            this.player = (ITimeCraftPlayer) this.minecraft.player;
        }
		ItemStack resultItemStack = this.menu.getSlot(0).getItem();
		if (resultItemStack.get(DataComponents.LORE) != null) {
			for (Component text : resultItemStack.get(DataComponents.LORE).lines()) {
				if (text.contains(INVALID_CRAFTING_TEXT)) {
					player.craftTime$stopCraft();
					return;
				}
			}
		}


		//自动合成:输入输出不为空时,才考虑试图合成
        if(isAutoCraftingEnabled() && !this.menu.getCraftSlots().isEmpty() && !this.menu.getSlot(0).getItem().isEmpty()){
			//获得合成难度
			player.craftTime$setCraftPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.menu, false, this));
			//进行一次craftTick,若合成结束返回true
			if(this.player.craftTime$craftTickIsFinished()){
				//模拟无限制时秒出合成物品的一次操作
				super.slotClicked(this.menu.getSlot(0), 0, 0, ClickType.THROW);
				//在ScreenHandlerMixin中自动将鼠标stack下的物品放入玩家物品栏中
				if(!isAutoCraftingEnabled()){
					player.craftTime$stopCraft();
				}
			}
			//刷新一次合成结果栏
			if(this.menu.getSlot(0).getItem().isEmpty()){
				sendTrigger();
			}
		}
		else player.craftTime$stopCraft();

	}
	//temp
	private boolean isAutoCraftingEnabled() {
		return true;
	}

	@Shadow
	@Final
	private RecipeBookComponent recipeBookComponent = new RecipeBookComponent();








	@Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
	public void timecraft$onMouseClick(Slot slot, int invSlot, int clickData, ClickType actionType,
			CallbackInfo info) {
		if (slot != null) {
			invSlot = slot.index;
		}
		if (invSlot == 0) {
			ItemStack resultItemStack = this.menu.getSlot(0).getItem();
			if (resultItemStack.get(DataComponents.LORE) != null) {
				for (Component text : resultItemStack.get(DataComponents.LORE).lines()) {
					if (text.contains(INVALID_CRAFTING_TEXT)) {
						info.cancel();
						return;
					}
				}
			}
			//没有进行合成且输入输出不会空时,才考虑合成
			if (!player.craftTime$isCrafting()  && !this.menu.getCraftSlots().isEmpty() && !this.menu.getSlot(0).getItem().isEmpty() ) {
				player.craftTime$startCraftWithNewPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.menu, false,this));
			}
			//阻止直接从输出栏拿物品
			if(getGameBooleanRuleFromClient(ENABLE_CRAFTING_TIME_AND_LEVEL))
				info.cancel();
		}
		this.recipeBookComponent.slotClicked(slot);
	}
}
