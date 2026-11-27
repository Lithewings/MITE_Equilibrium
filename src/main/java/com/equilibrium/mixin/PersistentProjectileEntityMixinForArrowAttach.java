package com.equilibrium.mixin;

import com.equilibrium.entity.arrow_attach.ArrowHitRecorder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractArrow.class)
public abstract class PersistentProjectileEntityMixinForArrowAttach {

	@Redirect(
			method = "onHitEntity",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
			)
	)
	private boolean recordArrowThenDamage(Entity target, DamageSource source, float amount) {
		AbstractArrow arrow = (AbstractArrow) (Object) this;

		boolean recorded = false;
		if (!arrow.level().isClientSide) {
			recorded = ArrowHitRecorder.attachArrow(arrow, target);
		}

		boolean applied = target.hurt(source, amount);
		//未造成伤害的箭矢不应该附着上去
		if (recorded && !applied) {
			ArrowHitRecorder.tryDetachTheArrow(arrow, target);
		}
		return applied;
	}
}
