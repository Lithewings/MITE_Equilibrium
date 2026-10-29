package com.equilibrium.difficulty_entry_screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;


public final class DifficultyEntryScreenRegistrations {

    public static final Identifier SCREEN_ID = Identifier.of(MOD_ID, "difficulty_entry");


    public static final ScreenHandlerType<DifficultyEntryScreenHandler> SCREEN_TYPE =
            new ScreenHandlerType<>(DifficultyEntryScreenHandler::new, FeatureSet.empty());



    private DifficultyEntryScreenRegistrations() {
    }


    public static void initGameScreenOnServer() {
        Registry.register(Registries.SCREEN_HANDLER, SCREEN_ID, SCREEN_TYPE);
    }

    @Environment(EnvType.CLIENT)
    public static void initGameScreenOnClient() {
        HandledScreens.<DifficultyEntryScreenHandler, DifficultyEntryScreen>register(SCREEN_TYPE, DifficultyEntryScreen::new);
    }
}
