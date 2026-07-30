package com.equilibrium.mixin.vanilla_entitymixin;

import com.equilibrium.tags.ModItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MagmaCube.class)
public abstract class MagmaCubeEntityMixin extends Slime {
    public MagmaCubeEntityMixin(EntityType<? extends Slime> entityType, Level world) {
        super(entityType, world);
    }
    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {

        if (damageSource.getEntity() instanceof Player player) {
            boolean isNotEnchanted = player.getMainHandItem().getEnchantments().isEmpty();
            boolean isHammerOrPickAxe = player.getMainHandItem().is(ModItemTags.HAMMERS) || player.getMainHandItem().is(ModItemTags.PICKAXES);
            if( isNotEnchanted || !isHammerOrPickAxe )
                return true;
        }

        return super.isInvulnerableTo(damageSource);
    }
}
