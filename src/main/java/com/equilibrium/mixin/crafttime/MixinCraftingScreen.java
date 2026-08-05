package com.equilibrium.mixin.crafttime;

import com.equilibrium.block.CraftingDifficultyHelper;
import com.equilibrium.block.ITimeCraftPlayer;
import com.equilibrium.network.C2SClickTimesPacket;
import com.equilibrium.network.C2STriggerContentChangePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CraftingMenu;
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


import static com.equilibrium.GlobalModConfig.isAutoCraftingEnabled;
import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromClient;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_CRAFTING_TIME_AND_LEVEL;
import static com.equilibrium.network.C2STriggerContentChangePacket.sendTrigger;
import static com.equilibrium.util.SharedConstant.INVALID_CRAFTING_TEXT;

@Mixin(CraftingScreen.class)
public abstract class MixinCraftingScreen extends AbstractContainerScreen<CraftingMenu> {

    @Shadow
    protected abstract void slotClicked(Slot slot, int slotId, int button, ClickType actionType);

    @Unique
    private ITimeCraftPlayer player;

    public MixinCraftingScreen(CraftingMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Unique
    private static final ResourceLocation CRAFT_OVERLAY_TEXTURE = ResourceLocation.parse("miteequilibrium:textures/gui/crafting_table.png");


    @Override
    //在合成台界面,对按下的键盘指令做出反应
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((keyCode == GLFW.GLFW_KEY_E || (keyCode == GLFW.GLFW_KEY_ESCAPE)) && this.shouldCloseOnEsc()) {
            //一旦中途退出,就失去所有进度渲染
            player.craftTime$setCraftTime(0);
            C2SClickTimesPacket.sendClickTimes(0);
            this.onClose();
            return true;
        } else {
            return false;
        }
    }


    @Inject(method = "renderBg", at = @At("TAIL"))
    protected void timecraft$drawBackground(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo ci) {


        //每秒渲染20次
        assert this.minecraft != null;
        this.player = (ITimeCraftPlayer) this.minecraft.player;

        RenderSystem.setShaderTexture(0, CRAFT_OVERLAY_TEXTURE);
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;


        if (player.craftTime$isCrafting() && player.craftTime$getCraftPeriod() > 0) {
            int l = (int) ((player.craftTime$getCraftTime() * 24.0F / player.craftTime$getCraftPeriod()));
            if (l >= 24) {
                context.blit(CRAFT_OVERLAY_TEXTURE, i + 89, j + 35, 0, 0, 25, 16, 24, 17);
            } else {
                context.blit(CRAFT_OVERLAY_TEXTURE, i + 89, j + 35, 0, 0, l + 1, 16, 24, 17);
            }
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



        //输入输出不为空时,才考虑试图合成
        if (!this.menu.craftSlots.isEmpty() && !this.menu.getSlot(0).getItem().isEmpty()) {
            //获得合成难度
            player.craftTime$setCraftPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.menu, true, this));
            //进行一次craftTick,若合成结束返回true
            if (this.player.craftTime$craftTickIsFinished()) {
                //模拟无限制时秒出合成物品的一次操作
                super.slotClicked(this.menu.getSlot(0), 0, 0, ClickType.THROW);
                //在ScreenHandlerMixin中自动将鼠标stack下的物品放入玩家物品栏中
                if(!isAutoCraftingEnabled()){
                    player.craftTime$stopCraft();
                }
            }
            //刷新一次合成结果栏
            if (this.menu.getSlot(0).getItem().isEmpty()) {
                sendTrigger();
            }
        } else player.craftTime$stopCraft();


    }

    @Unique
    int time = 0;


    @Shadow
    @Final
    private RecipeBookComponent recipeBookComponent;


    @Shadow
    public abstract void render(GuiGraphics context, int mouseX, int mouseY, float delta);

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    public void timecraft$onMouseClick(Slot slot, int invSlot, int clickData, ClickType actionType,
                                       CallbackInfo ci) {


        if (slot != null) {
            invSlot = slot.index;
        } else {
            //slot = null时,会触发invSlot=-999index越界错,说明鼠标点击的位置没有slot可用,这里需要额外处理,因为涉及发包
            return;
        }

        if (invSlot == 0 && clickData == 1) {
            //右键只会改变配方,不会合成
            time++;

            //服务端处理,真实逻辑处理

            C2SClickTimesPacket.sendClickTimes(time);
            C2STriggerContentChangePacket.sendTrigger();
            player.craftTime$setCraftTime(0);
            player.craftTime$setCrafting(false);
            ci.cancel();
        }
        if (invSlot > 0 && invSlot < 10) {
            player.craftTime$setCraftTime(0);
            player.craftTime$setCrafting(false);
        }
        if (invSlot == 0 && clickData == 0) {
            ItemStack resultItemStack = this.menu.getSlot(0).getItem();
            if (resultItemStack.get(DataComponents.LORE) != null) {
                for (Component text : resultItemStack.get(DataComponents.LORE).lines()) {
                    if (text.contains(INVALID_CRAFTING_TEXT)) {
                        ci.cancel();
                        return;
                    }
                }
            }

            //没有进行合成且输入输出不会空时,才考虑合成
            if (!player.craftTime$isCrafting() && !this.menu.craftSlots.isEmpty() && !this.menu.getSlot(0).getItem().isEmpty()) {
                player.craftTime$startCraftWithNewPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.menu, false, this));
            }
            //阻止直接从输出栏拿物品
            if(getGameBooleanRuleFromClient(ENABLE_CRAFTING_TIME_AND_LEVEL))
                ci.cancel();
        }
        this.recipeBookComponent.slotClicked(slot);
    }
}
