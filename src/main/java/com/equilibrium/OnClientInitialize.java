package com.equilibrium;

import com.equilibrium.network.S2CGameRuleSyncPayloadForBooleanPacket;
import com.equilibrium.network.S2CIllnessTextureBooleanPacket;
import com.equilibrium.network.S2CStockChangeGrassColorPacket;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;


public class OnClientInitialize implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        //S->C,发包、接收
        S2CStockChangeGrassColorPacket.registerOnClient();
        S2CIllnessTextureBooleanPacket.registerOnClient();
        S2CGameRuleSyncPayloadForBooleanPacket.registerOnClient();

    }
}
