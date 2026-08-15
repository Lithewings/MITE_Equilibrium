package com.equilibrium.block.crafting_table;

import com.equilibrium.block.CraftingDifficultyHelper;
import com.equilibrium.network.C2SClickTimesPacket;
import com.equilibrium.network.C2STriggerContentChangePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import static com.equilibrium.GlobalModConfig.isAutoCraftingEnabled;
import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromClient;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_CRAFTING_TIME_AND_LEVEL;
import static com.equilibrium.network.C2STriggerContentChangePacket.sendTrigger;
import static com.equilibrium.util.SharedConstant.INVALID_CRAFTING_TEXT;

public class ModCraftingScreen extends AbstractContainerScreen<ModCraftingScreenHandler> implements RecipeUpdateListener {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/crafting_table.png");
    private final RecipeBookComponent recipeBook = new RecipeBookComponent();
    private boolean narrow;

    public ModCraftingScreen(ModCraftingScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    //——————————————————————————————————————————————————————————————————
    private CraftTimeController getPlayerCraftingView(Minecraft client){
        return (CraftTimeController) client.player;
    }
    private static final ResourceLocation CRAFT_OVERLAY_TEXTURE = ResourceLocation.parse("miteequilibrium:textures/gui/crafting_table.png");

    int time = 0;

    //——————————————————————————————————————————————————————————————————

    @Override
    protected void init() {
        super.init();
        this.narrow = this.width < 379;
        this.recipeBook.init(this.width, this.height, this.minecraft, this.narrow, this.menu);
        this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);
        this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, button -> {
            this.recipeBook.toggleVisibility();
            this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);
            button.setPosition(this.leftPos + 5, this.height / 2 - 49);
        }));
        this.addWidget(this.recipeBook);
        this.titleLabelX = 29;
    }

    @ModifiedFromVanilla("ModCraftingScreen")
    @Override
    public void containerTick() {
        super.containerTick();
        this.recipeBook.tick();


        CraftTimeController playerCraftingView = getPlayerCraftingView(this.minecraft);

        ItemStack resultItemStack = this.menu.getSlot(0).getItem();
        if (resultItemStack.get(DataComponents.LORE) != null) {
            for (Component text : resultItemStack.get(DataComponents.LORE).lines()) {
                if (text.contains(INVALID_CRAFTING_TEXT)) {
                    playerCraftingView.stopCraft();
                    return;
                }
            }
        }



        //输入输出不为空时,才考虑试图合成
        if (!this.menu.input.isEmpty() && !this.menu.getSlot(0).getItem().isEmpty()) {
            //获得合成难度
            playerCraftingView.setCraftStage(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.menu, true, this));
            //进行一次craftTick,若合成结束返回true
            if (playerCraftingView.isCraftTickFinished()) {
                //模拟无限制时秒出合成物品的一次操作
                super.slotClicked(this.menu.getSlot(0), 0, 0, ClickType.THROW);
                //在ScreenHandlerMixin中自动将鼠标stack下的物品放入玩家物品栏中
                if(!isAutoCraftingEnabled()){
                    playerCraftingView.stopCraft();
                }
            }
            //刷新一次合成结果栏
            if (this.menu.getSlot(0).getItem().isEmpty()) {
                sendTrigger();
            }
        } else playerCraftingView.stopCraft();
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.recipeBook.isVisible() && this.narrow) {
            this.renderBackground(context, mouseX, mouseY, delta);
            this.recipeBook.render(context, mouseX, mouseY, delta);
        } else {
            super.render(context, mouseX, mouseY, delta);
            this.recipeBook.render(context, mouseX, mouseY, delta);
            this.recipeBook.renderGhostRecipe(context, this.leftPos, this.topPos, true, delta);
        }

        this.renderTooltip(context, mouseX, mouseY);
        this.recipeBook.renderTooltip(context, this.leftPos, this.topPos, mouseX, mouseY);
    }

    @Override
    @ModifiedFromVanilla("ModCraftingScreen")
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);


        //每秒渲染20次

        CraftTimeController craftTimeController = this.getPlayerCraftingView(this.minecraft);

        RenderSystem.setShaderTexture(0, CRAFT_OVERLAY_TEXTURE);
        int _i = this.leftPos;
        int _j = (this.height - this.imageHeight) / 2;


        if (craftTimeController.isCrafting() && craftTimeController.getCraftStage() > 0) {
            int l = (int) ((craftTimeController.getCraftTimeCost() * 24.0F / craftTimeController.getCraftStage()));
            if (l >= 24) {
                context.blit(CRAFT_OVERLAY_TEXTURE, _i + 89, _j + 35, 0, 0, 25, 16, 24, 17);
            } else {
                context.blit(CRAFT_OVERLAY_TEXTURE, _i + 89, _j + 35, 0, 0, l + 1, 16, 24, 17);
            }
        }
    }

    @ModifiedFromVanilla("ModCraftingScreen")
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((keyCode == GLFW.GLFW_KEY_E || (keyCode == GLFW.GLFW_KEY_ESCAPE)) && this.shouldCloseOnEsc()) {
            //一旦中途退出,就失去所有进度渲染
            getPlayerCraftingView(this.minecraft).setCraftTimeCost(0);
            C2SClickTimesPacket.sendClickTimes(0);
            this.onClose();
            return this.recipeBook.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            return this.recipeBook.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.recipeBook.charTyped(chr, modifiers) ? true : super.charTyped(chr, modifiers);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double pointX, double pointY) {
        return (!this.narrow || !this.recipeBook.isVisible()) && super.isHovering(x, y, width, height, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.recipeBook);
            return true;
        } else {
            return this.narrow && this.recipeBook.isVisible() ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left
                || mouseY < (double)top
                || mouseX >= (double)(left + this.imageWidth)
                || mouseY >= (double)(top + this.imageHeight);
        return this.recipeBook.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, button) && bl;
    }

    @ModifiedFromVanilla("ModCraftingScreen")
    @Override
    protected void slotClicked(Slot slot, int invSlot, int clickData, ClickType actionType) {
        CraftTimeController playerCraftingView = this.getPlayerCraftingView(this.minecraft);

        if (slot != null) {
            invSlot = slot.index;
        } else {
            //slot = null时,会触发invSlot=-999index越界错,说明鼠标点击的位置没有slot可用,这里需要额外处理,因为涉及发包
            super.slotClicked(slot, invSlot, clickData, actionType);
            this.recipeBook.slotClicked(slot);
            return;
        }

        if (invSlot == 0 && clickData == 1) {
            //右键只会改变配方,不会合成
            time++;

            //服务端处理,真实逻辑处理

            C2SClickTimesPacket.sendClickTimes(time);
            C2STriggerContentChangePacket.sendTrigger();
            playerCraftingView.setCraftTimeCost(0);
            playerCraftingView.setCraftingStatus(false);
            return;
        }
        if (invSlot > 0 && invSlot < 10) {
            playerCraftingView.setCraftTimeCost(0);
            playerCraftingView.setCraftingStatus(false);
        }
        if (invSlot == 0 && clickData == 0) {
            ItemStack resultItemStack = this.menu.getSlot(0).getItem();
            if (resultItemStack.get(DataComponents.LORE) != null) {
                for (Component text : resultItemStack.get(DataComponents.LORE).lines()) {
                    if (text.contains(INVALID_CRAFTING_TEXT)) {
                        return;
                    }
                }
            }

            //没有进行合成且输入输出不会空时,才考虑合成
            if (!playerCraftingView.isCrafting() && !this.menu.input.isEmpty() && !this.menu.getSlot(0).getItem().isEmpty()) {
                playerCraftingView.startCraftWithNewStage(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.menu, false, this));
            }
            //阻止直接从输出栏拿物品
            if(getGameBooleanRuleFromClient(ENABLE_CRAFTING_TIME_AND_LEVEL))
                return;
        }
        super.slotClicked(slot, invSlot, clickData, actionType);
        this.recipeBook.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBook.recipesUpdated();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBook;
    }
}
