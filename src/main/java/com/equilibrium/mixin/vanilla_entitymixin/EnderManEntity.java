package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.entity.goal.EndermanAlwaysAngryAtPlayerGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(EnderMan.class)
public abstract class EnderManEntity extends Monster implements NeutralMob {
    protected EnderManEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }
    @Inject(method = "initGoals", at = @At("HEAD"))
    protected void initGoals(CallbackInfo ci) {
        //玩家死亡超过一定次数,末影人会无缘无故对其发动攻击
        this.targetSelector.addGoal(3, new EndermanAlwaysAngryAtPlayerGoal<>(this, Player.class, true, false));
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    protected void mobTick(CallbackInfo ci) {
        if(endermanEntityCoolDown!=0) {
            this.setTarget(null);
            endermanEntityCoolDown--;
        }
    }

    //末影人下次进入愤怒状态的冷却值
    int endermanEntityCoolDown = 0;


    @Override
    public int getBaseExperienceReward(){
        return getXpForLevel(3);
    }

    @Inject(method = "damage", at = @At(value = "HEAD"))
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        //原文drown的体现,在LivingEntity中:
//        if (!this.getWorld().isClient && this.hurtByWater() && this.isWet()) {
//            this.damage(this.getDamageSources().drown(), 1.0F);
//        }
        boolean hurtByWater = source==this.damageSources().drown();
        if(hurtByWater){
            //被水伤害时,陷入一场300tick的冷却时间,只要玩家不主动招惹末影人,末影人会一直保持中立
            //结束之后,末影人恢复正常,检查玩家物品栏是否含有末影珍珠从而主动攻击,或者玩家主动招惹末影人,亦或者玩家死亡次数足够多让末影人主动攻击
            endermanEntityCoolDown=300;
        }else{
            //提前结束冷却
            endermanEntityCoolDown=0;
        }

    }



}
