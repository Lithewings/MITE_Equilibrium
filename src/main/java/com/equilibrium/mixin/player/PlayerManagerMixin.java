package com.equilibrium.mixin.player;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_MORE_SL_DAMAGE;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_NO_ANIMALS;
import static com.equilibrium.difficulty_entry.DifficultyEntryUtil.onPlayerConnectSynchronizingGameRulesForBoolean;


@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin {




    @Inject(method = "placeNewPlayer", at = @At(value = "TAIL"))
    public void onPlayerConnect(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {

        //获取服务器的所有nbt数据
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(player.getServer());
        if (serverState.onFirstInTheWorld) {
            //只触发一次
            serverState.onFirstInTheWorld = false;
            player.sendSystemMessage(Component.translatable("mod.first_day.helloWorld").withStyle(ChatFormatting.YELLOW));
            if(getGameBooleanRuleFromServer(ENABLE_NO_ANIMALS,player.getServer())){
                ItemStack leather = Items.LEATHER.getDefaultInstance();
                leather.setCount(16);
                player.getInventory().placeItemBackInInventory(leather);
            }


        }
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 255, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 255, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 255, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 255, false, false, false));

        //游戏规则同步,将服务器上的数据拷贝一份到客户端供使用
        onPlayerConnectSynchronizingGameRulesForBoolean(player);

        if (player.getHealth() <= 1) {
            player.hurt(player.damageSources().badRespawnPointExplosion(player.position()), 200);
        } else {
            if(getGameBooleanRuleFromServer(ENABLE_MORE_SL_DAMAGE, player.serverLevel().getServer())) {
                player.setHealth(player.getHealth()/2);
                player.hurt(player.damageSources().badRespawnPointExplosion(player.position()), player.getHealth()>=3?0:200);
            }
            else {
                player.setHealth(player.getHealth()-1);
                player.hurt(player.damageSources().badRespawnPointExplosion(player.position()), 0);
            }
        }

    }


    @Inject(method = "respawn", at = @At("TAIL"), cancellable = true)
    public void respawnPlayer(ServerPlayer player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer serverPlayerEntity = cir.getReturnValue();
        if(serverPlayerEntity.totalExperience==0) {
            serverPlayerEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 2), null);
            serverPlayerEntity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 1200, 2), null);
            serverPlayerEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200), null);
            serverPlayerEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 2), null);
            cir.setReturnValue(serverPlayerEntity);
        }
    }
}