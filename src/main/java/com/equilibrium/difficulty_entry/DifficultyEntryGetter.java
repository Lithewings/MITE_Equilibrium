package com.equilibrium.difficulty_entry;

import com.equilibrium.OnServerInitialize;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class DifficultyEntryGetter {



    //默认均从客户端世界的读取值
    //T为继承至Rule类的子类,有BooleanRules类型或IntegerRules类型

    public static <T extends GameRules.Rule<T>> T getGameRuleInstanceFromClient(GameRules.Key<T> key){

        if (MinecraftClient.getInstance().world instanceof ClientWorld clientWorld) {
            return clientWorld.getGameRules().get(key);
        }
        OnServerInitialize.LOGGER.info("Maybe the clientWorld is initializing, please wait.");
        return null;
    }

    //引入了同步机制,所以如果某些位置想获得server的游戏规则上下文时,用client上下文也可以拿到:
    public static boolean getGameBooleanRuleFromClient(GameRules.Key<GameRules.BooleanRule> key){
        GameRules.BooleanRule rule = getGameRuleInstanceFromClient(key);
        if(rule==null)
            return false;
        return rule.get();
    }
    //但首选检查serverWorld中的游戏规则
    public static boolean getGameBooleanRuleFromServer(GameRules.Key<GameRules.BooleanRule> key, MinecraftServer server){
        GameRules.BooleanRule rule = server.getGameRules().get(key);
        return rule.get();
    }
    //如果上下文均不能获取到客户端和服务端信息,那就不要引入这个机制

    //如果有world的上下文,就用world
    public static boolean getGameBooleanRuleFromWorld(GameRules.Key<GameRules.BooleanRule> key, World world){
        GameRules.BooleanRule rule = world.getGameRules().get(key);
        return rule.get();
    }
}
