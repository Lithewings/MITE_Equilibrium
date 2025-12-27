package com.equilibrium.mixin.vanilla_blocksmixin;

import com.equilibrium.network.S2CStockChangeGrassColorPacket;
import com.equilibrium.tags.ModEntityTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.equilibrium.MITEequilibrium.*;
import static com.equilibrium.network.S2CStockChangeGrassColorPacket.BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP;
import static com.equilibrium.util.WorldMoonPhasesSelector.setAndGetMoonType;

@Mixin(GrassBlock.class)
public abstract class GrassBlockMixin extends SpreadableBlock implements Fertilizable {
    public GrassBlockMixin(Settings settings) {
        super(settings);
    }

    @Unique
    private static boolean canSurvive(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.SNOW) && (Integer)blockState.get(SnowBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getLevel() == 8) {
            return false;
        } else {
            int i = ChunkLightProvider.getRealisticOpacity(world, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(world, blockPos));
            return i < world.getMaxLightLevel();
        }
    }
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //草方块无法生存时,转化为泥土
        if (!canSurvive(state, world, pos)) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            return;
        }
        super.randomTick(state, world, pos, random);

        int polluteLevel = world.getBlockState(pos).get(GRASSBLOCK_POLLUTED);
        //数值更新
        if (world.getRandom().nextInt(2) == 0 && polluteLevel >= 1) {
            //随机刻选中时,发包更新状态
            int finalPolluteLevel = Math.clamp(polluteLevel - 1, 0, 7);
            world.setBlockState(pos, state.with(GRASSBLOCK_POLLUTED, finalPolluteLevel), Block.NOTIFY_ALL);
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
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(player instanceof ServerPlayerEntity serverPlayerEntityplayer)
            serverPlayerEntityplayer.sendMessage(Text.of("The GrassBlock Pollute Level is : "+state.get(GRASSBLOCK_POLLUTED)));

        return ActionResult.PASS;
    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
       super.onStateReplaced(state,world,pos,newState,moved);
       //污染的草方块被打碎时,删除该状态
       BLOCK_POS_INTEGER_CONCURRENT_HASH_MAP.remove(pos);

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(GRASSBLOCK_POLLUTED);
    }

    @Inject(method = "<init>",at = @At(value = "TAIL"))
    public void GrassBlock(Settings settings, CallbackInfo ci) {
        this.stateManager.getDefaultState().with(GRASSBLOCK_POLLUTED, 0);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
        if(!this.getDefaultState().contains(GRASSBLOCK_POLLUTED))
            return;
        boolean isStock = entity.getType().isIn(ModEntityTags.STOCKS);
        boolean b1 = (setAndGetMoonType(world).equals("bloodMoon"));
        boolean b2 = world.isSkyVisible(pos.up());
        boolean b3 = entity.getType().isIn(ModEntityTags.STOCKS);
        if(isStock && entity.getRandom().nextInt(128)==0 && b1 && b2 && b3){
            if(world.canSetBlock(pos.up())&&world.getBlockState(pos.up()).isAir())
                world.setBlockState(pos.up(),Blocks.WITHER_ROSE.getDefaultState());
//            world.spawnEntity(new ItemEntity(world,pos.getX(),pos.getY()+1,pos.getZ(),Items.WITHER_ROSE.getDefaultStack()));
        }
        if (isStock && entity.getRandom().nextInt(128)==0) {
            int polluteLevel = 0;
            world.setBlockState(pos, state.with(GRASSBLOCK_POLLUTED, Math.clamp(polluteLevel+1,0,7)), Block.NOTIFY_ALL);
            //告诉客户端渲染草地被污染的等级
            ServerToClientUpdateGrassBlockState(world, pos, polluteLevel);
        }
    }

    @Unique
    private static void ServerToClientUpdateGrassBlockState(World world, BlockPos pos, int polluteLevel) {
        if(!world.isClient()) {
            //如果区块被加载,那么默认128格内的玩家可以看到渲染的污染草地颜色,返回一个player,一定是非空的instance
            if(world.getClosestPlayer(pos.getX(),pos.getY(),pos.getZ(),128,false) instanceof ServerPlayerEntity player)
                ServerPlayNetworking.send(
                        player,
                        new S2CStockChangeGrassColorPacket.GrassColorPayload(pos, polluteLevel)
                );
        }
    }
}
