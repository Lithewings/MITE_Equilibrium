package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.util.XpHashMap.getXpForLevel;

@Mixin(AbstractSkeleton.class)
public abstract class SkeletonEntityMixin extends Monster implements RangedAttackMob {



    @Override
    public int getBaseExperienceReward(){
        return getXpForLevel(1);
    }





    protected SkeletonEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    protected void AbstractSkeletonEntity(EntityType entityType, Level world, CallbackInfo ci) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(4);
        this.getAttribute(Attributes.FALL_DAMAGE_MULTIPLIER).setBaseValue(16);
//        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(64);
//        if(this.getWorld().getRegistryKey()==RegistryKey.of(RegistryKeys.WORLD, Identifier.of("miteequilibrium", "underworld"))){
//        //地下世界追踪距离修正,原本所有怪物追踪距离砍半,但骷髅可以比别的怪物看得更远一点
//            double range = this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
//            this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(range+16);
//    }
    }

    @Shadow public abstract void aiStep();

    @Inject(method = "getHardAttackInterval",at = @At(value = "HEAD"),cancellable = true)
    protected void getHardAttackInterval(CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        cir.setReturnValue(20);
    }

    @Inject(method = "getAttackInterval",at = @At(value = "HEAD"),cancellable = true)
    protected void getRegularAttackInterval(CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        cir.setReturnValue(40);
    }

}
