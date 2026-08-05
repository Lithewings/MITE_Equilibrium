package com.equilibrium.item.tool;

import com.equilibrium.OnServerInitialize;
import com.equilibrium.item.tool.flint.FlintAxeOrHatchet;
import com.equilibrium.item.tool.flint.FlintKnife;
import com.equilibrium.item.tool.flint.FlintShovel;
import com.equilibrium.item.tool.metal.*;
import com.equilibrium.item.tool.metal.adamantium.AdamantiumHoe;
import com.equilibrium.item.tool.metal.silver.SilverDagger;
import com.equilibrium.item.tool.metal.silver.SilverHammer;
import com.equilibrium.item.tool.metal.silver.SilverSword;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 工具物品注册类，使用 DeferredRegister 统一管理。
 * 在模组主类中调用 Tools.ITEMS.register(modEventBus) 即可完成注册。
 */
public class ToolItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OnServerInitialize.MOD_ID);

    // ----- 燧石工具 -----
    public static final Supplier<Item> FLINT_HATCHET =
            ITEMS.register("flint_hatchet", () -> createFlintAxeOrHatchetItem(ToolTiers.FLINT_HATCHET, 4, 1f));
    public static final Supplier<Item> FLINT_AXE =
            ITEMS.register("flint_axe", () -> createFlintAxeOrHatchetItem(ToolTiers.FLINT_AXE, 5, 1f));
    public static final Supplier<Item> FLINT_KNIFE =
            ITEMS.register("flint_knife", () -> createFlintKnifeItem(ToolTiers.FLINT_KNIFE, 3, 2f));
    public static final Supplier<Item> FLINT_SHOVEL =
            ITEMS.register("flint_shovel", () -> createFlintShovelItem(ToolTiers.FLINT_SHOVEL, 2, 2f));

    // ----- 斧头 -----
    public static final Supplier<Item> COPPER_AXE =
            ITEMS.register("copper_axe", () -> createMetalAxeItem(ToolTiers.COPPER_AXE, 8, 0.7f));
    public static final Supplier<Item> GOLD_AXE =
            ITEMS.register("gold_axe", () -> createMetalAxeItem(ToolTiers.GOLD_AXE, 5, 0.5f));
    public static final Supplier<Item> SILVER_AXE =
            ITEMS.register("silver_axe", () -> createMetalAxeItem(ToolTiers.SILVER_AXE, 8, 0.7f));
    public static final Supplier<Item> IRON_AXE =
            ITEMS.register("iron_axe", () -> createMetalAxeItem(ToolTiers.IRON_AXE, 9, 0.7f));
    public static final Supplier<Item> MITHRIL_AXE =
            ITEMS.register("mithril_axe", () -> createMetalAxeItem(ToolTiers.MITHRIL_AXE, 10, 0.8f));
    public static final Supplier<Item> ADAMANTIUM_AXE =
            ITEMS.register("adamantium_axe", () -> createMetalAxeItem(ToolTiers.ADAMANTIUM_AXE, 10, 1f));

    // ----- 镐 -----
    public static final Supplier<Item> COPPER_PICKAXE =
            ITEMS.register("copper_pickaxe", () -> createMetalPickAxeItem(ToolTiers.COPPER_PICKAXE, 5, 2f));
    public static final Supplier<Item> GOLD_PICKAXE =
            ITEMS.register("gold_pickaxe", () -> createMetalPickAxeItem(ToolTiers.GOLD_PICKAXE, 5, 2f));
    public static final Supplier<Item> SILVER_PICKAXE =
            ITEMS.register("silver_pickaxe", () -> createMetalPickAxeItem(ToolTiers.SILVER_PICKAXE, 5, 2f));
    public static final Supplier<Item> IRON_PICKAXE =
            ITEMS.register("iron_pickaxe", () -> createMetalPickAxeItem(ToolTiers.IRON_PICKAXE, 6, 2f));
    public static final Supplier<Item> MITHRIL_PICKAXE =
            ITEMS.register("mithril_pickaxe", () -> createMetalPickAxeItem(ToolTiers.MITHRIL_PICKAXE, 7, 2f));
    public static final Supplier<Item> ADAMANTIUM_PICKAXE =
            ITEMS.register("adamantium_pickaxe", () -> createMetalPickAxeItem(ToolTiers.ADAMANTIUM_PICKAXE, 8, 2f));

    // ----- 锄头 -----
    public static final Supplier<Item> COPPER_HOE =
            ITEMS.register("copper_hoe", () -> createMetalHoeItem(ToolTiers.COPPER_HOE, 4, 3f));
    public static final Supplier<Item> GOLD_HOE =
            ITEMS.register("gold_hoe", () -> createMetalHoeItem(ToolTiers.GOLD_HOE, 4, 3f));
    public static final Supplier<Item> SILVER_HOE =
            ITEMS.register("silver_hoe", () -> createMetalHoeItem(ToolTiers.SILVER_HOE, 4, 3f));
    public static final Supplier<Item> IRON_HOE =
            ITEMS.register("iron_hoe", () -> createMetalHoeItem(ToolTiers.IRON_HOE, 5, 3f));
    public static final Supplier<Item> MITHRIL_HOE =
            ITEMS.register("mithril_hoe", () -> createMetalHoeItem(ToolTiers.MITHRIL_HOE, 6, 3f));
    public static final Supplier<Item> ADAMANTIUM_HOE =
            ITEMS.register("adamantium_hoe", () -> createAdamantiumHoeItem(ToolTiers.ADAMANTIUM_HOE, 7, 3f));

    // ----- 锤子 -----
    public static final Supplier<Item> COPPER_HAMMER =
            ITEMS.register("copper_hammer", () -> createMetalHammerItem(ToolTiers.COPPER_HAMMER, 6, 3f));
    public static final Supplier<Item> SILVER_HAMMER =
            ITEMS.register("silver_hammer", () -> createSilverHammerItem(ToolTiers.SILVER_HAMMER, 6, 3f));
    public static final Supplier<Item> GOLD_HAMMER =
            ITEMS.register("gold_hammer", () -> createMetalHammerItem(ToolTiers.GOLD_HAMMER, 6, 3f));
    public static final Supplier<Item> IRON_HAMMER =
            ITEMS.register("iron_hammer", () -> createMetalHammerItem(ToolTiers.IRON_HAMMER, 7, 3f));
    public static final Supplier<Item> MITHRIL_HAMMER =
            ITEMS.register("mithril_hammer", () -> createMetalHammerItem(ToolTiers.MITHRIL_HAMMER, 8, 3f));
    public static final Supplier<Item> ADAMANTIUM_HAMMER =
            ITEMS.register("adamantium_hammer", () -> createMetalHammerItem(ToolTiers.ADAMANTIUM_HAMMER, 9, 4f));

    // ----- 铲子 -----
    public static final Supplier<Item> COPPER_SHOVEL =
            ITEMS.register("copper_shovel", () -> createMetalShovelItem(ToolTiers.COPPER_SHOVEL, 3, 4f));
    public static final Supplier<Item> GOLD_SHOVEL =
            ITEMS.register("gold_shovel", () -> createMetalShovelItem(ToolTiers.GOLD_SHOVEL, 3, 4f));
    public static final Supplier<Item> SILVER_SHOVEL =
            ITEMS.register("silver_shovel", () -> createMetalShovelItem(ToolTiers.SILVER_SHOVEL, 3, 4f));
    public static final Supplier<Item> IRON_SHOVEL =
            ITEMS.register("iron_shovel", () -> createMetalShovelItem(ToolTiers.IRON_SHOVEL, 4, 4f));
    public static final Supplier<Item> MITHRIL_SHOVEL =
            ITEMS.register("mithril_shovel", () -> createMetalShovelItem(ToolTiers.MITHRIL_SHOVEL, 5, 4f));
    public static final Supplier<Item> ADAMANTIUM_SHOVEL =
            ITEMS.register("adamantium_shovel", () -> createMetalShovelItem(ToolTiers.ADAMANTIUM_SHOVEL, 6, 4f));

    // ----- 剑 -----
    public static final Supplier<Item> COPPER_SWORD =
            ITEMS.register("copper_sword", () -> createMetalSwordItem(ToolTiers.COPPER_SWORD, 7, 3f));
    public static final Supplier<Item> GOLD_SWORD =
            ITEMS.register("gold_sword", () -> createMetalSwordItem(ToolTiers.GOLD_SWORD, 7, 3f));
    public static final Supplier<Item> SILVER_SWORD =
            ITEMS.register("silver_sword", () -> createSilverSwordItem(ToolTiers.SILVER_SWORD, 7, 3f));
    public static final Supplier<Item> IRON_SWORD =
            ITEMS.register("iron_sword", () -> createMetalSwordItem(ToolTiers.IRON_SWORD, 8, 3f));
    public static final Supplier<Item> MITHRIL_SWORD =
            ITEMS.register("mithril_sword", () -> createMetalSwordItem(ToolTiers.MITHRIL_SWORD, 9, 3f));
    public static final Supplier<Item> ADMANTIUM_SWORD =
            ITEMS.register("adamantium_sword", () -> createMetalSwordItem(ToolTiers.ADAMANTIUM_SWORD, 10, 3f));

    // ----- 匕首 -----
    public static final Supplier<Item> COPPER_DAGGER =
            ITEMS.register("copper_dagger", () -> createMetalDaggerItem(ToolTiers.COPPER_DAGGER, 6, 4f));
    public static final Supplier<Item> GOLD_DAGGER =
            ITEMS.register("gold_dagger", () -> createMetalDaggerItem(ToolTiers.GOLD_DAGGER, 6, 4f));
    public static final Supplier<Item> SILVER_DAGGER =
            ITEMS.register("silver_dagger", () -> createSilverDaggerItem(ToolTiers.SILVER_DAGGER, 6, 4f));
    public static final Supplier<Item> IRON_DAGGER =
            ITEMS.register("iron_dagger", () -> createMetalDaggerItem(ToolTiers.IRON_DAGGER, 7, 4f));
    public static final Supplier<Item> MITHRIL_DAGGER =
            ITEMS.register("mithril_dagger", () -> createMetalDaggerItem(ToolTiers.MITHRIL_DAGGER, 8, 4f));
    public static final Supplier<Item> ADMANTIUM_DAGGER =
            ITEMS.register("adamantium_dagger", () -> createMetalDaggerItem(ToolTiers.ADAMANTIUM_DAGGER, 9, 4f));

    // ----- 工厂方法（与原来完全一致）-----
    public static Item createFlintShovelItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new FlintShovel(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createFlintKnifeItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new FlintKnife(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createSilverSwordItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new SilverSword(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createSilverDaggerItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new SilverDagger(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createFlintAxeOrHatchetItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new FlintAxeOrHatchet(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalAxeItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalAxe(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalHoeItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalHoe(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalShovelItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalShovel(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalPickAxeItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalPickAxe(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalSwordItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalSword(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalDaggerItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalDagger(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createMetalHammerItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new MetalHammer(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createSilverHammerItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new SilverHammer(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static Item createAdamantiumHoeItem(Tier material, int finalDamage, float finalDamageSpeed) {
        return new AdamantiumHoe(material, new Item.Properties()
                .attributes(DiggerItem.createAttributes(material, -1 + finalDamage, -4 + finalDamageSpeed)));
    }

    public static void deferredRegisterLoadTools(){}



}