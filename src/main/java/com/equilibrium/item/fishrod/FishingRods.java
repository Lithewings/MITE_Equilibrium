package com.equilibrium.item.fishrod;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.equilibrium.OnServerInitialize.MOD_ID;


public class FishingRods {

    public static final Item FLINT_FISHING_ROD = create(FishingRodMaterial.FLINT);
    public static final Item COPPER_FISHING_ROD = create(FishingRodMaterial.COPPER);
    public static final Item SILVER_FISHING_ROD = create(FishingRodMaterial.SILVER);
    public static final Item GOLD_FISHING_ROD = create(FishingRodMaterial.GOLD);
    public static final Item MITHRIL_FISHING_ROD = create(FishingRodMaterial.MITHRIL);
    public static final Item ADAMANTIUM_FISHING_ROD = create(FishingRodMaterial.ADAMANTIUM);

    private static Item create(FishingRodMaterial material) {
        return new FishingRodItem(material, new Item.Settings()
                .maxCount(1)
                .maxDamage(material.getDurability()));
    }

    public static void registerModItemFishingRods() {
        register("flint_fishing_rod", FLINT_FISHING_ROD);
        register("copper_fishing_rod", COPPER_FISHING_ROD);
        register("silver_fishing_rod", SILVER_FISHING_ROD);
        register("gold_fishing_rod", GOLD_FISHING_ROD);
        register("mithril_fishing_rod", MITHRIL_FISHING_ROD);
        register("adamantium_fishing_rod", ADAMANTIUM_FISHING_ROD);
    }

    private static void register(String path, Item item) {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, path), item);
    }
}
