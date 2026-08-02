package com.equilibrium.mixin.crafttime;

import com.equilibrium.block.ITimeCraftPlayer;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LocalPlayer.class)
public class MixinClientPlayerEntity extends AbstractClientPlayer implements ITimeCraftPlayer {

	@Shadow @Final protected Minecraft minecraft;
	@Unique
	public boolean is_crafting = false;
	@Unique
	public float craft_time = 0;
	@Unique
	public float craft_period = 0;

	public MixinClientPlayerEntity(ClientLevel world, GameProfile profile) {
		super(world, profile);
	}

	@Override
	public void craftTime$setCrafting(boolean is_crafting) {
		this.is_crafting = is_crafting;
	}

	@Override
	public boolean craftTime$isCrafting() {
		return this.is_crafting;
	}

	@Override
	public void craftTime$setCraftTime(float craft_time) {
		this.craft_time = craft_time;
	}

	@Override
	public float craftTime$getCraftTime() {
		return this.craft_time;
	}

	@Override
	public void craftTime$setCraftPeriod(float craft_period) {
		this.craft_period = craft_period;
	}

	@Override
	public float craftTime$getCraftPeriod() {
		return this.craft_period;
	}
	
	@Override
	public void craftTime$stopCraft() {
		this.is_crafting = false;
		this.craft_time = 0F;
	}

	@Override
	public void craftTime$startCraftWithNewPeriod(float craft_period) {
		this.craft_time = 0;
		this.craft_period = craft_period;
		this.is_crafting = true;

//		if (craft_period >= 10F) {
//			MinecraftClient.getInstance().getSoundManager().play(new CraftingTickableSound(Random.create(),this, this.getBlockPos()));
//		}
	}

	@Override
	public boolean craftTime$craftTickIsFinished() {
		if (this.craftTime$isCrafting()) {
			if (this.craftTime$getCraftTime() < this.craftTime$getCraftPeriod()) {
				this.craft_time += getCraftingSpeed(this);
			} else if (this.craftTime$getCraftTime() >= this.craftTime$getCraftPeriod()) {
				//合成结束播放声音
				this.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.1F, 1f);
				this.craftTime$startCraftWithNewPeriod(craft_period);
				return true;
			}
		}
		return false;
	}

	@Unique
	public float getCraftingSpeed(Player player) {
		float speed = 1F;
		speed += 0.05F * Math.min(200, player.experienceLevel);
		return speed;
	}
}
