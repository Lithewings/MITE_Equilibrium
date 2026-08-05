package com.equilibrium.mixin.world;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.getGameBooleanRuleFromServer;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.ENABLE_MORE_RAIN_WEATHER;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin extends Level {


    @Shadow
    public abstract @NotNull MinecraftServer getServer();

    protected ServerWorldMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

//    this (index 0)
//
//    bl (index 1) - 保存 isRaining() 结果
//
//    i (index 2) - 即 clearWeatherTime
//
//    j (index 3) - 即 thunderTime
//
//    k (index 4) - 即 rainTime
//
//    bl2 (index 5) - isThundering()
//
//    bl3 (index 6) - isRaining() 从属性获取

//    CLEAR_WEATHER_DURATION_PROVIDER	晴天等待时间	12000 ~ 180000	10 分钟 ~ 2.5 小时	当世界处于晴天时，此值决定距离下一次下雨还要等多久。
//    RAIN_WEATHER_DURATION_PROVIDER	雨天持续时间	12000 ~ 24000	10 分钟 ~ 20 分钟	当世界开始下雨时，此值决定这场雨会持续多久。
//    CLEAR_THUNDER_WEATHER_DURATION_PROVIDER	无雷雨等待时间	12000 ~ 180000	10 分钟 ~ 2.5 小时	当世界没有雷雨时，此值决定距离下一次自然触发雷雨还要等多久。（注意：下雨是雷雨的前提，必须先下雨才有可能打雷。）
//    THUNDER_WEATHER_DURATION_PROVIDER	雷雨持续时间	3600 ~ 15600	3 分钟 ~ 13 分钟	当雷雨被触发时，此值决定这场雷雨会持续多久。



    @Unique
    private static final IntProvider NEW_CLEAR_WEATHER_DURATION_PROVIDER = UniformInt.of(12000, 24000);
    @Unique
    private static final IntProvider NEW_RAIN_WEATHER_DURATION_PROVIDER = UniformInt.of(24000*3, 24000*7);
    @Unique
    private static final IntProvider NEW_CLEAR_THUNDER_WEATHER_DURATION_PROVIDER = UniformInt.of(12000, 180000);
    @Unique
    private static final IntProvider NEW_THUNDER_WEATHER_DURATION_PROVIDER = UniformInt.of(3600, 15600);







    @Unique
    private boolean isOnRainyReason() {
        return getGameBooleanRuleFromServer(ENABLE_MORE_RAIN_WEATHER, this.getServer());
    }


    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private ServerLevelData serverLevelData;

    @Inject(method = "advanceWeatherCycle", at = @At("HEAD"), cancellable = true)
    private void tickWeather(CallbackInfo ci) {
        if (this.isOnRainyReason()) {
            //自定义逻辑
            boolean bl = this.isRaining();
            if (this.dimensionType().hasSkyLight()) {
                if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                    int i = this.serverLevelData.getClearWeatherTime();
                    int j = this.serverLevelData.getThunderTime();
                    int k = this.serverLevelData.getRainTime();
                    boolean bl2 = this.levelData.isThundering();
                    boolean bl3 = this.levelData.isRaining();
                    if (i > 0) {
                        i--;
                        j = bl2 ? 0 : 1;
                        k = bl3 ? 0 : 1;
                        bl2 = false;
                        bl3 = false;
                    } else {
                        if (j > 0) {
                            if (--j == 0) {
                                bl2 = !bl2;
                            }
                        } else if (bl2) {
                            j = NEW_THUNDER_WEATHER_DURATION_PROVIDER.sample(this.random);
                        } else {
                            j = NEW_CLEAR_THUNDER_WEATHER_DURATION_PROVIDER.sample(this.random);
                        }

                        if (k > 0) {
                            if (--k == 0) {
                                bl3 = !bl3;
                            }
                        } else if (bl3) {
                            k = NEW_RAIN_WEATHER_DURATION_PROVIDER.sample(this.random);
                        } else {
                            k = NEW_CLEAR_WEATHER_DURATION_PROVIDER.sample(this.random);
                        }
                    }

                    this.serverLevelData.setThunderTime(j);
                    this.serverLevelData.setRainTime(k);
                    this.serverLevelData.setClearWeatherTime(i);
                    this.serverLevelData.setThundering(bl2);
                    this.serverLevelData.setRaining(bl3);
                }

                this.oThunderLevel = this.thunderLevel;
                if (this.levelData.isThundering()) {
                    this.thunderLevel += 0.01F;
                } else {
                    this.thunderLevel -= 0.01F;
                }

                this.thunderLevel = Mth.clamp(this.thunderLevel, 0.0F, 1.0F);
                this.oRainLevel = this.rainLevel;
                if (this.levelData.isRaining()) {
                    this.rainLevel += 0.01F;
                } else {
                    this.rainLevel -= 0.01F;
                }

                this.rainLevel = Mth.clamp(this.rainLevel, 0.0F, 1.0F);
            }

            if (this.oRainLevel != this.rainLevel) {
                this.server
                        .getPlayerList()
                        .broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
            }

            if (this.oThunderLevel != this.thunderLevel) {
                this.server
                        .getPlayerList()
                        .broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
            }

            if (bl != this.isRaining()) {
                if (bl) {
                    this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, ClientboundGameEventPacket.DEMO_PARAM_INTRO));
                } else {
                    this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, ClientboundGameEventPacket.DEMO_PARAM_INTRO));
                }

                this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel));
                this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel));
            }
            ci.cancel();
        }
    }





    /**
     * 拦截睡眠后设置时间的调用，强制将时间设为 23000（黎明前）
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V"
            )
    )
    private void redirectSetTimeOfDay(ServerLevel world, long time) {
        // 忽略计算出的原始时间（通常为 0 或 24000），直接设为 23000
        world.setDayTime(time - time  % 24000L + BiasedToBottomInt.of(-1500,3000).sample(world.random));

    }

    /**
     * 完全跳过 resetWeather() 的调用
     */
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;resetWeatherCycle()V"
            )
    )
    private void cancelResetWeather(ServerLevel world) {
        // 空实现，什么也不做
    }
}





