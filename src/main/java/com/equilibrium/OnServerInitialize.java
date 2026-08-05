package com.equilibrium;

import com.equilibrium.block.CraftingDifficultyHelper;
import com.equilibrium.block.UseBlockActionUtil;
import com.equilibrium.block.furnace_and_its_entity.FurnaceEntityRegistry;
import com.equilibrium.item.armor.ArmorItems;
import com.equilibrium.item.coin.CoinItems;
import com.equilibrium.item.food.FoodItems;
import com.equilibrium.item.material.MaterialItems;
import com.equilibrium.item.ModItemGroups;
import com.equilibrium.item.miscellaneous.MiscellaneousItems;
import com.equilibrium.item.tool.ToolItems;
import com.equilibrium.item.vanilla_modify.MaxDamageModifier;
import com.equilibrium.item.vanilla_modify.MaxStackSizeModifier;
import com.equilibrium.network.*;
import com.equilibrium.server_and_client.server.SoundEventRegistry;
import com.equilibrium.server_and_client.server.command.ServerCommands;
import com.equilibrium.server_and_client.server.event.*;
import com.equilibrium.server_and_client.server.persistent_state.MapNbtSerializer;
import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import com.equilibrium.status.RegisterStatusEffect;
import com.equilibrium.structure.StructureRegister;
import com.equilibrium.util.AdvancementRemover;
import com.equilibrium.util.BooleanStorageUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.LevelResource;
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.equilibrium.GlobalModConfig.initConfig;
import static com.equilibrium.block.CraftingDifficultyHelper.initCraftingDifficulties;
import static com.equilibrium.difficulty_entry.DifficultyEntryGetter.isAnyExtraEntryExisting;
import static com.equilibrium.difficulty_entry.DifficultyEntryRegister.initGameRules;

