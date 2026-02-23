package com.equilibrium.mixin.render.screens;

import com.equilibrium.client.NewGameTab;
import com.equilibrium.client.NewWorldTab;
import com.equilibrium.util.BooleanStorageUtil;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.*;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.Text;
import net.minecraft.util.path.SymlinkFinder;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;


@Mixin(CreateWorldScreen.class)

public abstract class CreativeWorldScreenMixin extends Screen {
    @Unique
    private final String fileName = "Finish The Game Once.dat";
    @Unique
    private final Path configPath = FabricLoader.getInstance().getConfigDir().normalize().resolve(fileName);
    protected CreativeWorldScreenMixin(Text title) {
        super(title);
    }

    @ModifyArg(method = "init",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TabNavigationWidget$Builder;tabs([Lnet/minecraft/client/gui/tab/Tab;)Lnet/minecraft/client/gui/widget/TabNavigationWidget$Builder;"))
    public Tab[] init(Tab[] tabs) {
        if (!BooleanStorageUtil.load(configPath.toString(), false)) {
            tabs[0] = new NewGameTab(((CreateWorldScreen)(Object)this),this.textRenderer);
            tabs[1] = new NewWorldTab(((CreateWorldScreen)(Object)this),this.textRenderer);
            return tabs;
        }
       else return tabs;
    }
}