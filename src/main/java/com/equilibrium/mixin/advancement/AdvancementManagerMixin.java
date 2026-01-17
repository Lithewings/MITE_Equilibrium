package com.equilibrium.mixin.advancement;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static com.equilibrium.OnServerInitialize.LOGGER;

@Mixin(AdvancementManager.class)
public abstract class AdvancementManagerMixin {
    @Shadow
    @Final
    private Map<Identifier, PlacedAdvancement> advancements;
    @Shadow
    @Final
    private Set<PlacedAdvancement> roots;
    @Shadow
    @Final
    private Set<PlacedAdvancement> dependents;
    @Shadow
    @Nullable
    private AdvancementManager.Listener listener;


    @Shadow @Final private static Logger LOGGER;



    @Shadow
    protected abstract void remove(PlacedAdvancement advancement);

    //只是移除了日志


    @Inject(method = "remove",at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlacedAdvancement;getAdvancementEntry()Lnet/minecraft/advancement/AdvancementEntry;"), cancellable = true)
    private void remove(PlacedAdvancement advancement, CallbackInfo ci) {
        //        LOGGER.info("Forgot about advancement {}", advancement.getAdvancementEntry());
        ci.cancel();
        this.advancements.remove(advancement.getAdvancementEntry().id());
        if (advancement.getParent() == null) {
            this.roots.remove(advancement);
            if (this.listener != null) {
                this.listener.onRootRemoved(advancement);
            }
        } else {
            this.dependents.remove(advancement);
            if (this.listener != null) {
                this.listener.onDependentRemoved(advancement);
            }
        }
    }

    @Inject(method = "removeAll", at = @At("HEAD"), cancellable = true)
    public void removeAll(Set<Identifier> advancements, CallbackInfo ci) {
        ci.cancel();
        for (Identifier identifier : advancements) {
            PlacedAdvancement placedAdvancement = (PlacedAdvancement) this.advancements.get(identifier);
            if (placedAdvancement == null) {
//                LOGGER.warn("Told to remove advancement {} but I don't know what that is", identifier);
            } else {
                this.remove(placedAdvancement);
            }
        }
    }


}
