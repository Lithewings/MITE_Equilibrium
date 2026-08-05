package com.equilibrium;

import com.equilibrium.block.CraftingDifficultyHelper;
import com.equilibrium.block.anvil.AnvilBlocks;
import com.equilibrium.block.crafting_table.CraftingTableBlocks;
import com.equilibrium.block.enchanting_table.EnchantingTableBlocks;
import com.equilibrium.block.furnace.FurnaceBlocks;
import com.equilibrium.block.material.MaterialBlocks;
import com.equilibrium.block.miscellaneous.MiscellaneousBlocks;
import com.equilibrium.block.ore.OreBlocks;
import com.equilibrium.item.armor.ArmorItems;
import com.equilibrium.item.coin.CoinItems;
import com.equilibrium.item.food.FoodItems;
import com.equilibrium.item.material.MaterialItems;
import com.equilibrium.item.ModItemGroups;
import com.equilibrium.item.miscellaneous.MiscellaneousItems;
import com.equilibrium.item.tool.ToolItems;
import com.equilibrium.network.*;
import com.equilibrium.server_and_client.server.SoundEventRegistry;
import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.equilibrium.status.RegisterStatusEffect;
import com.equilibrium.structure.StructureRegister;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.slf4j.Logger;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.equilibrium.GlobalModConfig.initConfig;
import static com.equilibrium.block.CraftingDifficultyHelper.initCraftingDifficulties;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.initGameRules;

import static com.equilibrium.structure.ModPlacementGenerator.registerModOre;
import static com.equilibrium.tags.ModBlockTags.registerModBlockTags;
import static com.equilibrium.tags.ModEntityTags.registerModEntityTags;
import static com.equilibrium.tags.ModItemTags.registerModItemTags;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(OnServerInitialize.MOD_ID)
public class OnServerInitialize {
    public static final String MOD_ID = "miteequilibrium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final BooleanProperty FERTILIZED = BooleanProperty.create("fertilized");
    public static final IntegerProperty GRASSBLOCK_POLLUTED = IntegerProperty.create("grassblock_polluted", 0, 7);
    public static final BooleanProperty CROP_IS_ILLNESS = BooleanProperty.create("crop_illness");
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    //服务器状态
    public StateSaverAndLoader serverState;
    public OnServerInitialize(IEventBus modEventBus,ModContainer modContainer){
        //初始化游戏规则
        initGameRules();

        //S->C,发包
        S2CStockChangeGrassColorPacket.registerOnServer();
        S2CIllnessTextureBooleanPacket.registerOnServer();
        S2CGameRuleSyncPayloadForBooleanPacket.registerOnServer();

        //C->S,发包、接收
        C2SClickTimesPacket.registerOnServer();
        C2STriggerContentChangePacket.registerOnServer();


        //DeferredRegister风格下,所有要注册的物品,先触发类加载
        //方块等注册暂时使用@EventBusSubscriber + helper.register方法

        //物品注册
        MaterialItems.ITEMS.register(modEventBus);
        FoodItems.ITEMS.register(modEventBus);
        ArmorItems.ITEMS.register(modEventBus);
        ToolItems.ITEMS.register(modEventBus);
        CoinItems.ITEMS.register(modEventBus);
        MiscellaneousItems.ITEMS.register(modEventBus);

        //方块物品注册
        AnvilBlocks.ITEMS.register(modEventBus);
        CraftingTableBlocks.ITEMS.register(modEventBus);
        EnchantingTableBlocks.ITEMS.register(modEventBus);
        FurnaceBlocks.ITEMS.register(modEventBus);
        MaterialBlocks.ITEMS.register(modEventBus);
        MiscellaneousBlocks.ITEMS.register(modEventBus);
        OreBlocks.ITEMS.register(modEventBus);

        AnvilBlocks.BLOCKS.register(modEventBus);
        CraftingTableBlocks.BLOCKS.register(modEventBus);
        EnchantingTableBlocks.BLOCKS.register(modEventBus);
        FurnaceBlocks.BLOCKS.register(modEventBus);
        MaterialBlocks.BLOCKS.register(modEventBus);
        MiscellaneousBlocks.BLOCKS.register(modEventBus);
        OreBlocks.BLOCKS.register(modEventBus);

        //物品栏注册
        ModItemGroups.TABS.register(modEventBus);

        // 注册声音事件
        SoundEventRegistry.SOUND_EVENTS.register(modEventBus);

        // 注册矿物(Fabric)
        registerModOre();

        //注册结构
        NeoForge.EVENT_BUS.addListener(OnServerInitialize::onServerAboutToStart);
        StructureRegister.FEATURES.register(modEventBus);

        //效果注册
        RegisterStatusEffect.MOB_EFFECTS.register(modEventBus);
    }

    @SubscribeEvent
    //需要进行手动注册到addListener中
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        StructureRegister.addFeatureToBiomes();
    }

    /**
     * 在所有注册完成后初始化依赖物品/方块的逻辑
     */
    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        // 此时所有物品、方块均已注册，字段非 null
        event.enqueueWork(CraftingDifficultyHelper::initCraftingDifficulties);



        registerModBlockTags();
        registerModEntityTags();
        registerModItemTags();
        initConfig();

    }


}

