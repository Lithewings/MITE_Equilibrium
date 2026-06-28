package com.equilibrium.mixin;

import com.equilibrium.block.anvil_block.IronAnvilBlock.IronAnvilScreenHandler;
import com.equilibrium.mixin.player.ServerPlayerEntityMixin;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayerRenamePacketMixin extends ServerCommonNetworkHandler
        implements ServerPlayPacketListener,
        PlayerAssociatedNetworkHandler,
        TickablePacketListener {
    public ServerPlayerRenamePacketMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onRenameItem",at = @At("HEAD"), cancellable = true)
    public void onRenameItem(RenameItemC2SPacket packet, CallbackInfo ci) {
        ci.cancel();
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());


        if (this.player.currentScreenHandler instanceof ForgingScreenHandler) {
            if(this.player.currentScreenHandler instanceof AnvilScreenHandler anvilScreenHandler){
                if (!anvilScreenHandler.canUse(this.player)) {
                    LOGGER.debug("Player {} interacted with invalid menu {}", this.player, anvilScreenHandler);
                    return;
                }
                anvilScreenHandler.setNewItemName(packet.getName());
            }
            else if(this.player.currentScreenHandler instanceof IronAnvilScreenHandler ironAnvilScreenHandler){
                if (!ironAnvilScreenHandler.canUse(this.player)) {
                    LOGGER.debug("Player {} interacted with invalid menu {}", this.player, ironAnvilScreenHandler);
                    return;
                }
                ironAnvilScreenHandler.setNewItemName(packet.getName());
            }
        }
    }
}
