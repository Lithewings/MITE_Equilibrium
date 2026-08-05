package com.equilibrium.mixin.vanilla_blocksmixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.OnServerInitialize.MOD_ID;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;

@Mixin(BaseFireBlock.class)
public class FireBlockForPortalMixin {
    @Inject(method = "inPortalDimension",at = @At("HEAD"), cancellable = true)
    //正确地在地下世界建立传送门
    private static void isOverworldOrNether(Level world, CallbackInfoReturnable<Boolean> cir) {
        ResourceKey<Level> UNDERWORLD = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(MOD_ID,"underworld"));
        cir.setReturnValue(world.dimension() == Level.OVERWORLD || world.dimension() == Level.NETHER||world.dimension() == UNDERWORLD);
    }
}
