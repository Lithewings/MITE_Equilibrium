package com.equilibrium.mixin.crafttime;

import com.equilibrium.block.ITimeCraftPlayer;
import com.equilibrium.block.CraftingDifficultyHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
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
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.DifficultyEntryOnGameRules.ENABLE_CRAFTING_TIME_AND_LEVEL;
import static com.equilibrium.DifficultyEntryOnGameRules.getGameBooleanRuleFromClient;
import static com.equilibrium.GlobalModConfig.isAutoCraftingEnabled;
import static com.equilibrium.network.C2STriggerContentChangePacket.sendTrigger;
import static com.equilibrium.util.SharedConstant.INVALID_CRAFTING_TEXT;

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
		if (keyCode == GLFW.GLFW_KEY_E && this.shouldCloseOnEsc() ) {
			//一旦中途退出,就失去所有进度渲染
//			OnServerInitialize.LOGGER.info("end crafting");
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

		//可能存在溢出渲染问题, 用Math.min(l + 1,18)限制,其中的18是宽度
		if (player.craftTime$isCrafting() && player.craftTime$getCraftPeriod() > 0) {
			int l = (int) ((player.craftTime$getCraftTime() * 17.0F / player.craftTime$getCraftPeriod()));
			context.drawTexture(CRAFT_OVERLAY_TEXTURE, i + 134, j + 29, 0, 0, Math.min(l + 1,18), 14, 18, 15);
		}
	}

	@Inject(method = "handledScreenTick", at = @At("TAIL"))
	public void timecraft$tick(CallbackInfo info) {
        if (this.client != null) {
            this.player = (ITimeCraftPlayer) this.client.player;
        }
		ItemStack resultItemStack = this.handler.getSlot(0).getStack();
		if (resultItemStack.get(DataComponentTypes.LORE) != null) {
			for (Text text : resultItemStack.get(DataComponentTypes.LORE).lines()) {
				if (text.contains(INVALID_CRAFTING_TEXT)) {
					player.craftTime$stopCraft();
					return;
				}
			}
		}


		//自动合成:输入输出不为空时,才考虑试图合成
        if(isAutoCraftingEnabled() && !this.handler.getCraftingInput().isEmpty() && !this.handler.getSlot(0).getStack().isEmpty()){
			//获得合成难度
			player.craftTime$setCraftPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.handler, false, this));
			//进行一次craftTick,若合成结束返回true
			if(this.player.craftTime$craftTickIsFinished()){
				//模拟无限制时秒出合成物品的一次操作
				super.onMouseClick(this.handler.getSlot(0), 0, 0, SlotActionType.THROW);
				//在ScreenHandlerMixin中自动将鼠标stack下的物品放入玩家物品栏中
				if(!isAutoCraftingEnabled()){
					player.craftTime$stopCraft();
				}
			}
			//刷新一次合成结果栏
			if(this.handler.getSlot(0).getStack().isEmpty()){
				sendTrigger();
			}
		}
		else player.craftTime$stopCraft();

	}
	@Shadow
	private final RecipeBookWidget recipeBook = new RecipeBookWidget();








	@Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
	public void timecraft$onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType,
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
			//没有进行合成且输入输出不会空时,才考虑合成
			if (!player.craftTime$isCrafting()  && !this.handler.getCraftingInput().isEmpty() && !this.handler.getSlot(0).getStack().isEmpty() ) {
				player.craftTime$startCraftWithNewPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.handler, false,this));
			}
			//阻止直接从输出栏拿物品
			if(getGameBooleanRuleFromClient(ENABLE_CRAFTING_TIME_AND_LEVEL))
				info.cancel();
		}
		this.recipeBook.slotClicked(slot);
	}
}
