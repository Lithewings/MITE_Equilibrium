package com.equilibrium.mixin.advancement;

import com.equilibrium.util.BooleanStorageUtil;
import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.*;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {


    @Shadow
    @Final
    private static Logger LOGGER ;
    @Shadow
    @Final
    private static Gson GSON ;
    @Shadow
    @Final
    private PlayerManager playerManager;
    @Shadow
    @Final
    private Path filePath;
    @Shadow
    private AdvancementManager advancementManager;
    @Shadow
    @Final
    private Map<AdvancementEntry, AdvancementProgress> progress;
    @Shadow
    @Final
    private Set<AdvancementEntry> visibleAdvancements;

    @Shadow
    @Final
    private Set<AdvancementEntry> progressUpdates;

    @Shadow
    @Final
    private Set<PlacedAdvancement> updatedRoots;
    @Shadow
    private ServerPlayerEntity owner;





    @Shadow
    public abstract AdvancementProgress getProgress(AdvancementEntry advancement);
    @Shadow
    protected abstract void endTrackingCompleted(AdvancementEntry advancement);
    @Shadow
    protected abstract void onStatusUpdate(AdvancementEntry advancement);



    @Unique
    private final Path configPath = FabricLoader.getInstance().getConfigDir().normalize().resolve(BooleanStorageUtil.FINISH_GAME_ONCE);


    //原版的成就已移除,这里本不应该显示,除非击败一次末影龙
    //原版成就强制清除吧
    @Inject(method = "grantCriterion",at =@At("HEAD"), cancellable = true)
    public void grantCriterion(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {

//        if (BooleanStorageUtil.loadFinishGameOnce(configPath.toString())) {
//            return;
//        }

        cir.cancel();
        boolean bl = false;
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        boolean bl2 = advancementProgress.isDone();
        if (advancementProgress.obtain(criterionName)) {
            this.endTrackingCompleted(advancement);
            this.progressUpdates.add(advancement);
            bl = true;
            if (!bl2 && advancementProgress.isDone()) {
                advancement.value().rewards().apply(this.owner);
                advancement.value().display().ifPresent(display -> {
                    if (display.shouldAnnounceToChat() && this.owner.getWorld().getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)&& !advancement.id().getNamespace().equals("minecraft")) {
                        this.playerManager.broadcast(display.getFrame().getChatAnnouncementText(advancement, this.owner), false);
                    }
                });
            }
        }

        if (!bl2 && advancementProgress.isDone()) {
            this.onStatusUpdate(advancement);
        }

        cir.setReturnValue(bl);
    }
}
