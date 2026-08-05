package com.equilibrium.item.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.equilibrium.OnServerInitialize.MOD_ID;

public class ArmorItems {

    // 使用 DeferredRegister.Items 创建物品注册器（专门用于 Item）
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    // 铜质护甲
    public static final DeferredItem<ArmorItem> COPPER_HELMET = ITEMS.register("copper_helmet",
            () -> new ArmorItem(ArmorMaterials.COPPER, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(6 * 32)));

    public static final DeferredItem<ArmorItem> COPPER_CHEST_PLATE = ITEMS.register("copper_chest_plate",
            () -> new ArmorItem(ArmorMaterials.COPPER, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(12 * 32)));

    public static final DeferredItem<ArmorItem> COPPER_LEGGINGS = ITEMS.register("copper_leggings",
            () -> new ArmorItem(ArmorMaterials.COPPER, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(8 * 32)));

    public static final DeferredItem<ArmorItem> COPPER_BOOTS = ITEMS.register("copper_boots",
            () -> new ArmorItem(ArmorMaterials.COPPER, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(4 * 32)));

    // 秘银护甲
    public static final DeferredItem<ArmorItem> MITHRIL_HELMET = ITEMS.register("mithril_helmet",
            () -> new ArmorItem(ArmorMaterials.MITHRIL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(5 * 64)));

    public static final DeferredItem<ArmorItem> MITHRIL_CHEST_PLATE = ITEMS.register("mithril_chest_plate",
            () -> new ArmorItem(ArmorMaterials.MITHRIL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(8 * 64)));

    public static final DeferredItem<ArmorItem> MITHRIL_LEGGINGS = ITEMS.register("mithril_leggings",
            () -> new ArmorItem(ArmorMaterials.MITHRIL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(7 * 64)));

    public static final DeferredItem<ArmorItem> MITHRIL_BOOTS = ITEMS.register("mithril_boots",
            () -> new ArmorItem(ArmorMaterials.MITHRIL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(4 * 64)));

    // 远古金属链甲
    public static final DeferredItem<ArmorItem> ANCIENT_METAL_CHAINMAIL_HELMET = ITEMS.register("ancient_metal_chainmail_helmet",
            () -> new ArmorItem(ArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(5 * 32)));

    public static final DeferredItem<ArmorItem> ANCIENT_METAL_CHAINMAIL_CHEST_PLATE = ITEMS.register("ancient_metal_chainmail_chest_plate",
            () -> new ArmorItem(ArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(8 * 32)));

    public static final DeferredItem<ArmorItem> ANCIENT_METAL_CHAINMAIL_LEGGINGS = ITEMS.register("ancient_metal_chainmail_leggings",
            () -> new ArmorItem(ArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(7 * 32)));

    public static final DeferredItem<ArmorItem> ANCIENT_METAL_CHAINMAIL_BOOTS = ITEMS.register("ancient_metal_chainmail_boots",
            () -> new ArmorItem(ArmorMaterials.ANCIENT_METAL_CHAINMAIL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(4 * 32)));
}