package com.equilibrium.mixin.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementTrackerMixin {


    @Shadow
    @Final
    private Set<AdvancementHolder> progressChanged;

    @Shadow
    private ServerPlayer player;


    @Shadow
    public abstract AdvancementProgress getOrStartProgress(AdvancementHolder advancement);
    @Shadow
    protected abstract void unregisterListeners(AdvancementHolder advancement);
    @Shadow
    protected abstract void markForVisibilityUpdate(AdvancementHolder advancement);


    @Shadow @Final private PlayerList playerList;

    //原版的成就已移除,这里本不应该显示,除非击败一次末影龙
    //原版成就强制清除吧
    @Inject(method = "award",at =@At("HEAD"), cancellable = true)
    public void grantCriterion(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {

//        if (BooleanStorageUtil.loadFinishGameOnce(configPath.toString())) {
//            return;
//        }

        cir.cancel();
        boolean bl = false;
        AdvancementProgress advancementProgress = this.getOrStartProgress(advancement);
        boolean bl2 = advancementProgress.isDone();
        if (advancementProgress.grantProgress(criterionName)) {
            this.unregisterListeners(advancement);
            this.progressChanged.add(advancement);
            bl = true;
            if (!bl2 && advancementProgress.isDone()) {
                advancement.value().rewards().grant(this.player);
                advancement.value().display().ifPresent(display -> {
                    if (display.shouldAnnounceChat() && this.player.level().getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)&& !advancement.id().getNamespace().equals("minecraft")) {
                        this.playerList.broadcastSystemMessage(display.getType().createAnnouncement(advancement, this.player), false);
                    }
                });
            }
        }

        if (!bl2 && advancementProgress.isDone()) {
            this.markForVisibilityUpdate(advancement);
        }

        cir.setReturnValue(bl);
    }
}
