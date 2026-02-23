package com.equilibrium.mixin.vanilla_entitymixin;

import net.minecraft.entity.AngledModelEntity;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import static net.minecraft.component.DataComponentTypes.ENCHANTMENTS;

@Mixin(AxolotlEntity.class)
public abstract class AxolotEntityMixin extends AnimalEntity implements AngledModelEntity, VariantHolder<AxolotlEntity.Variant>, Bucketable {

    protected AxolotEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if(!player.getMainHandStack().get(ENCHANTMENTS).isEmpty()) {
            player.sendMessage(Text.of("桶交互失败,该实体无法与附魔物品交互"), true);
            return ActionResult.PASS;
        }
        return (ActionResult)Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
    }




}