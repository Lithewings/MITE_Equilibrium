package com.equilibrium.mixin.player;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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


@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {




    @Inject(method = "onPlayerConnect", at = @At(value = "TAIL"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {

        //获取服务器的所有nbt数据
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(player.getServer());
        if (serverState.onFirstInTheWorld) {
            //只触发一次
            serverState.onFirstInTheWorld = false;
            player.sendMessage(Text.translatable("mod.first_day.helloWorld").formatted(Formatting.YELLOW));
            if(getGameBooleanRuleFromServer(ENABLE_NO_ANIMALS,player.getServer())){
                ItemStack leather = Items.LEATHER.getDefaultStack();
                leather.setCount(16);
                player.getInventory().offerOrDrop(leather);
            }


        }
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 255, false, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 255, false, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 255, false, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 255, false, false, false));

        //游戏规则同步,将服务器上的数据拷贝一份到客户端供使用
        onPlayerConnectSynchronizingGameRulesForBoolean(player);

        if (player.getHealth() <= 1) {
            player.damage(player.getDamageSources().badRespawnPoint(player.getPos()), 114514);
        } else {
            if(getGameBooleanRuleFromServer(ENABLE_MORE_SL_DAMAGE, player.getServerWorld().getServer()))
                player.damage(player.getDamageSources().badRespawnPoint(player.getPos()),Math.max(3,player.getHealth()/2));
            else
                player.damage(player.getDamageSources().badRespawnPoint(player.getPos()), 1);
        }

    }


    @Inject(method = "respawnPlayer", at = @At("TAIL"), cancellable = true)
    public void respawnPlayer(ServerPlayerEntity player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        ServerPlayerEntity serverPlayerEntity = cir.getReturnValue();
        if(serverPlayerEntity.totalExperience==0) {
            serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 2), null);
            serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 1200, 2), null);
            serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 1200), null);
            serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 1200, 2), null);
            cir.setReturnValue(serverPlayerEntity);
        }
    }
}