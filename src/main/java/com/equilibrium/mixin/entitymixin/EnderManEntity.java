package com.equilibrium.mixin.entitymixin;

import com.equilibrium.entity.goal.EndermanAlwaysAngryAtPlayerGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.util.XpHashMap.getXpForLevel;
import static net.minecraft.entity.mob.Angriness.ANGRY;

@Mixin(EndermanEntity.class)
public abstract class EnderManEntity extends HostileEntity implements Angerable {
    protected EnderManEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method = "initGoals", at = @At("HEAD"))
    protected void initGoals(CallbackInfo ci) {
        //玩家死亡超过一定次数,末影人会无缘无故对其发动攻击
        this.targetSelector.add(3, new EndermanAlwaysAngryAtPlayerGoal<>(this, PlayerEntity.class, true, false));
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
    public int getXpToDrop(){
        return getXpForLevel(3);
    }

    @Inject(method = "damage", at = @At(value = "HEAD"))
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        //原文drown的体现,在LivingEntity中:
//        if (!this.getWorld().isClient && this.hurtByWater() && this.isWet()) {
//            this.damage(this.getDamageSources().drown(), 1.0F);
//        }
        boolean hurtByWater = source==this.getDamageSources().drown();
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
