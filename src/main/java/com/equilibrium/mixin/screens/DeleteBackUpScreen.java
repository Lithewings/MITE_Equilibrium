package com.equilibrium.mixin.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldOpenFlows.class)
public abstract class DeleteBackUpScreen {
    @Inject(at = @At("HEAD"), method = "askForBackup", cancellable = true)
    public void showBackupPrompt(LevelStorageSource.LevelStorageAccess session, boolean customized, Runnable callback, Runnable onCancel, CallbackInfo ci) {
        if (customized) return;
        ci.cancel();
        Minecraft.getInstance().execute(callback);
    }
}