import static com.equilibrium.item.vanilla_modify.FoodComponentModifier.foodComponentModify;
import static com.equilibrium.server_and_client.server.event.CropIllnessEvent.updateCropBlockPos;
import static com.equilibrium.server_and_client.server.moonphase_tasks.MoonPhaseEvent.moonPhaseEvent;
import static com.equilibrium.structure.ModPlacementGenerator.registerModOre;
import static com.equilibrium.tags.ModBlockTags.registerModBlockTags;
import static com.equilibrium.tags.ModEntityTags.registerModEntityTags;
import static com.equilibrium.tags.ModItemTags.registerModItemTags;
import static com.equilibrium.util.BooleanStorageUtil.loadWorldInformation;

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


        //方块等注册暂时使用@EventBusSubscriber

        //熔炉方块实体
        FurnaceEntityRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);

        MaterialItems.ITEMS.register(modEventBus);
        FoodItems.ITEMS.register(modEventBus);
        ArmorItems.ITEMS.register(modEventBus);
        ToolItems.ITEMS.register(modEventBus);
        CoinItems.ITEMS.register(modEventBus);
        MiscellaneousItems.ITEMS.register(modEventBus);

        //物品栏注册

        ModItemGroups.TABS.register(modEventBus);

        // 注册声音事件
        SoundEventRegistry.SOUND_EVENTS.register(modEventBus);

        // 注册矿物(Fabric)
        registerModOre();

        //监听事件注册
        NeoForge.EVENT_BUS.addListener(this::onServerAboutToStart);

        //注册结构
        StructureRegister.FEATURES.register(modEventBus);

        //效果注册
        RegisterStatusEffect.MOB_EFFECTS.register(modEventBus);

    }

    @SubscribeEvent
    //需要进行手动注册到addListener中
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        StructureRegister.addFeatureToBiomes();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {


            //成就删除
            AdvancementRemover.removeAllMinecraftAdvancements(server.getAdvancements().tree());


            //锁定游戏难度
            server.setDifficultyLocked(true);

            //读取服务器持久状态数据
            serverState = StateSaverAndLoader.getServerState(server);


            CropIllnessEvent.CROP_BLOCK_POS = MapNbtSerializer.fromNbt(
                    serverState.mapNbt2,
                    dis -> {
                        try {
                            return new BlockPos(dis.readInt(), dis.readInt(), dis.readInt());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    dis -> {
                        try {
                            return dis.readBoolean();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    ConcurrentHashMap::new);


            //之前的土地污染map被存在了nbt里,现在把它取出来
            //读取土地污染map

            S2CStockChangeGrassColorPacket.BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP = MapNbtSerializer.fromNbt(
                    serverState.mapNbt1,
                    dis -> {
                        try {
                            return new BlockPos(dis.readInt(), dis.readInt(), dis.readInt());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    dis -> {
                        try {
                            return dis.readInt();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    ConcurrentHashMap::new
            );
        });


        // 注册服务器 tick 事件
        ServerTickEvents.START_SERVER_TICK.register(server -> {

            serverState = StateSaverAndLoader.getServerState(server);

            //更新服务器状态,在这里修改的所有数据都会被保存
            if (tickCount % (TICK_INTERVAL / 10) == 0) {

                //保存土地污染map,这个map被网络包定义的一个static的map共享,现在把它读取到nbt然后保存,不需要传参因为可以断定要传送的数据位置
                serverState.saveMapNbtToBuffer1();
                //保存生病农作物的map
                serverState.saveMapNbtToBuffer2();

                boolean isGrandStageClear = false;
                Path path = server.getWorldPath(LevelResource.ROOT).normalize().resolve("WorldInformationRecorder.dat");
                BooleanStorageUtil.WorldInformationRecorder worldInformationRecorder = loadWorldInformation(path.toString());
                if (worldInformationRecorder != null && worldInformationRecorder.getIsGrandStageClear()==true) {
                    isGrandStageClear  = true;
                }

                if((isAnyExtraEntryExisting(server,null))&& !isGrandStageClear){
                    server.setDifficulty(Difficulty.HARD,true);
                    boolean allowCommands = server.getWorldData().isAllowCommands();
                    List<ServerPlayer> playerList = server.getPlayerList().getPlayers();
                    boolean isAnyCreativeOrSpectator = playerList.stream().allMatch(player -> player.isCreative()||player.isSpectator());
                    boolean isPlayerExisting = !playerList.isEmpty();
                    if(isPlayerExisting && (allowCommands || isAnyCreativeOrSpectator)){
                        playerList.forEach(serverPlayerEntity -> serverPlayerEntity.displayClientMessage(Component.literal("检测到错误的世界设置,服务器将在不久后强制清除玩家"),true) );
                        new Thread(() -> {
                            try {
                                Thread.sleep(8000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return; // 被中断则不再执行后续任务
                            }
                            if (server.isRunning()) {
                                server.execute(() -> {
                                    if (server.isRunning()) {
                                        server.getPlayerList().removeAll();
                                    }
                                });
                            }
                        }).start();
                    }


                }


            }


            // 每隔 TICK_INTERVAL 次 tick 触发一次检查
            tickCount++;
            //获取时间,得到月相,决定是否触发月相事件

            ServerLevel serverOverWorld = moonPhaseEvent(server);
            //护甲更新,玩家游戏模式更新,作物状态更新
            if (tickCount % (TICK_INTERVAL / 10) == 0) {
                for (ServerPlayer serverPlayerEntity : server.getPlayerList().getPlayers()) {
                    UpdateArmorEvent.updatePlayerArmor(serverPlayerEntity);
//					if(serverPlayerEntity.isCreative())
//						serverPlayerEntity.changeGameMode(GameMode.SURVIVAL);
                }
                updateCropBlockPos(serverOverWorld);
            }

            if (tickCount >= TICK_INTERVAL) {
                tickCount = 0; // 重置 tick 计数器
            }
        });

        //使用物品监听器,能不在这里写就不要在这里写,用物品自带的onUse方法
        //合成金属镐监听器
        CraftingMetalPickAxeCallback.EVENT.register(OnCraftingMetalPickAxe::onCraftingMetalPickAxe);
        //命令注册
        CommandRegistrationCallback.EVENT.register(ServerCommands::registerCommands);


        UseItemCallback.EVENT.register(OnItemUseEvent::onUseItem);

        //移除原版工作台方块,创造模式除外
        UseBlockCallback.EVENT.register(UseBlockActionUtil::canUseVanillaCraftingTable);
    }

    /**
     * 在所有注册完成后初始化依赖物品/方块的逻辑
     */
    private static final int TICK_INTERVAL = 500; // 每隔500 tick检查一次
    private int tickCount = 0; // 记录当前 tick

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        // 此时所有物品、方块均已注册，字段非 null
        event.enqueueWork(CraftingDifficultyHelper::initCraftingDifficulties);



        registerModBlockTags();
        registerModEntityTags();
        registerModItemTags();
        //配置类(Fabric风格)
        initConfig();

        //原版物品修改
        DefaultItemComponentEvents.MODIFY.register(new MaxStackSizeModifier());
        DefaultItemComponentEvents.MODIFY.register(new MaxDamageModifier());
        //食物修改
        foodComponentModify();





    }


}

