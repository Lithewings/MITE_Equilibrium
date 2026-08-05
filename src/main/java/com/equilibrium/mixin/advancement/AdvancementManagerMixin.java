package com.equilibrium.mixin.advancement;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(AdvancementTree.class)
public abstract class AdvancementManagerMixin {
    @Shadow
    @Final
    private Map<ResourceLocation, AdvancementNode> nodes;

    @Shadow
    @Final
    private Set<AdvancementNode> roots;

    @Final
    @Shadow
    private Set<AdvancementNode> tasks ;


    @Shadow
    @Nullable
    private AdvancementTree.Listener listener;


    @Shadow @Final private static Logger LOGGER;



    @Shadow
    protected abstract void remove(AdvancementNode advancement);

    //只是移除了日志


    @Inject(method = "remove(Lnet/minecraft/advancements/AdvancementNode;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementTree;remove(Lnet/minecraft/advancements/AdvancementNode;)V"), cancellable = true)
    private void remove(AdvancementNode advancement, CallbackInfo ci) {
        //        LOGGER.info("Forgot about advancement {}", advancement.getAdvancementEntry());
        ci.cancel();
        this.nodes.remove(advancement.holder().id());
        if (advancement.parent() == null) {
            this.roots.remove(advancement);
            if (this.listener != null) {
                this.listener.onRemoveAdvancementRoot(advancement);
            }
        } else {
            this.tasks.remove(advancement);
            if (this.listener != null) {
                this.listener.onRemoveAdvancementTask(advancement);
            }
        }
    }

    @Inject(method = "remove(Ljava/util/Set;)V", at = @At("HEAD"), cancellable = true)
    public void removeAll(Set<ResourceLocation> advancements, CallbackInfo ci) {
        ci.cancel();
        for (ResourceLocation identifier : advancements) {
            AdvancementNode placedAdvancement = (AdvancementNode) this.nodes.get(identifier);
            if (placedAdvancement == null) {
//                LOGGER.warn("Told to remove advancement {} but I don't know what that is", identifier);
            } else {
                this.remove(placedAdvancement);
            }
        }
    }


}
