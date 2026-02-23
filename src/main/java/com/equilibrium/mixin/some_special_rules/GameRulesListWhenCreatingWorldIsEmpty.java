package com.equilibrium.mixin.some_special_rules;

import com.google.common.collect.Sets;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(EditGameRulesScreen.class)
public class GameRulesListWhenCreatingWorldIsEmpty extends Screen {
    @Shadow
    private static final Text TITLE = Text.translatable("editGamerule.title");
    @Shadow
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    @Shadow
    @Final
    private Consumer<Optional<GameRules>> ruleSaver;
    @Shadow
    @Final
    private final Set<EditGameRulesScreen.AbstractRuleWidget> invalidRuleWidgets = Sets.<EditGameRulesScreen.AbstractRuleWidget>newHashSet();
    @Shadow
    @Final
    private GameRules gameRules;
    @Shadow
    private ButtonWidget doneButton;
    @Shadow
    private EditGameRulesScreen.RuleListWidget ruleListWidget;
    protected GameRulesListWhenCreatingWorldIsEmpty(Text title) {
        super(title);
    }


    @Inject(method = "init",at = @At(value = "HEAD"), cancellable = true)
    protected void init(CallbackInfo ci) {
        ci.cancel();
        this.layout.addHeader(TITLE, this.textRenderer);
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        this.doneButton = directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.ruleSaver.accept(Optional.of(this.gameRules))).build());
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget var10000 = this.addDrawableChild(child);
        });
        this.initTabNavigation();
    }
}
