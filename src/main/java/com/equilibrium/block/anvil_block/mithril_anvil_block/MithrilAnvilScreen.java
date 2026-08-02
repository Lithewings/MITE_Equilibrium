package com.equilibrium.block.anvil_block.mithril_anvil_block;

import com.equilibrium.tags.ModItemTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;


public class MithrilAnvilScreen extends ItemCombinerScreen<MithrilAnvilScreenHandler> {
    private static final ResourceLocation TEXT_FIELD_TEXTURE = ResourceLocation.withDefaultNamespace("container/anvil/text_field");
    private static final ResourceLocation TEXT_FIELD_DISABLED_TEXTURE = ResourceLocation.withDefaultNamespace("container/anvil/text_field_disabled");
    private static final ResourceLocation ERROR_TEXTURE = ResourceLocation.withDefaultNamespace("container/anvil/error");
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/anvil.png");
    private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");
    private EditBox nameField;
    private final Player player;

    public MithrilAnvilScreen(MithrilAnvilScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, TEXTURE);
        this.player = inventory.player;
        this.titleLabelX = 60;
    }

    @Override
    protected void subInit() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.nameField = new EditBox(this.font, i + 62, j + 24, 103, 12, Component.translatable("container.repair"));
        this.nameField.setCanLoseFocus(false);
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(50);
        this.nameField.setResponder(this::onRenamed);
        this.nameField.setValue("");
        this.addWidget(this.nameField);
        this.nameField.setEditable(this.menu.getSlot(0).hasItem());
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameField);
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        String string = this.nameField.getValue();
        this.init(client, width, height);
        this.nameField.setValue(string);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }

        return !this.nameField.keyPressed(keyCode, scanCode, modifiers) && !this.nameField.canConsumeInput() ? super.keyPressed(keyCode, scanCode, modifiers) : true;
    }


    private void onRenamed(String name) {
        Slot slot = this.menu.getSlot(0);
        if (slot.hasItem()) {
            String string = name;
            if (!slot.getItem().has(DataComponents.CUSTOM_NAME) && name.equals(slot.getItem().getHoverName().getString())) {
                string = "";
            }

            if (this.menu.setNewItemName(string)) {
                this.minecraft.player.connection.send(new ServerboundRenameItemPacket(string));
            }
        }
    }

    private boolean shouldRejectForIronAnvil(ItemStack input1, ItemStack input2) {
        boolean shouldReject1 = input1.is(ModItemTags.MITHRIL_ANVIL_REJECTION);
        boolean shouldReject2 = input2.is(ModItemTags.MITHRIL_ANVIL_REJECTION);
        return shouldReject1 || shouldReject2;
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        super.renderLabels(context, mouseX, mouseY);
        int i = this.menu.getLevelCost();
        if (i > 0) {
            int j = 8453920;
            Component text;
            if (shouldRejectForIronAnvil(this.menu.getSlot(0).getItem(),this.menu.getSlot(1).getItem())) {
                text = TOO_EXPENSIVE_TEXT;
                j = 16736352;
            } else if (!this.menu.getSlot(2).hasItem()) {
                text = null;
            } else {
                text = Component.translatable("container.repair.cost", i);
                if (!this.menu.getSlot(2).mayPickup(this.player)) {
                    j = 16736352;
                }
            }

            if (text != null && text!=TOO_EXPENSIVE_TEXT) {
                int k = this.imageWidth - 8 - this.font.width(text) - 2;
                int l = 69;
                context.fill(k - 2, 67, this.imageWidth - 8, 79, 1325400064);
                int cost = this.menu.getLevelCost();

                context.drawString(this.font, Component.nullToEmpty( Component.translatable("container.repair.require").getString()+": "+cost), k, 69, j);
            }
            else if(text == TOO_EXPENSIVE_TEXT){
                int k = this.imageWidth - 8 - this.font.width(text) - 2;
                int l = 69;
                context.fill(k - 2, 67, this.imageWidth - 8, 79, 1325400064);
                context.drawString(this.font, text, k, 69, j);
            }

        }
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        super.renderBg(context, delta, mouseX, mouseY);
        context.blitSprite(this.menu.getSlot(0).hasItem() ? TEXT_FIELD_TEXTURE : TEXT_FIELD_DISABLED_TEXTURE, this.leftPos + 59, this.topPos + 20, 110, 16);
    }

    @Override
    public void renderFg(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.nameField.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void renderErrorIcon(GuiGraphics context, int x, int y) {
        if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(this.menu.getResultSlot()).hasItem()) {
            context.blitSprite(ERROR_TEXTURE, x + 99, y + 45, 28, 21);
        }
    }

    @Override
    public void slotChanged(AbstractContainerMenu handler, int slotId, ItemStack stack) {
        if (slotId == 0) {
            this.nameField.setValue(stack.isEmpty() ? "" : stack.getHoverName().getString());
            this.nameField.setEditable(!stack.isEmpty());
            this.setFocused(this.nameField);
        }
    }
}
