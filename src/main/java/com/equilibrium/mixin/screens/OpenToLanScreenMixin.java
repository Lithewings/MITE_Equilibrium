package com.equilibrium.mixin.screens;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(ShareToLanScreen.class)
public abstract class OpenToLanScreenMixin extends Screen {

    protected OpenToLanScreenMixin(Component title) {
        super(title);
    }


    @Shadow
    private GameType gameMode = GameType.SURVIVAL;
    @Shadow
    private boolean commands;

    @Shadow
    private static final Component ALLOW_COMMANDS_LABEL = Component.translatable("selectWorld.allowCommands.new");
    @Shadow
    private static final Component GAME_MODE_LABEL = Component.translatable("selectWorld.gameMode");






    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ShareToLanScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",ordinal = 1))
    private GuiEventListener disableCheatsButton(GuiEventListener par1) {
        CycleButton<Boolean> cyclingButtonWidgetAllowCommand = CycleButton.onOffBuilder(this.commands)
                .withValues(List.of(false))
                .withInitialValue(false)
                .create(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_LABEL, (button, allowCommands) -> this.commands = allowCommands);
        //让按钮变灰色
        cyclingButtonWidgetAllowCommand.active=false;
        return cyclingButtonWidgetAllowCommand;

    }
    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ShareToLanScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",ordinal = 0))
    private GuiEventListener disableCreativeGamemode(GuiEventListener par1) {
        //取消创造模式


        return CycleButton.<GameType>builder(GameType::getShortDisplayName)
                .withValues(GameType.SURVIVAL,GameType.ADVENTURE)
                .withInitialValue(GameType.SURVIVAL)
                .create(this.width / 2 - 155, 100, 150, 20, GAME_MODE_LABEL, (button, gameMode1) -> this.gameMode = gameMode1);
    }
}



