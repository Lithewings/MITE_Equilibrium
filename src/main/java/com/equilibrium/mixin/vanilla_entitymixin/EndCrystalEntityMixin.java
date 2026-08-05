package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.item.tool.ToolItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystal.class)
public abstract class EndCrystalEntityMixin extends Entity {
    @Shadow public int time;

    public EndCrystalEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "hurt",at = @At("HEAD"),cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(source.getEntity() instanceof Player player && player.getMainHandItem().is(ToolItems.ADAMANTIUM_PICKAXE.get()))
            return;
        else{
            cir.setReturnValue(false);
        }

    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (super.isInvulnerableTo(damageSource)) {
            return true;
        }
        //免疫任何箭矢伤害
        if(damageSource.is(DamageTypes.ARROW))
            return true;

        return false;
    }
}
