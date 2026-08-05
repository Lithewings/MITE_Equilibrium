package com.equilibrium.mixin.render.hud_at_players_screen;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(DebugScreenOverlay.class)

public abstract class DebugHudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;


    @Shadow
    @Final
    private Font font;

    @Shadow
    private Level getLevel() {
        return DataFixUtils.orElse(
                Optional.ofNullable(this.minecraft.getSingleplayerServer()).flatMap(server -> Optional.ofNullable(server.getLevel(this.minecraft.level.dimension()))), this.minecraft.level
        );
    }


    @Unique
    public List<String> getLeftText() {

        IntegratedServer integratedServer = this.minecraft.getSingleplayerServer();
        ClientPacketListener clientPlayNetworkHandler = this.minecraft.getConnection();
        Connection clientConnection = clientPlayNetworkHandler.getConnection();
        float f = clientConnection.getAverageSentPackets();
        float g = clientConnection.getAverageReceivedPackets();
        TickRateManager tickManager = this.getLevel().tickRateManager();
        String string;
        if (tickManager.isSteppingForward()) {
            string = " (frozen - stepping)";
        } else if (tickManager.isFrozen()) {
            string = " (frozen)";
        } else {
            string = "";
        }

        String string3;
        if (integratedServer != null) {
            ServerTickRateManager serverTickManager = integratedServer.tickRateManager();
            boolean bl = serverTickManager.isSprinting();
            if (bl) {
                string = " (sprinting)";
            }

            String string2 = bl ? "-" : String.format(Locale.ROOT, "%.1f", tickManager.millisecondsPerTick());
            string3 = String.format(Locale.ROOT, "Integrated server @ %.1f/%s ms%s, %.0f tx, %.0f rx", integratedServer.getCurrentSmoothedTickTime(), string2, string, f, g);
        } else {
            string3 = String.format(Locale.ROOT, "\"%s\" server%s, %.0f tx, %.0f rx", clientPlayNetworkHandler.serverBrand(), string, f, g);
        }

        BlockPos blockPos = this.minecraft.getCameraEntity().blockPosition();
        if (this.minecraft.showOnlyReducedInfo()) {
            return Lists.<String>newArrayList(
                    "Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
                    this.minecraft.fpsString,
                    string3,
                    this.minecraft.levelRenderer.getSectionStatistics(),
                    this.minecraft.levelRenderer.getEntityStatistics(),
                    "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(),
                    this.minecraft.level.gatherChunkSourceStats(),
                    "",
                    String.format(Locale.ROOT, "Chunk-relative: %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
            );
        } else {
            Level world = this.getLevel();
            LongSet longSet = (LongSet)(world instanceof ServerLevel ? ((ServerLevel)world).getForcedChunks() : LongSets.EMPTY_SET);
            List<String> list = Lists.<String>newArrayList(
                    "Minecraft " + SharedConstants.getCurrentVersion().getName() + " ("
                            + this.minecraft.getLaunchedVersion()
                            + "/"
                            + ClientBrandRetriever.getClientModName()
                            + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType())
                            + ")",
                    this.minecraft.fpsString,
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


    @Unique
    public void drawText(GuiGraphics context, List<String> text, boolean left) {
        int i = 9;

        for (int j = 0; j < text.size(); j++) {
            String string = (String)text.get(j);
            if (!Strings.isNullOrEmpty(string)) {
                int k = this.font.width(string);
                int l = left ? 2 : context.guiWidth() - 2 - k;
                int m = 2 + i * j;
                context.fill(l - 1, m - 1, l + k + 1, m + i - 1, -1873784752);
            }
        }

        for (int jx = 0; jx < text.size(); jx++) {
            String string = (String)text.get(jx);
            if (!Strings.isNullOrEmpty(string)) {
                int k = this.font.width(string);
                int l = left ? 2 : context.guiWidth() - 2 - k;
                int m = 2 + i * jx;
                context.drawString(this.font, string, l, m, 14737632, false);
            }
        }
    }







    @Inject(method = "collectGameInformationText",at = @At(value = "HEAD"), cancellable = true)
    protected void collectGameInformationText(CallbackInfoReturnable<List<String>> cir) {
        cir.cancel();
        List<String> list = this.getLeftText();
        cir.setReturnValue(list);
    }


    @Unique
    private static long toMiB(long bytes) {
        return bytes / 1024L / 1024L;
    }



    @Shadow
    @Final
    private DebugScreenOverlay.AllocationRateCalculator allocationRateCalculator;

    @Inject(method = "getSystemInformation",at = @At(value = "HEAD"),cancellable = true)
    protected void getRightText(CallbackInfoReturnable<List<String>> cir) {
        cir.cancel();
        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;
        List<String> list = Lists.<String>newArrayList(
                String.format(Locale.ROOT, "Java: %s", System.getProperty("java.version")),
                String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", o * 100L / l, toMiB(o), toMiB(l)),
                String.format(Locale.ROOT, "Allocation rate: %03dMB/s", toMiB(allocationRateCalculator.bytesAllocatedPerSecond(o))),
                String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", m * 100L / l, toMiB(m)),
                "",
                String.format(Locale.ROOT, "CPU: %s", GlUtil.getCpuInfo()),
                "",
                String.format(
                        Locale.ROOT,
                        "Display: %dx%d (%s)",
                        Minecraft.getInstance().getWindow().getWidth(),
                        Minecraft.getInstance().getWindow().getHeight(),
                        GlUtil.getVendor()
                ),
                GlUtil.getRenderer(),
                GlUtil.getOpenGLVersion()
        );

        cir.setReturnValue(list);



    }









}


