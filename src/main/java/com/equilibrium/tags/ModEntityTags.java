package com.equilibrium.tags;

import com.equilibrium.OnServerInitialize;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModEntityTags {
    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(OnServerInitialize.MOD_ID,id));
    }
    public static final TagKey<EntityType<?>> STOCKS = of("stock");

    public static void registerModEntityTags(){
    }

}
