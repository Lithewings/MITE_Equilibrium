package com.equilibrium.mixin.crafttime;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeShownListener;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.network.C2STriggerContentChangePacket.sendTrigger;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookMixin implements PlaceRecipe<Ingredient>, Renderable, GuiEventListener, NarratableEntry, RecipeShownListener {
    @Inject(method = "updateCollections",at = @At("HEAD"))
    private void refreshResults(boolean resetCurrentPage, CallbackInfo ci) {
        sendTrigger();
    }

}
