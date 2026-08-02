package com.equilibrium.tags;

import com.equilibrium.OnServerInitialize;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ModEntityTags {
    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(OnServerInitialize.MOD_ID,id));
    }
    public static final TagKey<EntityType<?>> STOCKS = of("stock");

    public static void registerModEntityTags(){
    }

}
