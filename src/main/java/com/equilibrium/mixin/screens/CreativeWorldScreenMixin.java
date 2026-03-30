package com.equilibrium.mixin.screens;

import com.equilibrium.server_and_client.client.NewGameTab;
import com.equilibrium.server_and_client.client.NewWorldTab;
import com.equilibrium.util.BooleanStorageUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.nio.file.Path;


@Mixin(CreateWorldScreen.class)

public abstract class CreativeWorldScreenMixin extends Screen {


    protected CreativeWorldScreenMixin(Text title) {
        super(title);
    }

    @ModifyArg(method = "init",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TabNavigationWidget$Builder;tabs([Lnet/minecraft/client/gui/tab/Tab;)Lnet/minecraft/client/gui/widget/TabNavigationWidget$Builder;"))
    public Tab[] init(Tab[] tabs) {

        if (!BooleanStorageUtil.loadFinishGameOnce(FabricLoader.getInstance().getConfigDir().normalize().resolve(BooleanStorageUtil.FINISH_GAME_ONCE).toString())) {
            tabs[0] = new NewGameTab(((CreateWorldScreen)(Object)this),this.textRenderer);
            tabs[1] = new NewWorldTab(((CreateWorldScreen)(Object)this),this.textRenderer);
            return tabs;
        }
       else return tabs;
    }
}