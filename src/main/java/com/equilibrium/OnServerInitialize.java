package com.equilibrium;

import com.equilibrium.block.CraftingDifficultyHelper;
import com.equilibrium.network.*;
import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
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

import static com.equilibrium.block.CraftingDifficultyHelper.initCraftingDifficulties;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.initGameRules;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(OnServerInitialize.MOD_ID)
public class OnServerInitialize {
    public static final String MOD_ID = "miteequilibrium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    //服务器状态
    public StateSaverAndLoader serverState;

    public static final BooleanProperty FERTILIZED = BooleanProperty.create("fertilized");

    public static final IntegerProperty GRASSBLOCK_POLLUTED = IntegerProperty.create("grassblock_polluted", 0, 7);

    public static final BooleanProperty CROP_IS_ILLNESS = BooleanProperty.create("crop_illness");

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
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



        modEventBus.register(this);
    }

    /**
     * 在所有注册完成后初始化依赖物品/方块的逻辑
     */
    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        // 此时所有物品、方块均已注册，字段非 null
        event.enqueueWork(CraftingDifficultyHelper::initCraftingDifficulties);
    }


}
