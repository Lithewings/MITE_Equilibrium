package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.tags.ModItemTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MagmaCubeEntity.class)
public abstract class MagmaCubeEntityMixin extends SlimeEntity {
    public MagmaCubeEntityMixin(EntityType<? extends SlimeEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {

        if (damageSource.getAttacker() instanceof PlayerEntity player) {
            boolean isNotEnchanted = player.getMainHandStack().getEnchantments().isEmpty();
            boolean isHammerOrPickAxe = player.getMainHandStack().isIn(ModItemTags.HAMMERS) || player.getMainHandStack().isIn(ModItemTags.PICKAXES);
            if( isNotEnchanted || !isHammerOrPickAxe )
                return true;
        }

        return super.isInvulnerableTo(damageSource);
    }
}
