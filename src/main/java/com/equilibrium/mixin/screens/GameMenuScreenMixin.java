package com.equilibrium.mixin.screens;

import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PauseScreen.class)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Component title) {
        super(title);
    }
    int lastInitTime = 0;
    @Override
    public void tick() {
        lastInitTime++;
        super.tick();
    }

    @Override
    public boolean isPauseScreen() {
        //预留一帧再暂停,让player的tick再运行一帧,以监听玩家是否处于暂停界面中
        return lastInitTime > 0;
    }

}
