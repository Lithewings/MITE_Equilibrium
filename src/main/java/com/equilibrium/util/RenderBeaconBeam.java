package com.equilibrium.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 超简信标系统 - 仅根据世界时间控制
 */
public class RenderBeaconBeam {


    private static Vec3d beamPos = null;
    private static long startTick = 0;

    public static void show(Vec3d pos, long worldTime) {
        beamPos = pos;
        startTick = worldTime;
    }

    public static void RenderBeaconInit() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (beamPos == null) return;

            var client = MinecraftClient.getInstance();
            if (client.world == null) return;

            // 检查是否超过20分钟 (24000 ticks)
            if (client.world.getTimeOfDay() - startTick > 24000) {
                beamPos = null;
                return;
            }

            renderBeam(context, beamPos);
        });
    }

    private static void renderBeam(WorldRenderContext context, Vec3d pos) {
        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.world == null) return;

        var camera = context.camera().getPos();
        double x = pos.getX() + 0.5 - camera.x;
        double y = pos.getY() + 1.0 - camera.y;
        double z = pos.getZ() + 0.5 - camera.z;

        context.matrixStack().push();
        context.matrixStack().translate(x, y, z);

        // 尝试获取 tickDelta，失败则使用1.0
        float tickDelta;
        try {
            tickDelta = client.getRenderTickCounter().getTickDelta(false);
        } catch (Exception e) {
            // 忽略
            tickDelta=1.0f;
        }

        BeaconBlockEntityRenderer.renderBeam(
                context.matrixStack(),
                context.consumers(),
                net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer.BEAM_TEXTURE,
                tickDelta,
                1.0f,
                client.world.getTime(),
                0,
                1024,
                0xf9fffe,
                0.2f,
                0.25f
        );

        context.matrixStack().pop();
    }

    public static void hide() {
        beamPos = null;
    }
}
