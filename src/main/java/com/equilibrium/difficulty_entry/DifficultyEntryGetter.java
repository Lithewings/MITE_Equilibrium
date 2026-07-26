package com.equilibrium.difficulty_entry;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.util.BooleanStorageUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ALL_EXTRA_ENTRY_KEYS;
import static com.equilibrium.util.BooleanStorageUtil.loadWorldInformation;
import static net.minecraft.world.World.OVERWORLD;

public class DifficultyEntryGetter {




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


    public static boolean isAnyExtraEntryExisting(MinecraftServer server, @Nullable ServerPlayerEntity player){
        int entryNumber = 0;
        for (GameRules.Key<GameRules.BooleanRule> booleanRuleKey : ALL_EXTRA_ENTRY_KEYS) {
            if (getGameBooleanRuleFromServer(booleanRuleKey, server)) {
                entryNumber++;
            }
        }
        if(player!=null){
            player.sendMessage(Text.translatable("difficulty.extra.entry.message2", entryNumber));
        }
        return entryNumber>0;
    }





}
