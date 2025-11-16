package com.equilibrium.mixin.crafttime;

import com.equilibrium.ITimeCraftPlayer;
import com.equilibrium.item.Metal;
import com.equilibrium.item.ModItems;
import com.equilibrium.item.tools_attribute.metal.MetalPickAxe;
import com.equilibrium.network.C2SClickTimesPacket;
import com.equilibrium.network.C2STriggerContentChangePacket;
import com.equilibrium.util.CraftingDifficultyHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(CraftingScreen.class)
public abstract class MixinCraftingScreen extends HandledScreen<CraftingScreenHandler> {

	@Shadow protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

	@Unique
	private ITimeCraftPlayer player;

	public MixinCraftingScreen(CraftingScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Unique
	private static final Identifier CRAFT_OVERLAY_TEXTURE = Identifier.of("miteequilibrium:textures/gui/crafting_table.png");


	@Override
	//在合成台界面,对按下的键盘指令做出反应
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode != GLFW.GLFW_KEY_LEFT_SHIFT && this.shouldCloseOnEsc()) {
			//一旦中途退出,就失去所有进度渲染
			player.craftTime$setCraftTime(0);
			C2SClickTimesPacket.sendClickTimes(0);
			this.close();
			return true;
		}else{
			return false;
		}
	}




	@Inject(method = "drawBackground", at = @At("TAIL"))
	protected void timecraft$drawBackground(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {


		//每秒渲染20次
		assert this.client != null;
		this.player = (ITimeCraftPlayer) this.client.player;

		RenderSystem.setShaderTexture(0,CRAFT_OVERLAY_TEXTURE);
		int i = this.x;
		int j = (this.height - this.backgroundHeight) / 2;


		if (player.craftTime$isCrafting() && player.craftTime$getCraftPeriod() > 0) {
			int l = (int) ((player.craftTime$getCraftTime() * 24.0F / player.craftTime$getCraftPeriod()));
			if (l >= 24) {
				context.drawTexture(CRAFT_OVERLAY_TEXTURE, i + 89, j + 35, 0, 0, 25, 16, 24, 17);
			} else {
				context.drawTexture(CRAFT_OVERLAY_TEXTURE, i + 89, j + 35, 0, 0, l + 1, 16, 24, 17);
			}
		}




	}



	@Inject(method = "handledScreenTick", at = @At("TAIL"))
	public void timecraft$tick(CallbackInfo info) {

		assert this.client != null;
		this.player = (ITimeCraftPlayer) this.client.player;
		ItemStack resultStack = this.handler.getSlot(0).getStack();
//		if(resultStack==ItemStack.EMPTY)
//			//恢复默认值
//			this.toolDurabilityLevel =0;

		boolean finished = player.craftTime$tick(resultStack);

//		if (resultStack.getItem() instanceof MetalPickAxe metalPickAxe) {
//			nbt.putInt("DurabilityLevel", toolDurabilityLevel);
//			resultStack.set(DataComponentTypes.CUSTOM_DATA,NbtComponent.of(nbt));
//
//		}


		if (finished) {
			C2SClickTimesPacket.sendClickTimes(0);
			super.onMouseClick(this.handler.getSlot(0), 0, 0, SlotActionType.THROW);
//			this.getScreenHandler().result.setStack(0,resultStack);

			ArrayList<Item> old_recipe = CraftingDifficultyHelper.getItemFromMatrix(this.handler, true);

			//---------------------------------------------------------


			ArrayList<Item> new_recipe = CraftingDifficultyHelper.getItemFromMatrix(this.handler, true);

			if (old_recipe.equals(new_recipe) )
				player.craftTime$setCraftPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.handler, true,this));
			else {
				player.craftTime$stopCraft();

			}
		}
	}

//	@Inject(method = "mouseClicked", at = @At("TAIL"), cancellable = true)
//	public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
//		//button = 0 : leftClick
//		//button = 1 : rightClick
//		if(button==1){
//			this.durabilityLevel = 1;
//			cir.setReturnValue(false);
//		}
//	}





	int time = 0;

	@Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
	public void timecraft$onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType,
			CallbackInfo ci) {

		//slot = null时,会触发invSlot=-999index越界错,说明鼠标点击的位置没有slot可用

		if(invSlot == 0 && clickData==1){
			//右键只会改变配方,不会合成
			time++;

			//服务端处理,真实逻辑处理
			C2SClickTimesPacket.sendClickTimes(time);
			C2STriggerContentChangePacket.sendTrigger();
			player.craftTime$setCraftTime(0);
			player.craftTime$setCrafting(false);
			ci.cancel();
		}



		if (slot != null) {
			invSlot = slot.id;
		}
		if (invSlot > 0 && invSlot < 10) {
			player.craftTime$setCraftTime(0);
			player.craftTime$setCrafting(false);
		}
		if (invSlot == 0 &&  clickData==0) {
			if (!player.craftTime$isCrafting() ) {
				player.craftTime$startCraftWithNewPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.handler, true,this));


			}

			ci.cancel();
		}

	}
}
