package com.equilibrium.entity.arrow_attach;

import java.util.List;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class ArrowDeathHandler {

	public static final int MAX_DROP_COUNT = 64;
	public static final float DROP_RATIO = 0.5f;


	public static void onDeath(LivingEntity entity, DamageSource source) {
		List<AttachedArrowData> arrows = ((AttachedArrowsView) entity).attachedArrowView$getArrowStacks();
		//遍历所有附着的箭矢数据,将所有箭物品实体创建出来
		for (AttachedArrowData data : arrows) {
			ItemStack arrowStack = data.getArrowStack();
			int remaining = (int) (data.getCount()*DROP_RATIO);


			while (remaining > 0) {
				int size = (Math.min(remaining, MAX_DROP_COUNT));
				entity.spawnAtLocation(arrowStack.copyWithCount(size));
				remaining -= size;
			}
		}
	}
}
