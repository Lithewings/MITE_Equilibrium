package com.equilibrium.util;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.Optional;

public class SharedConstant {
    public static Style RED = Style.of(
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

    public static Style YELLOW = Style.of(
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
    public static final Text INVALID_CRAFTING_TEXT = Text.literal("需要更高等级的合成台").setStyle(YELLOW);






}
