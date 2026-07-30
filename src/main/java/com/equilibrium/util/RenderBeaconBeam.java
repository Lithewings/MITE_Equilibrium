package com.equilibrium.util;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.phys.Vec3;

/**
 * 超简信标系统 - 仅根据世界时间控制
 */
public class RenderBeaconBeam {


    private static Vec3 beamPos = null;
    private static long startTick = 0;

    public static void show(Vec3 pos, long worldTime) {
        beamPos = pos;
        startTick = worldTime;
    }

    public static void RenderBeaconInit() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (beamPos == null) return;

            var client = Minecraft.getInstance();
            if (client.level == null) return;

            // 检查是否超过20分钟 (24000 ticks)
            if (client.level.getDayTime() - startTick > 24000) {
                beamPos = null;
                return;
            }

            renderBeam(context, beamPos);
        });
    }

    private static void renderBeam(WorldRenderContext context, Vec3 pos) {
        var client = Minecraft.getInstance();
        if (client.level == null) return;

        var camera = context.camera().getPosition();
        double x = pos.x() + 0.5 - camera.x;
        double y = pos.y() + 1.0 - camera.y;
        double z = pos.z() + 0.5 - camera.z;

        context.matrixStack().pushPose();
        context.matrixStack().translate(x, y, z);

        // 尝试获取 tickDelta，失败则使用1.0
        float tickDelta;
        try {
            tickDelta = client.getTimer().getGameTimeDeltaPartialTick(false);
        } catch (Exception e) {
            // 忽略
            tickDelta=1.0f;
        }

        BeaconRenderer.renderBeaconBeam(
                context.matrixStack(),
                context.consumers(),
                BeaconRenderer.BEAM_LOCATION,
                tickDelta,
                1.0f,
                client.level.getGameTime(),
                0,
                1024,
                0xf9fffe,
                0.2f,
                0.25f
        );

        context.matrixStack().popPose();
    }

    public static void hide() {
        beamPos = null;
    }
}
