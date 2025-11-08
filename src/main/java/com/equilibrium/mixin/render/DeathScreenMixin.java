package com.equilibrium.mixin.render;

import com.equilibrium.MITEequilibrium;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
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
    protected DeathScreenMixin(Text title) {
        super(title);
    }
    @Shadow
    private Text scoreText;
    @Shadow
    private int ticksSinceDeath;
    @Shadow
    @Final
    private static Identifier DRAFT_REPORT_ICON_TEXTURE;

    @Shadow
    @Final
    private Text message;
    @Shadow
    @Final
    private boolean isHardcore;

    @Shadow
    private ButtonWidget titleScreenButton;
    @Shadow
    @Final
    private List<ButtonWidget> buttons;



    @Shadow
    private Style getTextComponentUnderMouse(int mouseX) {
        if (this.message == null) {
            return null;
        } else {
            int i = this.client.textRenderer.getWidth(this.message);
            int j = this.width / 2 - i / 2;
            int k = this.width / 2 + i / 2;
            return mouseX >= j && mouseX <= k ? this.client.textRenderer.getTextHandler().getStyleAt(this.message, mouseX - j) : null;
        }
    }


    @Unique
    private int nextReviveTime =5*20;

    @Unique
    public int getNextReviveTime(){
        return Math.max((nextReviveTime-this.ticksSinceDeath)/20, 0);
    }
    @Shadow
    private void quitLevel() {
        if (this.client.world != null) {
            this.client.world.disconnect();
        }

        this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
        this.client.setScreen(new TitleScreen());
    }

    @Inject(method = "onTitleScreenButtonClicked",at = @At("HEAD"),cancellable = true)
    private void onTitleScreenButtonClicked(CallbackInfo ci) {
        ci.cancel();
        this.quitLevel();
    }






    @Override
    public void tick() {
        super.tick();
        this.ticksSinceDeath++;
        if (getNextReviveTime()==0) {
            this.setButtonsActive(true);
        }
        else
            for (ButtonWidget buttonWidget : this.buttons) {
                if(!buttonWidget.getMessage().contains(Text.translatable("deathScreen.respawn")))
                    buttonWidget.active=true;
            }

    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Shadow
    private void setButtonsActive(boolean active) {
        for (ButtonWidget buttonWidget : this.buttons) {
            buttonWidget.active = active;
        }
    }


    @Shadow public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);

    @Inject(method = "render",at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        super.render(context, mouseX, mouseY, delta);
        context.getMatrices().push();
        context.getMatrices().scale(2.0F, 2.0F, 2.0F);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2 / 2, 30, 16777215);
        context.getMatrices().pop();
        if (this.message != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.message, this.width / 2, 85, 16777215);
        }

        if(!isHardcore) {
            String nextReviveTime = Text.translatable("next_respawn").getString()+getNextReviveTime()+" s";
            String deathText = Text.translatable("mod_death_text").getString();
            context.drawCenteredTextWithShadow(this.textRenderer, nextReviveTime, this.width / 2, 100, 16777215);
            context.drawCenteredTextWithShadow(this.textRenderer, deathText, this.width / 2, 115, 16777215);
        }
        if (this.message != null && mouseY > 85 && mouseY < 85 + 9) {
            Style style = this.getTextComponentUnderMouse(mouseX);
            context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
        }

        if (this.titleScreenButton != null && this.client.getAbuseReportContext().hasDraft()) {
            context.drawGuiTexture(
                    DRAFT_REPORT_ICON_TEXTURE, this.titleScreenButton.getX() + this.titleScreenButton.getWidth() - 17, this.titleScreenButton.getY() + 3, 15, 15
            );
        }



    }


}
