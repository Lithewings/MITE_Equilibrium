package com.equilibrium.mixin.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {
    protected DeathScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    private int delayTicker;
    @Shadow
    @Final
    private static ResourceLocation DRAFT_REPORT_SPRITE;

    @Shadow
    @Final
    private Component deathScore;
    @Shadow
    @Final
    private boolean hardcore;

    @Shadow
    private Button exitToTitleButton;
    @Shadow
    @Final
    private List<Button> exitButtons;



    @Shadow
    private Style getClickedComponentStyleAt(int mouseX) {
        if (this.deathScore == null) {
            return null;
        } else {
            int i = this.minecraft.font.width(this.deathScore);
            int j = this.width / 2 - i / 2;
            int k = this.width / 2 + i / 2;
            return mouseX >= j && mouseX <= k ? this.minecraft.font.getSplitter().componentStyleAtWidth(this.deathScore, mouseX - j) : null;
        }
    }


    @Unique
    private int nextReviveTime =5*20;

    @Unique
    public int getNextReviveTime(){
        return Math.max((nextReviveTime-this.delayTicker)/20, 0);
    }
    @Shadow
    private void exitToTitleScreen() {
        if (this.minecraft.level != null) {
            this.minecraft.level.disconnect();
        }

        this.minecraft.disconnect(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
        this.minecraft.setScreen(new TitleScreen());
    }

    @Inject(method = "handleExitToTitleScreen",at = @At("HEAD"),cancellable = true)
    private void onTitleScreenButtonClicked(CallbackInfo ci) {
        ci.cancel();
        this.exitToTitleScreen();
    }






    @Override
    public void tick() {
        super.tick();
        this.delayTicker++;
        if (getNextReviveTime()==0) {
            this.setButtonsActive(true);
        }
        else
            for (Button buttonWidget : this.exitButtons) {
                if(!buttonWidget.getMessage().contains(Component.translatable("deathScreen.respawn")))
                    buttonWidget.active=true;
            }

    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Shadow
    private void setButtonsActive(boolean active) {
        for (Button buttonWidget : this.exitButtons) {
            buttonWidget.active = active;
        }
    }


    @Shadow public abstract void render(GuiGraphics context, int mouseX, int mouseY, float delta);

    @Inject(method = "render",at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        super.render(context, mouseX, mouseY, delta);
        context.pose().pushPose();
        context.pose().scale(2.0F, 2.0F, 2.0F);
        context.drawCenteredString(this.font, this.title, this.width / 2 / 2, 30, 16777215);
        context.pose().popPose();
        if (this.deathScore != null) {
            context.drawCenteredString(this.font, this.deathScore, this.width / 2, 85, 16777215);
        }

        if(!hardcore) {
            String nextReviveTime = Component.translatable("next_respawn").getString()+getNextReviveTime()+" s";
            String deathText = Component.translatable("mod_death_text").getString();
            context.drawCenteredString(this.font, nextReviveTime, this.width / 2, 100, 16777215);
            context.drawCenteredString(this.font, deathText, this.width / 2, 115, 16777215);
        }
        if (this.deathScore != null && mouseY > 85 && mouseY < 85 + 9) {
            Style style = this.getClickedComponentStyleAt(mouseX);
            context.renderComponentHoverEffect(this.font, style, mouseX, mouseY);
        }

        if (this.exitToTitleButton != null && this.minecraft.getReportingContext().hasDraftReport()) {
            context.blitSprite(
                    DRAFT_REPORT_SPRITE, this.exitToTitleButton.getX() + this.exitToTitleButton.getWidth() - 17, this.exitToTitleButton.getY() + 3, 15, 15
            );
        }



    }


}
