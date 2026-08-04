package com.equilibrium.server_and_client.server;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class SoundEventRegistry {
    // 创建声音事件的延迟注册器
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    // ==================== 声音事件定义 ====================
    public static final Supplier<SoundEvent> ENTITY_INVISIBLE_STALKER_AMBIENT1 =
            register("mob.invisiblestalker.say1");
    public static final Supplier<SoundEvent> ENTITY_INVISIBLE_STALKER_AMBIENT2 =
            register("mob.invisiblestalker.say2");
    public static final Supplier<SoundEvent> ENTITY_INVISIBLE_STALKER_AMBIENT3 =
            register("mob.invisiblestalker.say3");

    public static final Supplier<SoundEvent> ENTITY_INVISIBLE_STALKER_HURT1 =
            register("mob.invisiblestalker.hurt1");
    public static final Supplier<SoundEvent> ENTITY_INVISIBLE_STALKER_HURT2 =
            register("mob.invisiblestalker.hurt2");
    public static final Supplier<SoundEvent> ENTITY_INVISIBLE_STALKER_DEATH =
            register("mob.invisiblestalker.death");

    public static final Supplier<SoundEvent> ENTITY_GHOUL_AMBIENT1 =
            register("mob.ghoul.say1");
    public static final Supplier<SoundEvent> ENTITY_GHOUL_AMBIENT2 =
            register("mob.ghoul.say2");

    public static final Supplier<SoundEvent> ENTITY_GHOUL_HURT1 =
            register("mob.ghoul.hurt1");
    public static final Supplier<SoundEvent> ENTITY_GHOUL_HURT2 =
            register("mob.ghoul.hurt2");
    public static final Supplier<SoundEvent> ENTITY_GHOUL_DEATH =
            register("mob.ghoul.death");

    public static final Supplier<SoundEvent> ENTITY_WIGHT_HURT1 =
            register("mob.wight.hurt1");
    public static final Supplier<SoundEvent> ENTITY_WIGHT_HURT2 =
            register("mob.wight.hurt2");
    public static final Supplier<SoundEvent> ENTITY_WIGHT_DEATH =
            register("mob.wight.death");
    public static final Supplier<SoundEvent> ENTITY_WIGHT_AMBIENT1 =
            register("mob.wight.say1");
    public static final Supplier<SoundEvent> ENTITY_WIGHT_AMBIENT2 =
            register("mob.wight.say2");

    // 辅助注册方法
    private static Supplier<SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}