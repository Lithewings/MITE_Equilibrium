package com.equilibrium.server_and_client.fog_weather_event;

import static com.equilibrium.OnServerInitialize.MOD_ID;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class IsFogWeatherSuitableUtil {

    public static final ResourceKey<Level> OVERWORLD = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("overworld"));
    public static final ResourceKey<Level> NETHER = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("the_nether"));
    public static final ResourceKey<Level> END = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("the_end"));
    public static final ResourceKey<Level> UNDERWORLD = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(MOD_ID, "underworld"));

    public static boolean APPLY_ON_OVERWORLD = true;
    public static boolean APPLY_ON_UNDERWORLD = true;
    public static boolean APPLY_ON_NETHER = false;
    public static boolean APPLY_ON_END = false;

    private static boolean isWorld(ResourceKey<Level> target , ClientLevel clientWorld) {
        // RegistryKey.of(...) 每次新建对象，必须用 equals() 做值比较，不能用 ==。
        return target.equals(clientWorld.dimension());
    }

    private static boolean isOverWorld(ClientLevel clientWorld) {
        return isWorld(OVERWORLD,clientWorld);
    }

    private static boolean isNether(ClientLevel clientWorld) {
        return isWorld(NETHER,clientWorld);
    }

    private static boolean isEnd(ClientLevel clientWorld) {
        return isWorld(END,clientWorld);
    }

    private static boolean isUnderWorld(ClientLevel clientWorld) {
        return isWorld(UNDERWORLD,clientWorld);
    }

    //可以对外暴露的方法
    public static boolean isValid(ClientLevel clientWorld) {
        return (isOverWorld(clientWorld) && APPLY_ON_OVERWORLD)
                || (isNether(clientWorld) && APPLY_ON_NETHER)
                || (isEnd(clientWorld) && APPLY_ON_END)
                || (isUnderWorld(clientWorld) && APPLY_ON_UNDERWORLD);
    }
}
