package com.equilibrium.mixin.screens;

import com.equilibrium.server_and_client.client.NewGameTab;
import com.equilibrium.server_and_client.client.NewWorldTab;
import com.equilibrium.util.BooleanStorageUtil;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLPaths;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreateWorldScreen.class)
public abstract class CreativeWorldScreenMixin extends Screen {

    protected CreativeWorldScreenMixin(Component title) {
        super(title);
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;addTabs([Lnet/minecraft/client/gui/components/tabs/Tab;)Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;"
            )
    )
    public Tab[] init(Tab[] tabs) {
        if (!BooleanStorageUtil.loadFinishGameOnce(
                FMLPaths.CONFIGDIR.get().resolve(BooleanStorageUtil.FINISH_GAME_ONCE).toString()
        )) {
            tabs[0] = new NewGameTab((CreateWorldScreen) (Object) this, this.font);
            tabs[1] = new NewWorldTab((CreateWorldScreen) (Object) this, this.font);
            return tabs;
        } else {
            return tabs;
        }
    }
}