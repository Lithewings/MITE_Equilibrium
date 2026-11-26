package com.equilibrium.mixin;

import java.util.*;

import com.equilibrium.entity.arrow_attach.ArrowHitRecorder;
import com.equilibrium.entity.arrow_attach.AttachedArrowData;
import com.equilibrium.entity.arrow_attach.AttachedArrowsView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixinForArrowAttach implements AttachedArrowsView {
	@Shadow public abstract void remove(Entity.RemovalReason reason);

	@Unique
	private Map<EntityType<?>, AttachedArrowData> arrowAttach;

	@Override
	public void attachedArrowView$attachArrow(PersistentProjectileEntity projectile) {

		//记录箭矢应掉落的物品(药箭统一掉落普通箭矢)
		ItemStack itemStack = ArrowHitRecorder.getDropStack(projectile);

		if (itemStack.isEmpty()) {
			//箭矢不掉落任何物品,则不进行附着
			return;
		}
		if (this.arrowAttach == null) {
			//首次创建附着箭矢信息时,再为其初始化
			this.arrowAttach = new HashMap<>();
		}
		//大于1的箭矢,合并,1->2->3
		this.arrowAttach.merge(projectile.getType(), new AttachedArrowData(itemStack, 1),
				(the_old, the_new) -> the_old.add());
	}

	@Override
	public List<AttachedArrowData> attachedArrowView$getArrowStacks() {
		if (this.arrowAttach == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(this.arrowAttach.values());
	}

	@Override
	public void attachedArrowView$detachTheArrow(PersistentProjectileEntity projectile) {
		if (this.arrowAttach == null)
			return;
		//箭矢数量归零则移除
		this.arrowAttach.computeIfPresent(projectile.getType(), (k, data) -> {
			int c = data.getCount() - 1;
			return c <= 0 ? null : new AttachedArrowData(data.getArrowStack(), c);
		});
	}

}
