package com.equilibrium.server_and_client.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;

import java.lang.reflect.Method;

public class NewGameTab extends GridLayoutTab {
    private static final Component GAME_TAB_TITLE_TEXT = Component.translatable("createWorld.tab.game.title");
    private static final Component ALLOW_COMMANDS_TEXT = Component.translatable("selectWorld.allowCommands.new");
    private final EditBox worldNameField;

    static final Component GAME_MODE_TEXT = Component.translatable("selectWorld.gameMode");
    static final Component ENTER_NAME_TEXT = Component.translatable("selectWorld.enterName");


    public NewGameTab(CreateWorldScreen createWorldScreen, Font textRenderer) {
        super(GAME_TAB_TITLE_TEXT);

        GridLayout.RowHelper adder = this.layout.rowSpacing(8).createRowHelper(1);
        LayoutSettings positioner = adder.newCellSettings();
        this.worldNameField = new EditBox(textRenderer, 208, 20, Component.translatable("selectWorld.enterName"));
        this.worldNameField.setValue(createWorldScreen.getUiState().getName());
        this.worldNameField.setResponder(createWorldScreen.getUiState()::setName);
        createWorldScreen.getUiState()
                .addListener(
                        creator -> this.worldNameField
                                .setTooltip(Tooltip.create(Component.translatable("selectWorld.targetFolder", Component.literal(creator.getTargetFolder()).withStyle(ChatFormatting.ITALIC))))
                );

        // 反射调用 protected 方法 setInitialFocus
        try {
            Method method = Screen.class.getDeclaredMethod("setInitialFocus", GuiEventListener.class);
            method.setAccessible(true);
            method.invoke(createWorldScreen, this.worldNameField);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adder.addChild(
                CommonLayouts.labeledElement(textRenderer, this.worldNameField, ENTER_NAME_TEXT),
                adder.newCellSettings().alignHorizontallyCenter()
        );
        CycleButton<WorldCreationUiState.SelectedGameMode> cyclingButtonWidget = adder.addChild(
                CycleButton.<WorldCreationUiState.SelectedGameMode>builder(value -> value.displayName)
                        .withValues(WorldCreationUiState.SelectedGameMode.SURVIVAL, WorldCreationUiState.SelectedGameMode.HARDCORE)
                        .create(0, 0, 210, 20, GAME_MODE_TEXT, (button, value) -> createWorldScreen.getUiState().setGameMode(value)),
                positioner
        );
        createWorldScreen.getUiState().addListener(creator -> {
            cyclingButtonWidget.setValue(creator.getGameMode());
            cyclingButtonWidget.active = !creator.isDebug();
            cyclingButtonWidget.setTooltip(Tooltip.create(creator.getGameMode().getInfo()));
        });
        CycleButton<Difficulty> cyclingButtonWidget2 = adder.addChild(
                CycleButton.<Difficulty>builder(Difficulty::getDisplayName)
                        .withValues(Difficulty.EASY,Difficulty.NORMAL,Difficulty.HARD)
                        .create(0, 0, 210, 20, Component.translatable("options.difficulty"), (button, value) -> createWorldScreen.getUiState().setDifficulty(value)),
                positioner
        );
        createWorldScreen.getUiState().addListener(creator -> {
            cyclingButtonWidget2.setValue(createWorldScreen.getUiState().getDifficulty());
            cyclingButtonWidget2.active = !createWorldScreen.getUiState().isHardcore();
            cyclingButtonWidget2.setTooltip(Tooltip.create(Component.translatable("mod.options.difficulty." + createWorldScreen.getUiState().getDifficulty().getKey() + ".info")));
        });
        CycleButton<Boolean> cyclingButtonWidget3 = adder.addChild(
                CycleButton.onOffBuilder()
                        .withTooltip(value -> Tooltip.create(Component.translatable("selectWorld.allowCommands.info")))
                        .create(0, 0, 210, 20, ALLOW_COMMANDS_TEXT, (button, value) -> createWorldScreen.getUiState().setAllowCommands(value))
        );
        createWorldScreen.getUiState().addListener(creator -> {
            cyclingButtonWidget3.setValue(createWorldScreen.getUiState().isAllowCommands());
            cyclingButtonWidget3.active = false;
        });

    }


}
