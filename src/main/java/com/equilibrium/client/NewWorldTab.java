package com.equilibrium.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class NewWorldTab extends GridScreenTab {
    private static final Text WORLD_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.world.title");
    private static final Text AMPLIFIED_GENERATOR_INFO_TEXT = Text.translatable("generator.minecraft.amplified.info");
    private static final Text MAP_FEATURES_TEXT = Text.translatable("selectWorld.mapFeatures");
    private static final Text MAP_FEATURES_INFO_TEXT = Text.translatable("mod.selectWorld.mapFeatures.info");
    private static final Text BONUS_ITEMS_TEXT = Text.translatable("selectWorld.bonusItems");
    private static final Text BONUS_ITEMS_INFO_TEXT = Text.translatable("mod.selectWorld.bonusItems");

    private static final Text ENTER_SEED_TEXT = Text.translatable("selectWorld.enterSeed");
    static final Text SEED_INFO_TEXT = Text.translatable("mod.selectWorld.seedInfo").formatted(Formatting.DARK_GRAY);
    private static final int field_42190 = 310;
    private final TextFieldWidget seedField;
    private final ButtonWidget customizeButton;


    public static boolean alwaysTrue() {
        return true;
    }
    public static boolean alwaysFalse() {
        return false;
    }




    public NewWorldTab(CreateWorldScreen createWorldScreen, TextRenderer textRenderer) {
        super(WORLD_TAB_TITLE_TEXT);
        GridWidget.Adder adder = this.grid.setColumnSpacing(10).setRowSpacing(8).createAdder(2);
        CyclingButtonWidget<WorldCreator.WorldType> cyclingButtonWidget = adder.add(
                CyclingButtonWidget.<WorldCreator.WorldType>builder(WorldCreator.WorldType::getName)
                        .values(createWorldScreen.worldCreator.getNormalWorldTypes().getFirst())
                        .build(0, 0, 150, 20, Text.translatable("selectWorld.mapType"), (button, worldType) -> createWorldScreen.worldCreator.setWorldType(worldType))
        );
        cyclingButtonWidget.setValue(createWorldScreen.worldCreator.getWorldType());

        cyclingButtonWidget.active=false;
        this.customizeButton = adder.add(ButtonWidget.builder(Text.translatable("selectWorld.customizeType"), button -> this.openCustomizeScreen(createWorldScreen)).build());
        createWorldScreen.worldCreator.addListener(creator -> this.customizeButton.active = !creator.isDebug() && creator.getLevelScreenProvider() != null);
        this.seedField = new TextFieldWidget(textRenderer, 308, 20, Text.translatable("selectWorld.enterSeed")) {
            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(ScreenTexts.SENTENCE_SEPARATOR).append(SEED_INFO_TEXT);
            }
        };
        this.seedField.setPlaceholder(SEED_INFO_TEXT);
        this.seedField.setText(createWorldScreen.worldCreator.getSeed());
        this.seedField.setChangedListener(seed -> createWorldScreen.worldCreator.setSeed(this.seedField.getText()));
        this.seedField.active=false;

        adder.add(LayoutWidgets.createLabeledWidget(textRenderer, this.seedField, ENTER_SEED_TEXT), 2);
        WorldScreenOptionGrid.Builder builder = WorldScreenOptionGrid.builder(310);
        //我可以保证这个屏幕只会在非调试模式下使用,奖励箱无论何时一定生成;结构也一定会生成,默认在所有模式下都生成结构,调式模式下不调用这个屏幕
        builder.add(MAP_FEATURES_TEXT, createWorldScreen.worldCreator::shouldGenerateStructures, createWorldScreen.worldCreator::setGenerateStructures)
                .toggleable(()->false)
                .tooltip(MAP_FEATURES_INFO_TEXT);
        builder.add(BONUS_ITEMS_TEXT, NewWorldTab::alwaysTrue, null)
                .toggleable(() -> false)
                .tooltip(BONUS_ITEMS_INFO_TEXT);
        createWorldScreen.worldCreator.setBonusChestEnabled(true);
        WorldScreenOptionGrid worldScreenOptionGrid = builder.build(widget ->
            adder.add(widget, 2)
        );




        createWorldScreen.worldCreator.addListener(creator -> worldScreenOptionGrid.refresh());


    }

    private void openCustomizeScreen(CreateWorldScreen createWorldScreen) {
        LevelScreenProvider levelScreenProvider = createWorldScreen.worldCreator.getLevelScreenProvider();
        if (levelScreenProvider != null) {
            createWorldScreen.client
                    .setScreen(levelScreenProvider.createEditScreen(createWorldScreen, createWorldScreen.worldCreator.getGeneratorOptionsHolder()));
        }
    }

}