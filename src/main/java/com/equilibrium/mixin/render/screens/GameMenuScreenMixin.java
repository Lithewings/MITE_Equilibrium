package com.equilibrium.mixin.render.screens;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }
    int lastInitTime = 0;
    @Override
    public void tick() {
        lastInitTime++;
        super.tick();
    }

    @Override
    public boolean shouldPause() {
        //预留一帧再暂停,让player的tick再运行一帧,以监听玩家是否处于暂停界面中
        return lastInitTime > 0;
    }

}
