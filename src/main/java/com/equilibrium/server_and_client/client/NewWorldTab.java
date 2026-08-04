package com.equilibrium.server_and_client.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.SwitchGrid;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class NewWorldTab extends GridLayoutTab {
    private static final Component WORLD_TAB_TITLE_TEXT = Component.translatable("createWorld.tab.world.title");
    private static final Component AMPLIFIED_GENERATOR_INFO_TEXT = Component.translatable("generator.minecraft.amplified.info");
    private static final Component MAP_FEATURES_TEXT = Component.translatable("selectWorld.mapFeatures");
    private static final Component MAP_FEATURES_INFO_TEXT = Component.translatable("mod.selectWorld.mapFeatures.info");
    private static final Component BONUS_ITEMS_TEXT = Component.translatable("selectWorld.bonusItems");
    private static final Component BONUS_ITEMS_INFO_TEXT = Component.translatable("mod.selectWorld.bonusItems");

    private static final Component ENTER_SEED_TEXT = Component.translatable("selectWorld.enterSeed");
    static final Component SEED_INFO_TEXT = Component.translatable("mod.selectWorld.seedInfo").withStyle(ChatFormatting.DARK_GRAY);
    private static final int field_42190 = 310;
    private final EditBox seedField;
    private final Button customizeButton;


    public static boolean alwaysTrue() {
        return true;
    }
    public static boolean alwaysFalse() {
        return false;
    }




    public NewWorldTab(CreateWorldScreen createWorldScreen, Font textRenderer) {
        super(WORLD_TAB_TITLE_TEXT);
        GridLayout.RowHelper adder = this.layout.columnSpacing(10).rowSpacing(8).createRowHelper(2);
        CycleButton<WorldCreationUiState.WorldTypeEntry> cyclingButtonWidget = adder.addChild(
                CycleButton.<WorldCreationUiState.WorldTypeEntry>builder(WorldCreationUiState.WorldTypeEntry::describePreset)
                        .withValues(createWorldScreen.getUiState().getNormalPresetList().getFirst())
                        .create(0, 0, 150, 20, Component.translatable("selectWorld.mapType"), (button, worldType) -> createWorldScreen.getUiState().setWorldType(worldType))
        );
        cyclingButtonWidget.setValue(createWorldScreen.getUiState().getWorldType());

        cyclingButtonWidget.active=false;
        this.customizeButton = adder.addChild(Button.builder(Component.translatable("selectWorld.customizeType"), button -> this.openCustomizeScreen(createWorldScreen)).build());
        createWorldScreen.getUiState().addListener(creator -> this.customizeButton.active = !creator.isDebug() && creator.getPresetEditor() != null);
        this.seedField = new EditBox(textRenderer, 308, 20, Component.translatable("selectWorld.enterSeed")) {
            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(CommonComponents.NARRATION_SEPARATOR).append(SEED_INFO_TEXT);
            }
        };
        this.seedField.setHint(SEED_INFO_TEXT);
        this.seedField.setValue(createWorldScreen.getUiState().getSeed());
        this.seedField.setResponder(seed -> createWorldScreen.getUiState().setSeed(this.seedField.getValue()));
        this.seedField.active=false;

        adder.addChild(CommonLayouts.labeledElement(textRenderer, this.seedField, ENTER_SEED_TEXT), 2);
        SwitchGrid.Builder builder = SwitchGrid.builder(310);
        //我可以保证这个屏幕只会在非调试模式下使用,奖励箱无论何时一定生成;结构也一定会生成,默认在所有模式下都生成结构,调式模式下不调用这个屏幕
        builder.addSwitch(MAP_FEATURES_TEXT, createWorldScreen.getUiState()::isGenerateStructures, createWorldScreen.getUiState()::setGenerateStructures)
                .withIsActiveCondition(()->false)
                .withInfo(MAP_FEATURES_INFO_TEXT);
        builder.addSwitch(BONUS_ITEMS_TEXT, NewWorldTab::alwaysTrue, null)
                .withIsActiveCondition(() -> false)
                .withInfo(BONUS_ITEMS_INFO_TEXT);
        createWorldScreen.getUiState().setBonusChest(true);
        SwitchGrid worldScreenOptionGrid = builder.build(widget ->
            adder.addChild(widget, 2)
        );




        createWorldScreen.getUiState().addListener(creator -> worldScreenOptionGrid.refreshStates());


    }

    private void openCustomizeScreen(CreateWorldScreen createWorldScreen) {
        PresetEditor levelScreenProvider = createWorldScreen.getUiState().getPresetEditor();
        if (levelScreenProvider != null) {
            createWorldScreen.getMinecraft()
                    .setScreen(levelScreenProvider.createEditScreen(createWorldScreen, createWorldScreen.getUiState().getSettings()));
        }
    }

}