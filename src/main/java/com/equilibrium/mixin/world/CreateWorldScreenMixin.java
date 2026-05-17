package com.equilibrium.mixin.world;

import com.equilibrium.DamageSourceRegister;
import com.equilibrium.ModWorldPreset;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow
    @Final
    public WorldCreator worldCreator;

    @Shadow public abstract WorldCreator getWorldCreator();

    @Inject(method = "<init>",at = @At("TAIL"))

    private void create(MinecraftClient client, Screen parent, GeneratorOptionsHolder generatorOptionsHolder, Optional defaultWorldType, OptionalLong seed, CallbackInfo ci) {

//        DynamicRegistryManager registryManager = generatorOptionsHolder.getCombinedRegistryManager();
//        Registry<WorldPreset> registry = registryManager.get(RegistryKeys.WORLD_PRESET);
//
//
//        this.worldCreator.getNormalWorldTypes().add(1,new WorldCreator.WorldType(registry.getEntry(ModWorldPreset.CLASSIC_PRESET).get()));
//        this.worldCreator.getNormalWorldTypes().removeFirst();

    }
}
