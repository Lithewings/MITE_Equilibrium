package com.equilibrium.difficulty_entry_screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;

import static com.equilibrium.OnServerInitialize.MOD_ID;


public final class DifficultyEntryScreenRegistrations {

    public static final ResourceLocation SCREEN_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "difficulty_entries");


    public static final MenuType<DifficultyEntryScreenHandler> SCREEN_TYPE =
            new MenuType<>(DifficultyEntryScreenHandler::new, FeatureFlagSet.of());



    private DifficultyEntryScreenRegistrations() {
    }


    public static void initGameScreenOnServer() {
        Registry.register(BuiltInRegistries.MENU, SCREEN_ID, SCREEN_TYPE);
    }

    @Environment(EnvType.CLIENT)
    public static void initGameScreenOnClient() {
        MenuScreens.<DifficultyEntryScreenHandler, DifficultyEntryScreen>register(SCREEN_TYPE, DifficultyEntryScreen::new);
    }
}
