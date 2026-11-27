package com.equilibrium.item.fishrod;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.OnServerInitialize.MOD_ID;


public class FishingRods {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> FLINT_FISHING_ROD =
            register("flint_fishing_rod", FishingRodMaterial.FLINT);

    public static final DeferredItem<Item> COPPER_FISHING_ROD =
            register("copper_fishing_rod", FishingRodMaterial.COPPER);

    public static final DeferredItem<Item> SILVER_FISHING_ROD =
            register("silver_fishing_rod", FishingRodMaterial.SILVER);

    public static final DeferredItem<Item> GOLD_FISHING_ROD =
            register("gold_fishing_rod", FishingRodMaterial.GOLD);

    public static final DeferredItem<Item> MITHRIL_FISHING_ROD =
            register("mithril_fishing_rod", FishingRodMaterial.MITHRIL);

    public static final DeferredItem<Item> ADAMANTIUM_FISHING_ROD =
            register("adamantium_fishing_rod", FishingRodMaterial.ADAMANTIUM);

    private static DeferredItem<Item> register(String name, FishingRodMaterial material) {
        return ITEMS.register(name, () -> new FishingRodItem(material, new Item.Properties()
                .stacksTo(1)
                .durability(material.getDurability())));
    }
}
