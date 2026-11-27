package com.equilibrium.entity.arrow_attach;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public final class ArrowHitRecorder {



	private ArrowHitRecorder() {
	}

	public static boolean attachArrow(AbstractArrow projectile, Entity target) {

		if (!(target instanceof LivingEntity livingEntity))
			return false;
		if (!isAcceptableArrow(projectile))
			return false;
		if (projectile.getPierceLevel() > 0)
			return false;
		if (!(projectile.getOwner() instanceof ServerPlayer))
			return false;

		//附着箭矢

		((AttachedArrowsView) livingEntity).attachedArrowView$attachArrow(projectile);
		return true;
	}

	public static void tryDetachTheArrow(AbstractArrow projectile, Entity target) {
		if (!(target instanceof LivingEntity livingEntity))
			return;
		if (!isAcceptableArrow(projectile))
			return;
		if (projectile.getPierceLevel() > 0)
			return;
		if (!(projectile.getOwner() instanceof ServerPlayer))
			return;
		//减少指定的箭矢,为其减少一次箭矢附着
		((AttachedArrowsView) livingEntity).attachedArrowView$detachTheArrow(projectile);

	}

	public static boolean isAcceptableArrow(AbstractArrow projectile) {
		return projectile instanceof Arrow ||
				projectile instanceof SpectralArrow;
	}


	public static ItemStack getDropStack(AbstractArrow projectile) {
		ItemStack stack = projectile.getPickupItemStackOrigin();
		if (stack.is(Items.TIPPED_ARROW)||stack.is(Items.SPECTRAL_ARROW)) {
			return new ItemStack(Items.ARROW);
		}
		return stack;
	}
}
