package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.component.DataComponentTypes.ENCHANTMENTS;

@Mixin(TadpoleEntity.class)
public abstract class TadPoleEntityMixin extends FishEntity {
    public TadPoleEntityMixin(EntityType<? extends FishEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob" ,at = @At("HEAD"),cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
       //一定是桶,附魔直接跳过
        if(!player.getMainHandStack().get(ENCHANTMENTS).isEmpty()) {
            player.sendMessage(Text.of("桶交互失败,该实体无法与附魔物品交互"), true);
           cir.setReturnValue(ActionResult.PASS);
           cir.cancel();
       }
    }

}
