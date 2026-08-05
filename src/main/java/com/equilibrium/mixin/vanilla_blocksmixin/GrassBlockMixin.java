package com.equilibrium.mixin.vanilla_blocksmixin;

import com.equilibrium.network.S2CStockChangeGrassColorPacket;
import com.equilibrium.tags.ModEntityTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.OnServerInitialize.*;
import static com.equilibrium.network.S2CStockChangeGrassColorPacket.BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP;
import static com.equilibrium.server_and_client.server.moonphase_tasks.WorldMoonPhasesSelector.calculateMoonType;

@Mixin(GrassBlock.class)
public abstract class GrassBlockMixin extends SpreadingSnowyDirtBlock implements BonemealableBlock {
    public GrassBlockMixin(Properties settings) {
        super(settings);
    }

    @Unique
    private static boolean canBeGrass(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.above();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.is(Blocks.SNOW) && (Integer)blockState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngine.getLightBlockInto(world, state, pos, blockState, blockPos, Direction.UP, blockState.getLightBlock(world, blockPos));
            return i < world.getMaxLightLevel();
        }
    }
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        //草方块无法生存时,转化为泥土
        if (!canBeGrass(state, world, pos)) {
            world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
            return;
        }
        super.randomTick(state, world, pos, random);

        int polluteLevel = world.getBlockState(pos).getValue(GRASSBLOCK_POLLUTED);
        //数值更新
        if (world.getRandom().nextInt(64) == 0 && polluteLevel >= 1) {
            //随机刻选中时,发包更新状态
            int finalPolluteLevel = Math.clamp(polluteLevel - 1, 0, 7);
            world.setBlock(pos, state.setValue(GRASSBLOCK_POLLUTED, finalPolluteLevel), Block.UPDATE_ALL);
            //将新值写入并发哈希表,然后发包给客户端
            BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP.put(pos, finalPolluteLevel);
            //更新状态
            //在客户端private static int getModGrassColor(BlockRenderView world, BlockPos pos, ColorResolver resolver) 中
            //当检测到发包的污染值为0时,就会把这个键从HashMap中删除,释放空间
            ServerToClientUpdateGrassBlockState(world, pos, finalPolluteLevel);
        }

        //定期清理
        if (world.getRandom().nextInt(8) == 0) {
            //定期清理零元素
            BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP.forEach((blockPos, mapPolluteLevel) -> {
                if (mapPolluteLevel == 0)
                    BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP.remove(blockPos);
            });
            //无实际意义,这里发包只是为了触发更新
            ServerToClientUpdateGrassBlockState(world, pos, polluteLevel);
        }

    }
//    @Override
//    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
//        if(player instanceof ServerPlayerEntity serverPlayerEntityplayer)
//            serverPlayerEntityplayer.sendMessage(Text.of("The GrassBlock Pollute Level is : "+state.get(GRASSBLOCK_POLLUTED)));
//
//        return ActionResult.PASS;
//    }
    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
       super.onRemove(state,world,pos,newState,moved);
       //污染的草方块被打碎时,删除该状态
       BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP.remove(pos);

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GRASSBLOCK_POLLUTED);
    }

    @Inject(method = "<init>",at = @At(value = "TAIL"))
    public void GrassBlock(Properties settings, CallbackInfo ci) {
        this.stateDefinition.any().setValue(GRASSBLOCK_POLLUTED, 0);
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(world, pos, state, entity);
        if(!this.defaultBlockState().hasProperty(GRASSBLOCK_POLLUTED))
            return;
        boolean isStock = entity.getType().is(ModEntityTags.STOCKS);
        boolean b1 = (calculateMoonType(world).equals("bloodMoon"));
        boolean b2 = world.canSeeSky(pos.above());
        boolean b3 = entity.getType().is(ModEntityTags.STOCKS);
        if(isStock && entity.getRandom().nextInt(128)==0 && b1 && b2 && b3){
            if(world.isLoaded(pos.above())&&world.getBlockState(pos.above()).isAir())
                world.setBlockAndUpdate(pos.above(),Blocks.WITHER_ROSE.defaultBlockState());
//            world.spawnEntity(new ItemEntity(world,pos.getX(),pos.getY()+1,pos.getZ(),Items.WITHER_ROSE.getDefaultStack()));
        }
        if (isStock && entity.getRandom().nextInt(128)==0) {
            int polluteLevel = state.getValue(GRASSBLOCK_POLLUTED);
            world.setBlock(pos, state.setValue(GRASSBLOCK_POLLUTED, Math.clamp(polluteLevel+1,0,7)), Block.UPDATE_ALL);
            //告诉客户端渲染草地被污染的等级
            ServerToClientUpdateGrassBlockState(world, pos, polluteLevel);
        }
    }

    @Unique
    private static void ServerToClientUpdateGrassBlockState(Level world, BlockPos pos, int polluteLevel) {
        if(!world.isClientSide()) {
            //如果区块被加载,那么默认128格内的玩家可以看到渲染的污染草地颜色,返回一个player,一定是非空的instance
            if(world.getNearestPlayer(pos.getX(),pos.getY(),pos.getZ(),128,false) instanceof ServerPlayer player)
                ServerPlayNetworking.send(
                        player,
                        new S2CStockChangeGrassColorPacket.GrassColorPayload(pos, polluteLevel)
                );
        }
    }
}
