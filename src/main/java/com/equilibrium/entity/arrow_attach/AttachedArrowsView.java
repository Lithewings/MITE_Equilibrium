package com.equilibrium.entity.arrow_attach;

import java.util.List;

import net.minecraft.entity.projectile.PersistentProjectileEntity;


public interface AttachedArrowsView {

	void attachedArrowView$attachArrow(PersistentProjectileEntity projectile);

	List<AttachedArrowData> attachedArrowView$getArrowStacks();

	void attachedArrowView$detachTheArrow(PersistentProjectileEntity projectile);
}
