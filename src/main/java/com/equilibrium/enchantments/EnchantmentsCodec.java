package com.equilibrium.enchantments;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class EnchantmentsCodec {

    public static final MapCodec<?extends EnchantmentEntityEffect> REACHING = register("reaching",WaterBucketEnchantment.CODEC);



    private static MapCodec<? extends EnchantmentEntityEffect> register(String name,MapCodec<? extends EnchantmentEntityEffect> codec){
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(MOD_ID,name),codec);
    }
    public static void registerAllOfEnchantments(){

    }
}
