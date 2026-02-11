package com.equilibrium.mixin.crafttime;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeDisplayListener;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

import static com.equilibrium.network.C2STriggerContentChangePacket.sendTrigger;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookMixin implements RecipeGridAligner<Ingredient>, Drawable, Element, Selectable, RecipeDisplayListener {
    @Inject(method = "refreshResults",at = @At("HEAD"))
    private void refreshResults(boolean resetCurrentPage, CallbackInfo ci) {
        sendTrigger();
    }

}
