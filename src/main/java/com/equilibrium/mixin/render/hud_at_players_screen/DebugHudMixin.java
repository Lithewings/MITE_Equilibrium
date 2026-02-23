package com.equilibrium.mixin.render.hud_at_players_screen;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Mixin(DebugHud.class)

public abstract class DebugHudMixin {
    //没有加入mixin配置,若需要再加入

    @Shadow
    @Final
    private MinecraftClient client;
@Shadow
    private boolean showDebugHud;
@Shadow
    private boolean renderingChartVisible;
@Shadow
    private boolean renderingAndTickChartsVisible;
@Shadow
    private boolean packetSizeAndPingChartsVisible;


@Shadow
@Final
    private TextRenderer textRenderer;










    @Shadow
    private ChunkPos pos;
    @Shadow
    private CompletableFuture<WorldChunk> chunkFuture;
    @Shadow
    private WorldChunk chunk;



    public void resetChunk() {
        this.chunkFuture = null;
        this.chunk = null;
    }


    private World getWorld() {
        return DataFixUtils.orElse(
                Optional.ofNullable(this.client.getServer()).flatMap(server -> Optional.ofNullable(server.getWorld(this.client.world.getRegistryKey()))), this.client.world
        );
    }

    private String getServerWorldDebugString() {
        ServerWorld serverWorld = this.getServerWorld();
        return serverWorld != null ? serverWorld.asString() : null;
    }

    @Nullable
    private ServerWorld getServerWorld() {
        IntegratedServer integratedServer = this.client.getServer();
        return integratedServer != null ? integratedServer.getWorld(this.client.world.getRegistryKey()) : null;
    }

    private WorldChunk getClientChunk() {
        if (this.chunk == null) {
            this.chunk = this.client.world.getChunk(this.pos.x, this.pos.z);
        }

        return this.chunk;
    }

    @Nullable
    private WorldChunk getChunk() {
        if (this.chunkFuture == null) {
            ServerWorld serverWorld = this.getServerWorld();
            if (serverWorld == null) {
                return null;
            }

            this.chunkFuture = serverWorld.getChunkManager()
                    .getChunkFutureSyncOnMainThread(this.pos.x, this.pos.z, ChunkStatus.FULL, false)
                    .thenApply(optionalChunk -> (WorldChunk)optionalChunk.orElse(null));
        }

        return (WorldChunk)this.chunkFuture.getNow(null);
    }
    @Shadow
    private static final Map<Heightmap.Type, String> HEIGHT_MAP_TYPES = Util.make(new EnumMap(Heightmap.Type.class), types -> {
        types.put(Heightmap.Type.WORLD_SURFACE_WG, "SW");
        types.put(Heightmap.Type.WORLD_SURFACE, "S");
        types.put(Heightmap.Type.OCEAN_FLOOR_WG, "OW");
        types.put(Heightmap.Type.OCEAN_FLOOR, "O");
        types.put(Heightmap.Type.MOTION_BLOCKING, "M");
        types.put(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, "ML");
    });


    @Shadow
    private static String getBiomeString(RegistryEntry<Biome> biome) {
        return biome.getKeyOrValue().map(biomeKey -> biomeKey.getValue().toString(), biome_ -> "[unregistered " + biome_ + "]");
    }

