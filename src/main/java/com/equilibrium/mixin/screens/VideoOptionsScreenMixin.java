package com.equilibrium.mixin.screens;

import com.equilibrium.common_gamerules.GameRuleGetter;
import com.equilibrium.server_and_client.fog_weather_event.IsFogWeatherSuitableUtil;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.OnServerInitialize.MOD_ID;
import static com.equilibrium.common_gamerules.GameRuleRegister.IS_FOG_WEATHER_NOW;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoOptionsScreenMixin extends OptionsSubScreen {
    public VideoOptionsScreenMixin(Screen parent, Options gameOptions, Component title) {
        super(parent, gameOptions, title);
    }



    @Inject(method = "addOptions", at = @At("RETURN"))
    private void disableViewDistanceOption(CallbackInfo ci) {
        OptionInstance<Integer> viewDistanceOption = this.options.renderDistance();
        // 获取对应的 widget
        LayoutElement widget = this.list.findOption(viewDistanceOption);
        if (widget instanceof AbstractWidget clickableWidget) {
            if(minecraft!=null && minecraft.level!=null){

                boolean isRightDimension = IsFogWeatherSuitableUtil.isValid(minecraft.level);
                if(isRightDimension && GameRuleGetter.booleanGameRuleGetterFromClient(IS_FOG_WEATHER_NOW)==true)
                    clickableWidget.active = false;
            }

        }
    }
}
