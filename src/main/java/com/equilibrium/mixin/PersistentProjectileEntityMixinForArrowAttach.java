package com.equilibrium.mixin;

import com.equilibrium.entity.arrow_attach.ArrowHitRecorder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixinForArrowAttach {
	@Redirect(
			method = "onEntityHit",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
			)
	)
	private boolean recordArrowThenDamage(Entity target, DamageSource source, float amount) {
		PersistentProjectileEntity arrow = (PersistentProjectileEntity) (Object) this;

		boolean recorded = false;
		if (!arrow.getWorld().isClient()) {
			recorded = ArrowHitRecorder.attachArrow(arrow, target);
		}

		boolean applied = target.damage(source, amount);
		//未造成伤害的箭矢不应该附着上去
		if (recorded && !applied) {
			ArrowHitRecorder.tryDetachTheArrow(arrow, target);

		}
		return applied;
	}
}
