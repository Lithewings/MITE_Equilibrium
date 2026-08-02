package com.equilibrium.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.Optional;

public class SharedConstant {

    public static Style RED = Style.create(
            Optional.of(TextColor.fromRgb(16733525)),
            Optional.of(false),
            Optional.of(false),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    public static Style YELLOW = Style.create(
            Optional.of(TextColor.fromRgb(16777045)),
            Optional.of(false),
            Optional.of(false),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());
    Component text = Component.translatable("info.need.advance.craftingtable");

    public static final Component INVALID_CRAFTING_TEXT = Component.literal("需要更高等级的合成台").setStyle(YELLOW);






}
