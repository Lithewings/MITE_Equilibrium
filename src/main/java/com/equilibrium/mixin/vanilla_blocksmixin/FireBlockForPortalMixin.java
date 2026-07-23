package com.equilibrium.mixin.vanilla_blocksmixin;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.equilibrium.OnServerInitialize.MOD_ID;

@Mixin(AbstractFireBlock.class)
public class FireBlockForPortalMixin {
    @Inject(method = "isOverworldOrNether",at = @At("HEAD"), cancellable = true)
    //正确地在地下世界建立传送门
    private static void isOverworldOrNether(World world, CallbackInfoReturnable<Boolean> cir) {
        RegistryKey<World> UNDERWORLD = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID,"underworld"));
        cir.setReturnValue(world.getRegistryKey() == World.OVERWORLD || world.getRegistryKey() == World.NETHER||world.getRegistryKey() == UNDERWORLD);
    }
}
