package com.equilibrium.item;


import com.equilibrium.item.tools_attribute.flint.FlintAxeOrHatchet;
import com.equilibrium.item.tools_attribute.flint.FlintKnife;
import com.equilibrium.item.tools_attribute.ModToolMaterials;
import com.equilibrium.item.tools_attribute.flint.FlintShovel;
import com.equilibrium.item.tools_attribute.metal.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Tools {
    //一个完整的工具注册流程

    //1、把它放进标签里,用于实体交互距离增益上以及附魔选取上
    //2、为它注册枚举类中的基础伤害和耐久
    //3、继承工具类,确定伤害实体和方块所消耗的耐久值如何
    //4、物品注册,模型注册,放置贴图







    //以下开始添加物品:


    public static final Item FLINT_HATCHET =createFlintAxeOrHatchetItem(ModToolMaterials.FLINT_HATCHET,4,1f);
    public static final Item FLINT_AXE =createFlintAxeOrHatchetItem(ModToolMaterials.FLINT_AXE,5,1f);
    public static final Item FLINT_KNIFE =createFlintKnifeItem(ModToolMaterials.FLINT_KNIFE,3,2f);
    public static final Item FLINT_SHOVEL =createFlintShovelItem(ModToolMaterials.FLINT_SHOVEL,2,2f);




    public static final Item COPPER_AXE =createMetalAxeItem(ModToolMaterials.COPPER_AXE,8,0.7f);
    public static final Item GOLD_AXE = createMetalAxeItem(ModToolMaterials.GOLD_AXE,5,0.5f);
    public static final Item SILVER_AXE = createMetalAxeItem(ModToolMaterials.SILVER_AXE,8,0.7f);
    public static final Item IRON_AXE = createMetalAxeItem(ModToolMaterials.IRON_AXE,9,0.7f);
    public static final Item MITHRIL_AXE = createMetalAxeItem(ModToolMaterials.ADAMANTIUM_AXE,10,0.8f);
    public static final Item ADAMANTIUM_AXE = createMetalAxeItem(ModToolMaterials.ADAMANTIUM_AXE,10,1f);


    public static final Item COPPER_PICKAXE = createMetalPickAxeItem(ModToolMaterials.COPPER_PICKAXE,5,2f);
    public static final Item GOLD_PICKAXE = createMetalPickAxeItem(ModToolMaterials.GOLD_PICKAXE,5,2f);
    public static final Item SILVER_PICKAXE = createMetalPickAxeItem(ModToolMaterials.SILVER_PICKAXE,5,2f);
    public static final Item IRON_PICKAXE = createMetalPickAxeItem(ModToolMaterials.IRON_PICKAXE,6,2f);
    public static final Item MITHRIL_PICKAXE = createMetalPickAxeItem(ModToolMaterials.MITHRIL_PICKAXE,7,2f);
    public static final Item ADAMANTIUM_PICKAXE = createMetalPickAxeItem(ModToolMaterials.ADAMANTIUM_PICKAXE,8,2f);

    public static final Item COPPER_HOE = createMetalHoeItem(ModToolMaterials.COPPER_HOE,4,3f);
    public static final Item GOLD_HOE = createMetalHoeItem(ModToolMaterials.GOLD_HOE,4,3f);
    public static final Item SILVER_HOE = createMetalHoeItem(ModToolMaterials.SILVER_HOE,4,3f);
    public static final Item IRON_HOE = createMetalHoeItem(ModToolMaterials.IRON_HOE,5,3f);
    public static final Item MITHRIL_HOE = createMetalHoeItem(ModToolMaterials.MITHRIL_HOE,6,3f);


    public static final Item ADAMANTIUM_HOE =createAdamantiumHoeItem(ModToolMaterials.ADAMANTIUM_HOE,7,3f);


    public static Item createAdamantiumHoeItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){
        return new AdamantiumHoe(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }



    public static final Item COPPER_HAMMER = createMetalHammerItem(ModToolMaterials.COPPER_HAMMER,6,3f);
    public static final Item SILVER_HAMMER = createSilverHammerItem(ModToolMaterials.SILVER_HAMMER,6,3f);
    public static final Item GOLD_HAMMER = createMetalHammerItem(ModToolMaterials.GOLD_HAMMER,6,3f);
    public static final Item IRON_HAMMER = createMetalHammerItem(ModToolMaterials.IRON_HAMMER,7,3f);
    public static final Item MITHRIL_HAMMER = createMetalHammerItem(ModToolMaterials.MITHRIL_HAMMER,8,3f);
    public static final Item ADAMANTIUM_HAMMER =createMetalHammerItem(ModToolMaterials.ADAMANTIUM_HAMMER,9,4f);




    public static final Item COPPER_SHOVEL = createMetalShovelItem(ModToolMaterials.COPPER_SHOVEL,3,4f);
    public static final Item GOLD_SHOVEL = createMetalShovelItem(ModToolMaterials.GOLD_SHOVEL,3,4f);
    public static final Item SILVER_SHOVEL = createMetalShovelItem(ModToolMaterials.SILVER_SHOVEL,3,4f);
    public static final Item IRON_SHOVEL = createMetalShovelItem(ModToolMaterials.IRON_SHOVEL,4,4f);
    public static final Item MITHRIL_SHOVEL = createMetalShovelItem(ModToolMaterials.MITHRIL_SHOVEL,5,4f);
    public static final Item ADAMANTIUM_SHOVEL =createMetalShovelItem(ModToolMaterials.ADAMANTIUM_SHOVEL,6,4f);

    public static final Item COPPER_SWORD = createMetalSwordItem(ModToolMaterials.COPPER_SWORD,7,3f);
    public static final Item GOLD_SWORD = createMetalSwordItem(ModToolMaterials.GOLD_SWORD,7,3f);
    public static final Item SILVER_SWORD = createSilverSwordItem(ModToolMaterials.SILVER_SWORD,7,3f);
    public static final Item IRON_SWORD = createMetalSwordItem(ModToolMaterials.IRON_SWORD,8,3f);
    public static final Item MITHRIL_SWORD = createMetalSwordItem(ModToolMaterials.MITHRIL_SWORD,9,3f);
    public static final Item ADMANTIUM_SWORD =createMetalSwordItem(ModToolMaterials.ADAMANTIUM_SHOVEL,10,3f);

    public static final Item COPPER_DAGGER = createMetalDaggerItem(ModToolMaterials.COPPER_DAGGER,6,4f);
    public static final Item GOLD_DAGGER = createMetalDaggerItem(ModToolMaterials.GOLD_DAGGER,6,4f);
    public static final Item SILVER_DAGGER = createSilverDaggerItem(ModToolMaterials.SILVER_DAGGER,6,4f);
    public static final Item IRON_DAGGER = createMetalDaggerItem(ModToolMaterials.IRON_DAGGER,7,4f);
    public static final Item MITHRIL_DAGGER = createMetalDaggerItem(ModToolMaterials.MITHRIL_DAGGER,8,4f);
    public static final Item ADMANTIUM_DAGGER =createMetalDaggerItem(ModToolMaterials.ADAMANTIUM_DAGGER,9,4f);






    public static Item createFlintShovelItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){
        return new FlintShovel(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }

    public static Item createFlintKnifeItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){
        return new FlintKnife(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }


    public static Item createSilverSwordItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){

        return new SilverSword(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }

    public static Item createSilverDaggerItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){

        return new SilverDagger(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }


    public static Item createFlintAxeOrHatchetItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){
        return new FlintAxeOrHatchet(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }
    public static Item createMetalAxeItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){
        return new MetalAxe(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }
    public static Item createMetalHoeItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){
        return new MetalHoe(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }
    public static Item createMetalShovelItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){
        return new MetalShovel(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }


    public static Item createMetalPickAxeItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){
        return new MetalPickAxe(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }
    public static Item createMetalSwordItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){

        return new MetalSword(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }
    public static Item createMetalDaggerItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){

        return new MetalDagger(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }

    public static Item createMetalHammerItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){

        return new MetalHammer(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }

    public static Item createSilverHammerItem(ToolMaterial material, int finalDamage,float finalDamageSpeed){

        return new SilverHammer(material,new Item.Settings().
                attributeModifiers(MiningToolItem.createAttributeModifiers(material,-1+finalDamage,-4+finalDamageSpeed))
        );
    }


    public static void registerModItemTools() {


        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","adamantium_hammer"),ADAMANTIUM_HAMMER);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","copper_hammer"),COPPER_HAMMER);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","silver_hammer"),SILVER_HAMMER);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","gold_hammer"),GOLD_HAMMER);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","iron_hammer"),IRON_HAMMER);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","mithril_hammer"), MITHRIL_HAMMER);

        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","adamantium_axe"), ADAMANTIUM_AXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","mithril_axe"), MITHRIL_AXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","iron_axe"),IRON_AXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","copper_axe"),COPPER_AXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","silver_axe"),SILVER_AXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","gold_axe"),GOLD_AXE);

        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","adamantium_hoe"), ADAMANTIUM_HOE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","mithril_hoe"), MITHRIL_HOE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","iron_hoe"),IRON_HOE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","copper_hoe"),COPPER_HOE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","silver_hoe"),SILVER_HOE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","gold_hoe"),GOLD_HOE);

        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","adamantium_pickaxe"), ADAMANTIUM_PICKAXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","mithril_pickaxe"), MITHRIL_PICKAXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","iron_pickaxe"),IRON_PICKAXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","copper_pickaxe"),COPPER_PICKAXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","silver_pickaxe"),SILVER_PICKAXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","gold_pickaxe"),GOLD_PICKAXE);

        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","adamantium_sword"), ADMANTIUM_SWORD);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","mithril_sword"), MITHRIL_SWORD);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","iron_sword"),IRON_SWORD);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","copper_sword"),COPPER_SWORD);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","silver_sword"),SILVER_SWORD);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","gold_sword"),GOLD_SWORD);


        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","adamantium_shovel"), ADAMANTIUM_SHOVEL);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","mithril_shovel"), MITHRIL_SHOVEL);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","iron_shovel"),IRON_SHOVEL);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","copper_shovel"),COPPER_SHOVEL);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","silver_shovel"),SILVER_SHOVEL);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","gold_shovel"),GOLD_SHOVEL);


        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","adamantium_dagger"), ADMANTIUM_DAGGER);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","mithril_dagger"), MITHRIL_DAGGER);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","iron_dagger"),IRON_DAGGER);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","copper_dagger"),COPPER_DAGGER);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","silver_dagger"),SILVER_DAGGER);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","gold_dagger"),GOLD_DAGGER);



        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","flint_hatchet"),FLINT_HATCHET);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","flint_axe"),FLINT_AXE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","flint_knife"), FLINT_KNIFE);
        Registry.register(Registries.ITEM, Identifier.of("miteequilibrium","flint_shovel"),FLINT_SHOVEL);
    }
}
