package com.equilibrium.mixin.render.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.NetworkUtils;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(OpenToLanScreen.class)
public abstract class OpenToLanScreenMixin extends Screen {

    protected OpenToLanScreenMixin(Text title) {
        super(title);
    }

@Shadow
@Final
    private Screen parent;
    @Shadow
    private GameMode gameMode = GameMode.SURVIVAL;
    @Shadow
    private boolean allowCommands;
    @Shadow
    private int port = NetworkUtils.findLocalPort();

    @Shadow
    private TextFieldWidget portField;

@Shadow
    private static final Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands.new");
@Shadow
    private static final Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
@Shadow
    private static final Text OTHER_PLAYERS_TEXT = Text.translatable("lanServer.otherPlayers");
@Shadow
    private static final Text PORT_TEXT = Text.translatable("lanServer.port");
@Shadow
    private static final Text UNAVAILABLE_PORT_TEXT = Text.translatable("lanServer.port.unavailable.new", 1024, 65535);
@Shadow
    private static final Text INVALID_PORT_TEXT = Text.translatable("lanServer.port.invalid.new", 1024, 65535);
@Shadow
    private static final int ERROR_TEXT_COLOR = 16733525;







    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/OpenToLanScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",ordinal = 1))
    private Element disableCheatsButton(Element par1) {

        CyclingButtonWidget<Boolean> cyclingButtonWidgetAllowCommand = CyclingButtonWidget.onOffBuilder(this.allowCommands)
                .values(List.of(false))
                .initially(false)
                .build(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_TEXT, (button, allowCommands) -> this.allowCommands = allowCommands);
        //让按钮变灰色
        cyclingButtonWidgetAllowCommand.active=false;
        return cyclingButtonWidgetAllowCommand;

    }
    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/OpenToLanScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",ordinal = 0))
    private Element disableCreativeGamemode(Element par1) {
        //取消创造模式


        return CyclingButtonWidget.<GameMode>builder(GameMode::getSimpleTranslatableName)
                .values(GameMode.SURVIVAL,GameMode.ADVENTURE)
                .initially(GameMode.SURVIVAL)
                .build(this.width / 2 - 155, 100, 150, 20, GAME_MODE_TEXT, (button, gameMode1) -> this.gameMode = gameMode1);
    }
}



