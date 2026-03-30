package com.equilibrium.server_and_client.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.Difficulty;

import java.nio.file.Path;
import java.util.List;

public class NewGameTab extends GridScreenTab {
    private static final Text GAME_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.game.title");
    private static final Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands.new");
    private final TextFieldWidget worldNameField;

    static final Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
    static final Text ENTER_NAME_TEXT = Text.translatable("selectWorld.enterName");




    public NewGameTab(CreateWorldScreen createWorldScreen, TextRenderer textRenderer) {
        super(GAME_TAB_TITLE_TEXT);

        GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
        Positioner positioner = adder.copyPositioner();
        this.worldNameField = new TextFieldWidget(textRenderer, 208, 20, Text.translatable("selectWorld.enterName"));
        this.worldNameField.setText(createWorldScreen.worldCreator.getWorldName());
        this.worldNameField.setChangedListener(createWorldScreen.worldCreator::setWorldName);
        createWorldScreen.worldCreator
                .addListener(
                        creator -> this.worldNameField
                                .setTooltip(Tooltip.of(Text.translatable("selectWorld.targetFolder", Text.literal(creator.getWorldDirectoryName()).formatted(Formatting.ITALIC))))
                );
        createWorldScreen.setInitialFocus(this.worldNameField);
        adder.add(
                LayoutWidgets.createLabeledWidget(textRenderer, this.worldNameField, ENTER_NAME_TEXT),
                adder.copyPositioner().alignHorizontalCenter()
        );
        CyclingButtonWidget<WorldCreator.Mode> cyclingButtonWidget = adder.add(
                CyclingButtonWidget.<WorldCreator.Mode>builder(value -> value.name)
                        .values(WorldCreator.Mode.SURVIVAL, WorldCreator.Mode.HARDCORE)
                        .build(0, 0, 210, 20, GAME_MODE_TEXT, (button, value) -> createWorldScreen.worldCreator.setGameMode(value)),
                positioner
        );
        createWorldScreen.worldCreator.addListener(creator -> {
            cyclingButtonWidget.setValue(creator.getGameMode());
            cyclingButtonWidget.active = !creator.isDebug();
            cyclingButtonWidget.setTooltip(Tooltip.of(creator.getGameMode().getInfo()));
        });
        CyclingButtonWidget<Difficulty> cyclingButtonWidget2 = adder.add(
                CyclingButtonWidget.<Difficulty>builder(Difficulty::getTranslatableName)
                        .values(Difficulty.EASY,Difficulty.NORMAL,Difficulty.HARD)
                        .build(0, 0, 210, 20, Text.translatable("options.difficulty"), (button, value) -> createWorldScreen.worldCreator.setDifficulty(value)),
                positioner
        );
        createWorldScreen.worldCreator.addListener(creator -> {
            cyclingButtonWidget2.setValue(createWorldScreen.worldCreator.getDifficulty());
            cyclingButtonWidget2.active = !createWorldScreen.worldCreator.isHardcore();
            cyclingButtonWidget2.setTooltip(Tooltip.of(Text.translatable("mod.options.difficulty." + createWorldScreen.worldCreator.getDifficulty().getName() + ".info")));
        });
        CyclingButtonWidget<Boolean> cyclingButtonWidget3 = adder.add(
                CyclingButtonWidget.onOffBuilder()
                        .tooltip(value -> Tooltip.of(CreateWorldScreen.ALLOW_COMMANDS_INFO_TEXT))
                        .build(0, 0, 210, 20, ALLOW_COMMANDS_TEXT, (button, value) -> createWorldScreen.worldCreator.setCheatsEnabled(value))
        );
        createWorldScreen.worldCreator.addListener(creator -> {
            cyclingButtonWidget3.setValue(createWorldScreen.worldCreator.areCheatsEnabled());
            cyclingButtonWidget3.active = false;
        });

    }


}
