package com.equilibrium.mixin.world;

import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameRules;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
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

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {


    @Shadow
    public abstract @NotNull MinecraftServer getServer();

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
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
    private static final IntProvider NEW_CLEAR_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(12000, 24000);
    @Unique
    private static final IntProvider NEW_RAIN_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(24000*3, 24000*7);
    @Unique
    private static final IntProvider NEW_CLEAR_THUNDER_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(12000, 180000);
    @Unique
    private static final IntProvider NEW_THUNDER_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(3600, 15600);







    @Unique
    private boolean isOnRainyReason() {
        return getGameBooleanRuleFromServer(ENABLE_MORE_RAIN_WEATHER, this.getServer());
    }


    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private ServerWorldProperties worldProperties;

    @Inject(method = "tickWeather", at = @At("HEAD"), cancellable = true)
    private void tickWeather(CallbackInfo ci) {
        if (this.isOnRainyReason()) {
            //自定义逻辑
            boolean bl = this.isRaining();
            if (this.getDimension().hasSkyLight()) {
                if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
                    int i = this.worldProperties.getClearWeatherTime();
                    int j = this.worldProperties.getThunderTime();
                    int k = this.worldProperties.getRainTime();
                    boolean bl2 = this.properties.isThundering();
                    boolean bl3 = this.properties.isRaining();
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
                            j = NEW_THUNDER_WEATHER_DURATION_PROVIDER.get(this.random);
                        } else {
                            j = NEW_CLEAR_THUNDER_WEATHER_DURATION_PROVIDER.get(this.random);
                        }

                        if (k > 0) {
                            if (--k == 0) {
                                bl3 = !bl3;
                            }
                        } else if (bl3) {
                            k = NEW_RAIN_WEATHER_DURATION_PROVIDER.get(this.random);
                        } else {
                            k = NEW_CLEAR_WEATHER_DURATION_PROVIDER.get(this.random);
                        }
                    }

                    this.worldProperties.setThunderTime(j);
                    this.worldProperties.setRainTime(k);
                    this.worldProperties.setClearWeatherTime(i);
                    this.worldProperties.setThundering(bl2);
                    this.worldProperties.setRaining(bl3);
                }

                this.thunderGradientPrev = this.thunderGradient;
                if (this.properties.isThundering()) {
                    this.thunderGradient += 0.01F;
                } else {
                    this.thunderGradient -= 0.01F;
                }

                this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0F, 1.0F);
                this.rainGradientPrev = this.rainGradient;
                if (this.properties.isRaining()) {
                    this.rainGradient += 0.01F;
                } else {
                    this.rainGradient -= 0.01F;
                }

                this.rainGradient = MathHelper.clamp(this.rainGradient, 0.0F, 1.0F);
            }

            if (this.rainGradientPrev != this.rainGradient) {
                this.server
                        .getPlayerManager()
                        .sendToDimension(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, this.rainGradient), this.getRegistryKey());
            }

            if (this.thunderGradientPrev != this.thunderGradient) {
                this.server
                        .getPlayerManager()
                        .sendToDimension(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, this.thunderGradient), this.getRegistryKey());
            }

            if (bl != this.isRaining()) {
                if (bl) {
                    this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STOPPED, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
                } else {
                    this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
                }

                this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, this.rainGradient));
                this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, this.thunderGradient));
            }
            ci.cancel();
        }
    }
}





