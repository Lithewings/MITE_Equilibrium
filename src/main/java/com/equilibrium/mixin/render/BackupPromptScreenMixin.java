package com.equilibrium.mixin.render;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.BackupPromptScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BackupPromptScreen.class)
public class BackupPromptScreenMixin extends Screen {
    protected BackupPromptScreenMixin(Text title) {
        super(title);
    }

    @Shadow @Final
    private  Runnable onCancel;
    @Shadow @Final
    protected  BackupPromptScreen.Callback callback;
    @Shadow @Final
    private  Text subtitle;
    @Shadow @Final
    private  boolean showEraseCacheCheckbox;
    @Shadow
    private MultilineText wrappedText;
    @Shadow
    private CheckboxWidget eraseCacheCheckbox;


//    @ModifyArg(method = "init",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/BackupPromptScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",ordinal = 0))
//    protected Element init1(Element par1) {
//        ButtonWidget buttonWidget = (ButtonWidget) par1;
//        buttonWidget.visible=false;
//        return buttonWidget;
//    }
//    @ModifyArg(method = "init",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/BackupPromptScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",ordinal = 1))
//    protected Element init2(Element par1) {
//        ButtonWidget buttonWidget = (ButtonWidget) par1;
//        buttonWidget.visible=false;
//        return buttonWidget;
//    }
//
//    @ModifyArg(method = "init",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/BackupPromptScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",ordinal = 2))
//    protected Element init3(Element par1) {
//        ButtonWidget buttonWidget = (ButtonWidget) par1;
//        ((ButtonWidget) par1).setMessage(Text.translatable("select.world.wrong.version"));
//        return buttonWidget;
//    }





}
