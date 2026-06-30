package com.equilibrium.mixin;

import com.equilibrium.block.anvil_block.adamantium_anvil_block.AdamantiumScreenHandler;
import com.equilibrium.block.anvil_block.iron_anvil_block.IronAnvilScreenHandler;
import com.equilibrium.block.anvil_block.mithril_anvil_block.MithrilAnvilScreenHandler;
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
            switch (this.player.currentScreenHandler) {
                case AnvilScreenHandler anvilScreenHandler -> {
                    if (!anvilScreenHandler.canUse(this.player)) {
                        LOGGER.debug("Player {} interacted with invalid menu {}", this.player, anvilScreenHandler);
                        return;
                    }
                    anvilScreenHandler.setNewItemName(packet.getName());
                }
                case IronAnvilScreenHandler ironAnvilScreenHandler -> {
                    if (!ironAnvilScreenHandler.canUse(this.player)) {
                        LOGGER.debug("Player {} interacted with invalid menu {}", this.player, ironAnvilScreenHandler);
                        return;
                    }
                    ironAnvilScreenHandler.setNewItemName(packet.getName());
                }
                case MithrilAnvilScreenHandler mithrilAnvilScreenHandler -> {
                    if (!mithrilAnvilScreenHandler.canUse(this.player)) {
                        LOGGER.debug("Player {} interacted with invalid menu {}", this.player, mithrilAnvilScreenHandler);
                        return;
                    }
                    mithrilAnvilScreenHandler.setNewItemName(packet.getName());
                }
                case AdamantiumScreenHandler adamantiumScreenHandler -> {
                    if (!adamantiumScreenHandler.canUse(this.player)) {
                        LOGGER.debug("Player {} interacted with invalid menu {}", this.player, adamantiumScreenHandler);
                        return;
                    }
                    adamantiumScreenHandler.setNewItemName(packet.getName());
                }
                default -> {
                }
            }
        }
    }
}
