package com.equilibrium.mixin.tables;

import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.*;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

//    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
//    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci){
//        player.sendMessage(Text.of(MITEequilibrium.MOD_ID).copy().append(Text.translatable("crafttime.join_info").formatted(Formatting.YELLOW)));
//    }
}