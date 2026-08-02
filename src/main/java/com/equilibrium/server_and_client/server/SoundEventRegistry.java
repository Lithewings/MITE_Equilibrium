package com.equilibrium.server_and_client.server;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class SoundEventRegistry {
    public static final SoundEvent ENTITY_INVISIBLE_STALKER_AMBIENT1 = register("mob.invisiblestalker.say1");
    public static final SoundEvent ENTITY_INVISIBLE_STALKER_AMBIENT2 = register("mob.invisiblestalker.say2");
    public static final SoundEvent ENTITY_INVISIBLE_STALKER_AMBIENT3 = register("mob.invisiblestalker.say3");


    public static final SoundEvent ENTITY_INVISIBLE_STALKER_HURT1 = register("mob.invisiblestalker.hurt1");
    public static final SoundEvent ENTITY_INVISIBLE_STALKER_HURT2 = register("mob.invisiblestalker.hurt2");

    public static final SoundEvent ENTITY_INVISIBLE_STALKER_DEATH = register("mob.invisiblestalker.death");


    public static final SoundEvent ENTITY_GHOUL_AMBIENT1 = register("mob.ghoul.say1");
    public static final SoundEvent ENTITY_GHOUL_AMBIENT2 = register("mob.ghoul.say2");


    public static final SoundEvent ENTITY_GHOUL_HURT1 = register("mob.ghoul.hurt1");
    public static final SoundEvent ENTITY_GHOUL_HURT2 = register("mob.ghoul.hurt2");

    public static final SoundEvent ENTITY_GHOUL_DEATH = register("mob.ghoul.death");


    public static final SoundEvent ENTITY_WIGHT_HURT1 = register("mob.wight.hurt1");
    public static final SoundEvent ENTITY_WIGHT_HURT2 = register("mob.wight.hurt2");

    public static final SoundEvent ENTITY_WIGHT_DEATH = register("mob.wight.death");

    public static final SoundEvent ENTITY_WIGHT_AMBIENT1 = register("mob.wight.say1");
    public static final SoundEvent ENTITY_WIGHT_AMBIENT2 = register("mob.wight.say2");


    public static SoundEvent register(String name){
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID,name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT,id,SoundEvent.createVariableRangeEvent(id));
    }





    public static void registrySoundEvents(){

    }




}
