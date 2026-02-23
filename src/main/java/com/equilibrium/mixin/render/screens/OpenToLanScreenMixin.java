package com.equilibrium.mixin.render.screens;

import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.PublishCommand;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.NetworkUtils;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(OpenToLanScreen.class)
public class OpenToLanScreenMixin extends Screen {

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



    private Text updatePort(String portText) {
        if (portText.isBlank()) {
            this.port = NetworkUtils.findLocalPort();
            return null;
        } else {
            try {
                this.port = Integer.parseInt(portText);
                if (this.port < 1024 || this.port > 65535) {
                    return INVALID_PORT_TEXT;
                } else {
                    return !NetworkUtils.isPortAvailable(this.port) ? UNAVAILABLE_PORT_TEXT : null;
                }
            } catch (NumberFormatException var3) {
                this.port = NetworkUtils.findLocalPort();
                return INVALID_PORT_TEXT;
            }
        }
    }



    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void disableCheatsButton(CallbackInfo ci) {
        ci.cancel();
        IntegratedServer integratedServer = this.client.getServer();
        this.gameMode = integratedServer.getDefaultGameMode();
        this.allowCommands = integratedServer.getSaveProperties().areCommandsAllowed();

        CyclingButtonWidget<Boolean> cyclingButtonWidgetAllowCommand = CyclingButtonWidget.onOffBuilder(this.allowCommands)
                .values(List.of(false))
                .initially(false)
                .build(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_TEXT, (button, allowCommands) -> this.allowCommands = allowCommands);
        //让按钮变灰色
        cyclingButtonWidgetAllowCommand.active=false;


        CyclingButtonWidget<GameMode> cyclingButtonWidgetAllowCreativeMode =  CyclingButtonWidget.<GameMode>builder(GameMode::getSimpleTranslatableName)
                .values(GameMode.SURVIVAL,GameMode.ADVENTURE)
                .initially(GameMode.SURVIVAL)
                .build(this.width / 2 - 155, 100, 150, 20, GAME_MODE_TEXT, (button, gameMode) -> this.gameMode = gameMode);

//        cyclingButtonWidgetAllowCreativeMode.active=false;


        this.addDrawableChild(
                cyclingButtonWidgetAllowCreativeMode
        );
        this.addDrawableChild(
                cyclingButtonWidgetAllowCommand
        );
        ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable("lanServer.start"), button -> {
            this.client.setScreen(null);
            Text text;
            if (integratedServer.openToLan(this.gameMode, this.allowCommands, this.port)) {
                text = PublishCommand.getStartedText(this.port);
            } else {
                text = Text.translatable("commands.publish.failed");
            }

            this.client.inGameHud.getChatHud().addMessage(text);
            this.client.updateWindowTitle();
        }).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build();
        this.portField = new TextFieldWidget(this.textRenderer, this.width / 2 - 75, 160, 150, 20, Text.translatable("lanServer.port"));
        this.portField.setChangedListener(portText -> {
            Text text = this.updatePort(portText);
            this.portField.setPlaceholder(Text.literal(this.port + "").formatted(Formatting.DARK_GRAY));
            if (text == null) {
                this.portField.setEditableColor(14737632);
                this.portField.setTooltip(null);
                buttonWidget.active = true;
            } else {
                this.portField.setEditableColor(16733525);
                this.portField.setTooltip(Tooltip.of(text));
                buttonWidget.active = false;
            }
        });
        this.portField.setPlaceholder(Text.literal(this.port + "").formatted(Formatting.DARK_GRAY));


        this.addDrawableChild(this.portField);
        this.addDrawableChild(buttonWidget);
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());











    }
}



