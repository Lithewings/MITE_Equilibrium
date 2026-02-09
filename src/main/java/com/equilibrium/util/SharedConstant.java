package com.equilibrium.util;

import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

import java.util.Optional;

public class SharedConstant {
    public static Style RED_BOLD= Style.of(
            Optional.of(TextColor.fromRgb(16733525)),
            Optional.of(true),
            Optional.of(false),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    public static Style YELLOW_BOLD= Style.of(
            Optional.of(TextColor.fromRgb(16777045)),
            Optional.of(true),
            Optional.of(false),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

}
