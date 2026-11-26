package com.equilibrium.entity.arrow_attach;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;


public final class ArrowHitRecorder {



	private ArrowHitRecorder() {
	}

	public static boolean attachArrow(PersistentProjectileEntity projectile, Entity target) {

		if (!(target instanceof LivingEntity livingEntity))
			return false;
		if (!isAcceptableArrow(projectile))
			return false;
		if (projectile.getPierceLevel() > 0)
			return false;
		if (!(projectile.getOwner() instanceof ServerPlayerEntity))
			return false;

		//附着箭矢

		((AttachedArrowsView) livingEntity).attachedArrowView$attachArrow(projectile);
		return true;
	}

	public static void tryDetachTheArrow(PersistentProjectileEntity projectile, Entity target) {
		if (!(target instanceof LivingEntity livingEntity))
			return;
		if (!isAcceptableArrow(projectile))
			return;
		if (projectile.getPierceLevel() > 0)
			return;
		if (!(projectile.getOwner() instanceof ServerPlayerEntity))
			return;
		//减少指定的箭矢,为其减少一次箭矢附着
		((AttachedArrowsView) livingEntity).attachedArrowView$detachTheArrow(projectile);

	}

	public static boolean isAcceptableArrow(PersistentProjectileEntity projectile) {
		return projectile instanceof ArrowEntity ||
				projectile instanceof SpectralArrowEntity;
	}


	public static ItemStack getDropStack(PersistentProjectileEntity projectile) {
		ItemStack stack = projectile.getItemStack();
		if (stack.isOf(Items.TIPPED_ARROW)||stack.isOf(Items.SPECTRAL_ARROW)) {
			return new ItemStack(Items.ARROW);
		}
		return stack;
	}
}