    public List<String> getLeftText() {

        IntegratedServer integratedServer = this.client.getServer();
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        ClientConnection clientConnection = clientPlayNetworkHandler.getConnection();
        float f = clientConnection.getAveragePacketsSent();
        float g = clientConnection.getAveragePacketsReceived();
        TickManager tickManager = this.getWorld().getTickManager();
        String string;
        if (tickManager.isStepping()) {
            string = " (frozen - stepping)";
        } else if (tickManager.isFrozen()) {
            string = " (frozen)";
        } else {
            string = "";
        }

        String string3;
        if (integratedServer != null) {
            ServerTickManager serverTickManager = integratedServer.getTickManager();
            boolean bl = serverTickManager.isSprinting();
            if (bl) {
                string = " (sprinting)";
            }

            String string2 = bl ? "-" : String.format(Locale.ROOT, "%.1f", tickManager.getMillisPerTick());
            string3 = String.format(Locale.ROOT, "Integrated server @ %.1f/%s ms%s, %.0f tx, %.0f rx", integratedServer.getAverageTickTime(), string2, string, f, g);
        } else {
            string3 = String.format(Locale.ROOT, "\"%s\" server%s, %.0f tx, %.0f rx", clientPlayNetworkHandler.getBrand(), string, f, g);
        }

        BlockPos blockPos = this.client.getCameraEntity().getBlockPos();
        if (this.client.hasReducedDebugInfo()) {
            return Lists.<String>newArrayList(
                    "Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
                    this.client.fpsDebugString,
                    string3,
                    this.client.worldRenderer.getChunksDebugString(),
                    this.client.worldRenderer.getEntitiesDebugString(),
                    "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(),
                    this.client.world.asString(),
                    "",
                    String.format(Locale.ROOT, "Chunk-relative: %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
            );
        } else {
            World world = this.getWorld();
            LongSet longSet = (LongSet)(world instanceof ServerWorld ? ((ServerWorld)world).getForcedChunks() : LongSets.EMPTY_SET);
            List<String> list = Lists.<String>newArrayList(
                    "Minecraft " + SharedConstants.getGameVersion().getName() + " ("
                            + this.client.getGameVersion()
                            + "/"
                            + ClientBrandRetriever.getClientModName()
                            + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType())
                            + ")",
                    this.client.fpsDebugString,
                    string3

            );
            //坐标显示
//            list.add(
//                    String.format(
//                            Locale.ROOT, "X: %.1f",this.client.getCameraEntity().getX()
//                    )
//
//            );
//            list.add(
//                    String.format(
//                            Locale.ROOT, "Y: %.1f",this.client.getCameraEntity().getY()
//                    )
//
//            );
//            list.add(
//                    String.format(
//                            Locale.ROOT, "Z: %.1f", this.client.getCameraEntity().getZ()
//                    )

//            );
            return list;
        }

    }


    public void drawText(DrawContext context, List<String> text, boolean left) {
        int i = 9;

        for (int j = 0; j < text.size(); j++) {
            String string = (String)text.get(j);
            if (!Strings.isNullOrEmpty(string)) {
                int k = this.textRenderer.getWidth(string);
                int l = left ? 2 : context.getScaledWindowWidth() - 2 - k;
                int m = 2 + i * j;
                context.fill(l - 1, m - 1, l + k + 1, m + i - 1, -1873784752);
            }
        }

        for (int jx = 0; jx < text.size(); jx++) {
            String string = (String)text.get(jx);
            if (!Strings.isNullOrEmpty(string)) {
                int k = this.textRenderer.getWidth(string);
                int l = left ? 2 : context.getScaledWindowWidth() - 2 - k;
                int m = 2 + i * jx;
                context.drawText(this.textRenderer, string, l, m, 14737632, false);
            }
        }
    }












    @Inject(method = "drawLeftText",at = @At(value = "HEAD"),cancellable = true)
    protected void drawLeftText(DrawContext context, CallbackInfo ci) {
        ci.cancel();

        List<String> list = this.getLeftText();
        this.drawText(context, list, true);
    }


    private static long toMiB(long bytes) {
        return bytes / 1024L / 1024L;
    }



@Shadow
@Final
private DebugHud.AllocationRateCalculator allocationRateCalculator;

    @Inject(method = "getRightText",at = @At(value = "HEAD"),cancellable = true)
    protected void getRightText(CallbackInfoReturnable<List<String>> cir) {
        cir.cancel();
        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;
        List<String> list = Lists.<String>newArrayList(
                String.format(Locale.ROOT, "Java: %s", System.getProperty("java.version")),
                String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", o * 100L / l, toMiB(o), toMiB(l)),
                String.format(Locale.ROOT, "Allocation rate: %03dMB/s", toMiB(allocationRateCalculator.get(o))),
                String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", m * 100L / l, toMiB(m)),
                "",
                String.format(Locale.ROOT, "CPU: %s", GlDebugInfo.getCpuInfo()),
                "",
                String.format(
                        Locale.ROOT,
                        "Display: %dx%d (%s)",
                        MinecraftClient.getInstance().getWindow().getFramebufferWidth(),
                        MinecraftClient.getInstance().getWindow().getFramebufferHeight(),
                        GlDebugInfo.getVendor()
                ),
                GlDebugInfo.getRenderer(),
                GlDebugInfo.getVersion()
        );

        cir.setReturnValue(list);



    }









}


