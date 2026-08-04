package com.equilibrium.status;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PhytonutrientStatusEffect extends MobEffect {
    protected PhytonutrientStatusEffect() {
        super(
        MobEffectCategory.HARMFUL, // 药水效果是有益的还是有害的
                5797459);
    }


    // 这个方法在每个 tick 都会调用，以检查是否应应用药水效果
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }



    // 这个方法在应用药水效果时会被调用，所以我们可以在这里实现自定义功能。
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player playerEntity) {

            //玩家在复杂移动时施加饥饿度
            if(playerEntity.isSprinting()||playerEntity.onClimbable()||playerEntity.isSwimming()||playerEntity.flyDist>0){
                playerEntity.causeFoodExhaustion(0.0010f * (float)(amplifier + 1));
                return true;
            }
            else{
                playerEntity.causeFoodExhaustion(0.0003f * (float)(amplifier + 1));
                return true;
            }
        }
        return false;
    }

}





