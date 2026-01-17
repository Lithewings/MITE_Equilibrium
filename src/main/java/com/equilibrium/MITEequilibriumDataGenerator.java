package com.equilibrium;


import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class MITEequilibriumDataGenerator implements DataGeneratorEntrypoint {


	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator){
		FabricDataGenerator.Pack pack=fabricDataGenerator.createPack();
//		pack.addProvider(ModRecipeGenerator::new);
//		pack.addProvider(ModModelProvider::new);
//		pack.addProvider(ModLanguageTranslatorZhCn::new);
//		pack.addProvider(TagsProvider::new);
//		pack.addProvider(ModLootTableProvider::new);
//		pack.addProvider(ModEnchantmentDynamicRegistryProvider::new);
//		pack.addProvider(ModAdventureProvider::new);

	}

}
