package com.equilibrium.mixin.render.hud_at_players_screen;

import com.equilibrium.util.PlayerMaxHealthOrFoodLevelHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Shadow @Nullable protected abstract Player getCameraPlayer();

    @ModifyConstant(method = "renderFood", constant = @Constant(intValue = 10))
    private int renderFood(int constant){
        if(this.getCameraPlayer() instanceof Player player){
            FoodData hungerManager = player.getFoodData();
            //随时更新数据z
            hungerManager.tick(player);
            return PlayerMaxHealthOrFoodLevelHelper.getMaxHealthOrFoodLevel(player)/2;
        }
        return constant;
    }

//    @Inject(method = "renderFood", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V",shift = At.Shift.AFTER), cancellable = true)
//    private void renderFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
//
//        HungerManager hungerManager = player.getHungerManager();
//        //随时更新数据
//        hungerManager.update(player);
//
//        int i = hungerManager.getFoodLevel();
//        //获得额外的饱食度上限渲染
//        //规则:从6点饱食度开始,每增加5级,就增加2点饱食度上限,每5级增加一次
//        //仅仅是渲染,实际上限在hungerManager里面设置
//
//        int maxFoodLevel = PlayerMaxHealthOrFoodLevelHelper.getMaxHealthOrFoodLevel(player);
//
//
//        //遍历10个鸡腿
//        for (int j = 0; j < maxFoodLevel / 2; j++) {
//            int k = top;
//            Identifier identifier;
//            Identifier identifier2;
//            Identifier identifier3;
//            if (player.hasStatusEffect(StatusEffects.HUNGER)) {
//                identifier = FOOD_EMPTY_HUNGER_TEXTURE;
//                identifier2 = FOOD_HALF_HUNGER_TEXTURE;
//                identifier3 = FOOD_FULL_HUNGER_TEXTURE;
//            } else {
//                identifier = FOOD_EMPTY_TEXTURE;
//                identifier2 = FOOD_HALF_TEXTURE;
//                identifier3 = FOOD_FULL_TEXTURE;
//            }
//
//            if (player.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (i * 3 + 1) == 0) {
//                k = top + (this.random.nextInt(3) - 1);
//            }
//
//            int l = right - j * 8 - 9;
//            context.drawGuiTexture(identifier, l, k, 9, 9);
//            if (j * 2 + 1 < i) {
//                context.drawGuiTexture(identifier3, l, k, 9, 9);
//            }
//
//            if (j * 2 + 1 == i) {
//                context.drawGuiTexture(identifier2, l, k, 9, 9);
//            }
//        }
//
//        RenderSystem.disableBlend();
//    }


}
