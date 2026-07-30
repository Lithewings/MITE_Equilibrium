package com.equilibrium.difficulty_entry;

import com.equilibrium.OnServerInitialize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;

import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ALL_EXTRA_ENTRY_KEYS;

public class DifficultyEntryGetter {




    //T为继承至Rule类的子类,有BooleanRules类型或IntegerRules类型

    public static <T extends GameRules.Value<T>> T getGameRuleInstanceFromClient(GameRules.Key<T> key){

        if (Minecraft.getInstance().level instanceof ClientLevel clientWorld) {
            return clientWorld.getGameRules().getRule(key);
        }
        OnServerInitialize.LOGGER.info("Maybe the clientWorld is initializing, please wait.");
        return null;
    }

    //引入了同步机制,所以如果某些位置想获得server的游戏规则上下文时,用client上下文也可以拿到:
    public static boolean getGameBooleanRuleFromClient(GameRules.Key<GameRules.BooleanValue> key){
        GameRules.BooleanValue rule = getGameRuleInstanceFromClient(key);
        if(rule==null)
            return false;
        return rule.get();
    }
    //但首选检查serverWorld中的游戏规则
    public static boolean getGameBooleanRuleFromServer(GameRules.Key<GameRules.BooleanValue> key, MinecraftServer server){
        GameRules.BooleanValue rule = server.getGameRules().getRule(key);
        return rule.get();
    }
    //如果上下文均不能获取到客户端和服务端信息,那就不要引入这个机制


    public static boolean isAnyExtraEntryExisting(MinecraftServer server, @Nullable ServerPlayer player){
        int entryNumber = 0;
        for (GameRules.Key<GameRules.BooleanValue> booleanRuleKey : ALL_EXTRA_ENTRY_KEYS) {
            if (getGameBooleanRuleFromServer(booleanRuleKey, server)) {
                entryNumber++;
            }
        }
        if(player!=null){
            player.sendSystemMessage(Component.translatable("difficulty.extra.entry.message2", entryNumber));
        }
        return entryNumber>0;
    }





}
