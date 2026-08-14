package com.equilibrium.block.crafting_table;

import com.equilibrium.block.CraftingDifficultyHelper;
import com.equilibrium.network.C2SClickTimesPacket;
import com.equilibrium.network.C2STriggerContentChangePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.equilibrium.GlobalModConfig.isAutoCraftingEnabled;
import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromClient;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_CRAFTING_TIME_AND_LEVEL;
import static com.equilibrium.network.C2STriggerContentChangePacket.sendTrigger;
import static com.equilibrium.util.SharedConstant.INVALID_CRAFTING_TEXT;

public class ModCraftingScreen extends HandledScreen<ModCraftingScreenHandler> implements RecipeBookProvider {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/crafting_table.png");
    private final RecipeBookWidget recipeBook = new RecipeBookWidget();
    private boolean narrow;

    public ModCraftingScreen(ModCraftingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    //——————————————————————————————————————————————————————————————————
    private CraftTimeController getPlayerCraftingView(MinecraftClient client){
        return (CraftTimeController) client.player;
    }
    private static final Identifier CRAFT_OVERLAY_TEXTURE = Identifier.of("miteequilibrium:textures/gui/crafting_table.png");

    int time = 0;

    //——————————————————————————————————————————————————————————————————

    @Override
    protected void init() {
        super.init();
        this.narrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, this.handler);
        this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
        this.addDrawableChild(new TexturedButtonWidget(this.x + 5, this.height / 2 - 49, 20, 18, RecipeBookWidget.BUTTON_TEXTURES, button -> {
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            button.setPosition(this.x + 5, this.height / 2 - 49);
        }));
        this.addSelectableChild(this.recipeBook);
        this.titleX = 29;
    }

    @ModifiedFromVanilla("ModCraftingScreen")
    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        this.recipeBook.update();


        CraftTimeController playerCraftingView = getPlayerCraftingView(this.client);

        ItemStack resultItemStack = this.handler.getSlot(0).getStack();
        if (resultItemStack.get(DataComponentTypes.LORE) != null) {
            for (Text text : resultItemStack.get(DataComponentTypes.LORE).lines()) {
                if (text.contains(INVALID_CRAFTING_TEXT)) {
                    playerCraftingView.stopCraft();
                    return;
                }
            }
        }



        //输入输出不为空时,才考虑试图合成
        if (!this.handler.input.isEmpty() && !this.handler.getSlot(0).getStack().isEmpty()) {
            //获得合成难度
            playerCraftingView.setCraftStage(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.handler, true, this));
            //进行一次craftTick,若合成结束返回true
            if (playerCraftingView.isCraftTickFinished()) {
                //模拟无限制时秒出合成物品的一次操作
                super.onMouseClick(this.handler.getSlot(0), 0, 0, SlotActionType.THROW);
                //在ScreenHandlerMixin中自动将鼠标stack下的物品放入玩家物品栏中
                if(!isAutoCraftingEnabled()){
                    playerCraftingView.stopCraft();
                }
            }
            //刷新一次合成结果栏
            if (this.handler.getSlot(0).getStack().isEmpty()) {
                sendTrigger();
            }
        } else playerCraftingView.stopCraft();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.recipeBook.isOpen() && this.narrow) {
            this.renderBackground(context, mouseX, mouseY, delta);
            this.recipeBook.render(context, mouseX, mouseY, delta);
        } else {
            super.render(context, mouseX, mouseY, delta);
            this.recipeBook.render(context, mouseX, mouseY, delta);
            this.recipeBook.drawGhostSlots(context, this.x, this.y, true, delta);
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);
        this.recipeBook.drawTooltip(context, this.x, this.y, mouseX, mouseY);
    }

    @Override
    @ModifiedFromVanilla("ModCraftingScreen")
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);


        //每秒渲染20次

        CraftTimeController craftTimeController = this.getPlayerCraftingView(this.client);

        RenderSystem.setShaderTexture(0, CRAFT_OVERLAY_TEXTURE);
        int _i = this.x;
        int _j = (this.height - this.backgroundHeight) / 2;


        if (craftTimeController.isCrafting() && craftTimeController.getCraftStage() > 0) {
            int l = (int) ((craftTimeController.getCraftTimeCost() * 24.0F / craftTimeController.getCraftStage()));
            if (l >= 24) {
                context.drawTexture(CRAFT_OVERLAY_TEXTURE, _i + 89, _j + 35, 0, 0, 25, 16, 24, 17);
            } else {
                context.drawTexture(CRAFT_OVERLAY_TEXTURE, _i + 89, _j + 35, 0, 0, l + 1, 16, 24, 17);
            }
        }
    }

    @ModifiedFromVanilla("ModCraftingScreen")
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((keyCode == GLFW.GLFW_KEY_E || (keyCode == GLFW.GLFW_KEY_ESCAPE)) && this.shouldCloseOnEsc()) {
            //一旦中途退出,就失去所有进度渲染
            getPlayerCraftingView(this.client).setCraftTimeCost(0);
            C2SClickTimesPacket.sendClickTimes(0);
            this.close();
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
    protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.recipeBook);
            return true;
        } else {
            return this.narrow && this.recipeBook.isOpen() ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left
                || mouseY < (double)top
                || mouseX >= (double)(left + this.backgroundWidth)
                || mouseY >= (double)(top + this.backgroundHeight);
        return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
    }

    @ModifiedFromVanilla("ModCraftingScreen")
    @Override
    protected void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType) {
        CraftTimeController playerCraftingView = this.getPlayerCraftingView(this.client);

        if (slot != null) {
            invSlot = slot.id;
        } else {
            //slot = null时,会触发invSlot=-999index越界错,说明鼠标点击的位置没有slot可用,这里需要额外处理,因为涉及发包
            super.onMouseClick(slot, invSlot, clickData, actionType);
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
            ItemStack resultItemStack = this.handler.getSlot(0).getStack();
            if (resultItemStack.get(DataComponentTypes.LORE) != null) {
                for (Text text : resultItemStack.get(DataComponentTypes.LORE).lines()) {
                    if (text.contains(INVALID_CRAFTING_TEXT)) {
                        return;
                    }
                }
            }

            //没有进行合成且输入输出不会空时,才考虑合成
            if (!playerCraftingView.isCrafting() && !this.handler.input.isEmpty() && !this.handler.getSlot(0).getStack().isEmpty()) {
                playerCraftingView.startCraftWithNewStage(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.handler, false, this));
            }
            //阻止直接从输出栏拿物品
            if(getGameBooleanRuleFromClient(ENABLE_CRAFTING_TIME_AND_LEVEL))
                return;
        }
        super.onMouseClick(slot, invSlot, clickData, actionType);
        this.recipeBook.slotClicked(slot);
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }
}
