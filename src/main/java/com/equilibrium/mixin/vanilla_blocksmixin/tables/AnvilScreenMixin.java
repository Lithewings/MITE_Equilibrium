package com.equilibrium.mixin.vanilla_blocksmixin.tables;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> { @Shadow protected abstract void setup();

    public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @ModifyArg(method = "drawForeground",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"),index = 1)
    protected Text drawForeground(Text text) {
        int i = this.handler.getLevelCost();
        return Text.of( Text.translatable("container.repair.require").getString()+": "+i);
    }


    @Redirect(method = "drawForeground", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z",opcode = Opcodes.GETFIELD))
    //当作是创造模式,即便大于39级也可以继续打造,而不会过于昂贵,但如果实际经验值不够,仍然拿不出来是红字提示
    protected boolean drawForeground(PlayerAbilities instance) {
        return  true;
    }




}
