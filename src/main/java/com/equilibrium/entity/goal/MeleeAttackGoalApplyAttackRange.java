package com.equilibrium.entity.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class MeleeAttackGoalApplyAttackRange extends Goal {
	protected final PathfinderMob mob;
	private final double speed;
	private final boolean pauseWhenMobIdle;
	protected Path path;
	private double targetX;
	private double targetY;
	private double targetZ;
	private int updateCountdownTicks;
	private int cooldown;
	private final int attackIntervalTicks = 20;
	protected long lastUpdateTime;
	private static final long MAX_ATTACK_TIME = 20L;
	protected float attackRange;

	public MeleeAttackGoalApplyAttackRange(PathfinderMob mob, double speed, boolean pauseWhenMobIdle,float attackRange) {
		this.mob = mob;
		this.speed = speed;
		this.pauseWhenMobIdle = pauseWhenMobIdle;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		this.attackRange = attackRange;
	}

	public float getAttackRange() {
		return attackRange;
	}

	@Override
	public boolean canUse() {
		long l = this.mob.level().getGameTime();
		if (l - this.lastUpdateTime < 20L) {
			return false;
		} else {
			this.lastUpdateTime = l;
			LivingEntity livingEntity = this.mob.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else {
				this.path = this.mob.getNavigation().createPath(livingEntity, 0);
				return this.path != null ? true : this.mob.distanceTo(livingEntity) < attackRange;
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isAlive()) {
			return false;
		} else if (!this.pauseWhenMobIdle) {
			return !this.mob.getNavigation().isDone();
		} else {
			return !this.mob.isWithinRestriction(livingEntity.blockPosition())
				? false
				: !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player)livingEntity).isCreative();
		}
	}

	@Override
	public void start() {
		this.mob.getNavigation().moveTo(this.path, this.speed);
		this.mob.setAggressive(true);
		this.updateCountdownTicks = 0;
		this.cooldown = 0;
	}

	@Override
	public void stop() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
			this.mob.setTarget(null);
		}

		this.mob.setAggressive(false);
		this.mob.getNavigation().stop();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity != null) {
			this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
			this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
			if ((this.pauseWhenMobIdle || this.mob.getSensing().hasLineOfSight(livingEntity))
				&& this.updateCountdownTicks <= 0
				&& (
					this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
						|| livingEntity.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0
						|| this.mob.getRandom().nextFloat() < 0.05F
				)) {
				this.targetX = livingEntity.getX();
				this.targetY = livingEntity.getY();
				this.targetZ = livingEntity.getZ();
				this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
				double d = this.mob.distanceToSqr(livingEntity);
				if (d > 1024.0) {
					this.updateCountdownTicks += 10;
				} else if (d > 256.0) {
					this.updateCountdownTicks += 5;
				}

				if (!this.mob.getNavigation().moveTo(livingEntity, this.speed)) {
					this.updateCountdownTicks += 15;
				}

				this.updateCountdownTicks = this.adjustedTickDelay(this.updateCountdownTicks);
			}

			this.cooldown = Math.max(this.cooldown - 1, 0);
			this.attack(livingEntity);
		}
	}

	protected void attack(LivingEntity target) {
		if (this.canAttack(target)) {
			this.resetCooldown();
			this.mob.swing(InteractionHand.MAIN_HAND);
			this.mob.doHurtTarget(target);
		}
	}

	protected void resetCooldown() {
		this.cooldown = this.adjustedTickDelay(20);
	}

	protected boolean isCooledDown() {
		return this.cooldown <= 0;
	}

	protected boolean canAttack(LivingEntity target) {
		return this.isCooledDown() && this.mob.distanceTo(target) < getAttackRange() && this.mob.getSensing().hasLineOfSight(target);
	}

	protected int getCooldown() {
		return this.cooldown;
	}

	protected int getMaxCooldown() {
		return this.adjustedTickDelay(20);
	}
}
