package com.equilibrium.entity.arrow_attach;

import java.util.List;
import net.minecraft.world.entity.projectile.AbstractArrow;


public interface AttachedArrowsView {

	void attachedArrowView$attachArrow(AbstractArrow projectile);

	List<AttachedArrowData> attachedArrowView$getArrowStacks();

	void attachedArrowView$detachTheArrow(AbstractArrow projectile);
}
