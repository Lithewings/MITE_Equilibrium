package com.equilibrium.mixin.crafttime;

import com.equilibrium.block.crafting_table.CraftTimeController;
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
public class CraftingTimeInformationWithClientPlayerEntity extends AbstractClientPlayer implements CraftTimeController {

	@Shadow
	@Final
	protected Minecraft minecraft;

	@Unique
	public boolean isCrafting = false;
	@Unique
	public float craftTimeCost = 0;
	@Unique
	public float craftStage = 0;

	public CraftingTimeInformationWithClientPlayerEntity(ClientLevel world, GameProfile profile) {
		super(world, profile);
	}

	@Override
	public void setCraftingStatus(boolean isCrafting) {
		this.isCrafting = isCrafting;
	}

	@Override
	public boolean isCrafting() {
		return this.isCrafting;
	}

	@Override
	public void setCraftTimeCost(float craftTimeCost) {
		this.craftTimeCost = craftTimeCost;
	}

	@Override
	public float getCraftTimeCost() {
		return this.craftTimeCost;
	}

	@Override
	public void setCraftStage(float craftStage) {
		this.craftStage = craftStage;
	}

	@Override
	public float getCraftStage() {
		return this.craftStage;
	}

	@Override
	public void stopCraft() {
		this.isCrafting = false;
		this.craftTimeCost = 0F;
	}

	@Override
	public void startCraftWithNewStage(float craftStage) {
		this.craftTimeCost = 0;
		this.craftStage = craftStage;
		this.isCrafting = true;

//		if (craft_period >= 10F) {
//			MinecraftClient.getInstance().getSoundManager().play(new CraftingTickableSound(Random.create(),this, this.getBlockPos()));
//		}
	}

	@Override
	public boolean isCraftTickFinished() {
		if (this.isCrafting()) {
			if (this.getCraftTimeCost() < this.getCraftStage()) {
				this.craftTimeCost += getCraftingSpeed(this);
			} else if (this.getCraftTimeCost() >= this.getCraftStage()) {
				//合成结束播放声音
				this.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.1F, 1f);
				this.startCraftWithNewStage(craftStage);
				return true;
			}
		}
		return false;
	}

	@Unique
	//利用玩家经验进行合成加速
	public float getCraftingSpeed(Player player) {
		float speed = 1F;
		speed += 0.05F * Math.min(200, player.experienceLevel);
		return speed;
	}
}
