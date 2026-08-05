//package com.equilibrium.mixin;
//
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.text.Text;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(MinecraftClient.class)
//public class KeepBreakingMixin {
//    @Unique
//    private boolean keepBreaking = false;
//    @Unique
//    private boolean wasRightPressed = false;
//    @Unique
//    private boolean rightJustPressed = false;
//
//    @Inject(method = "handleInputEvents", at = @At("HEAD"))
//    private void onHandleInputEvents(CallbackInfo ci) {
//        MinecraftClient client = (MinecraftClient) (Object) this;
//
//        // 安全检查
//        if (client.player == null) return;
//
//        // 获取当前按键状态
//        boolean rightPressed = client.options.useKey.isPressed();
//        boolean leftPressed = client.options.attackKey.isPressed();
//
//        // 检测右键点击瞬间（从没按到按下）
//        rightJustPressed = rightPressed && !wasRightPressed;
//
//        // 右键点击时切换锁定状态
//        if (rightJustPressed && leftPressed) {
//            keepBreaking = !keepBreaking;
//
//            // 发送状态提示消息
//            if (keepBreaking) {
//                client.player.sendMessage(Text.literal("§a锁定破坏中..."), true);
//            } else {
//                client.player.sendMessage(Text.literal("§c取消锁定"), true);
//                // 【新增】取消锁定时立即恢复左键状态
//                client.options.attackKey.setPressed(false);
//            }
//        }
//
//        // 应用锁定状态
//        if (keepBreaking) {
//            client.options.attackKey.setPressed(true);
//        }
//
//        // 记录右键状态供下一帧使用
//        wasRightPressed = rightPressed;
//    }
//}
//
package com.equilibrium.mixin.some_special_rules;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.GlobalModConfig.isBreakLockEnabled;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;


@Mixin(Minecraft.class)
public class KeepBreakingMixin {
    @Shadow @Nullable public LocalPlayer player;
    @Unique
    private boolean keepBreaking = false;
    @Unique
    private boolean wasLeftPressed = false;
    @Unique
    private boolean wasRightPressed = false;
    @Unique
    private int skip = 1; // 用于跳过锁定后首次左键触发的取消逻辑

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void onHandleInputEvents(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;

        if (!isBreakLockEnabled()) {
            return;
        }

        // 安全检查：无玩家时直接返回
        if (client.player == null)
            return;



        // 获取当前按键状态
        boolean leftPressed = client.options.keyAttack.isDown();   // 左键（破坏键）
        boolean rightPressed = client.options.keyUse.isDown();     // 右键（使用键）
        // 检测左键/右键的"按下瞬间"（从没按到按下）
        boolean leftJustPressed = leftPressed && !wasLeftPressed;
        boolean rightJustPressed = rightPressed && !wasRightPressed;

        // 1. 核心逻辑：长按左键时，按下右键触发锁定
        if (rightJustPressed && leftPressed && !keepBreaking) {
            keepBreaking = true;
            client.player.displayClientMessage(Component.literal("§a锁定破坏中..."), true);
            skip = 1; // 锁定时重置skip
        }

        // 2. 锁定状态下，点击左键取消锁定（跳过首次误触发）
        if (leftJustPressed && keepBreaking) {
            if (skip == 0) {
                keepBreaking = false;
                client.player.displayClientMessage(Component.literal("§c取消锁定"), true);
                client.options.keyAttack.setDown(false); // 恢复左键状态
                skip = 1; // 重置skip
            } else {
                skip--; // 跳过本次触发
            }
        }

        // 3. 锁定状态下强制保持左键按下，实现持续破坏
        if (keepBreaking) {
            client.options.keyAttack.setDown(true);
        }

        // 记录当前帧按键状态，供下一帧判断"按下瞬间"使用
        wasLeftPressed = leftPressed;
        wasRightPressed = rightPressed;
    }
}