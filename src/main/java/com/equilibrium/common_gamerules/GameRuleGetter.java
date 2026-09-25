package com.equilibrium.common_gamerules;

import com.equilibrium.OnServerInitialize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;

public class GameRuleGetter {
    public static boolean booleanGameRuleGetterFromServer(MinecraftServer server, GameRules.Key<GameRules.BooleanValue> key) {
        GameRules.BooleanValue rule = server.getGameRules().getRule(key);
        return rule.get();
    }

    public static boolean booleanGameRuleGetterFromClient(GameRules.Key<GameRules.BooleanValue> key) {
        if (Minecraft.getInstance().level instanceof ClientLevel clientWorld) {
            GameRules.BooleanValue rule = clientWorld.getGameRules().getRule(key);
            if (rule == null)
                return false;
            return rule.get();
        }
        return false;
    }
    public static boolean booleanGameRuleGetterFromClient(@NotNull ClientLevel clientWorld, GameRules.Key<GameRules.BooleanValue> key) {
        GameRules.BooleanValue rule = clientWorld.getGameRules().getRule(key);
        if (rule == null)
            return false;
        return rule.get();

    }


}
