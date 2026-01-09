package com.equilibrium.mixin.portal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.Portal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static com.equilibrium.OnServerInitialize.MOD_ID;


@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin extends Block implements Portal{


    @Shadow @Nullable protected abstract TeleportTarget getOrCreateExitPortalTarget(ServerWorld world, Entity entity, BlockPos pos, BlockPos scaledPos, boolean inNether, WorldBorder worldBorder);

    @Shadow @Final private static Logger LOGGER;

    public NetherPortalBlockMixin(Settings settings) {
        super(settings);
    }




    @Unique
    private static final String TRANSPORT_TARGET1="You will transport to overworld";
    @Unique
    private static final String TRANSPORT_TARGET2="You will transport to underworld";
    @Unique
    private static final String TRANSPORT_TARGET3="You will transport to nether";
    @Unique
    private static String getTeleportWorld(World world, Entity entity) {

        //获取目前的世界类型(访问注册方法)
        RegistryKey<World> registryKey = world.getRegistryKey();
        //传送后的世界类型
        RegistryKey<World> teleport;
        //避免空指针错误

        if (registryKey == null) {
            return "Not an illegal transportation";
        } else {
            boolean atBottom = Math.abs(entity.getY()-world.getBottomY())<5;
            //3格的缓冲区,防止玩家在地下世界本应该传送到下界的时候跳跃造成y变化导致传送到了主世界
            boolean buffer = Math.abs(entity.getY()-world.getBottomY())>=5 && Math.abs(entity.getY()-world.getBottomY())<8;

            if(world.getRegistryKey()==overworld && atBottom){
                teleport=underworld;
            }
            else if (world.getRegistryKey()==overworld && !atBottom && !buffer) {
                teleport=overworld;
            } else if (world.getRegistryKey()==underworld && !atBottom && !buffer ) {
                teleport=overworld;
            } else if (world.getRegistryKey()==underworld &&  atBottom) {
                teleport=nether;
            } else if(world.getRegistryKey()==nether){
                teleport=underworld;
            }
            else {
                //不传,获取目前世界,因为在缓冲区上
                teleport=world.getRegistryKey();
            }
            String worldType;
            if(teleport==underworld){
                worldType= TRANSPORT_TARGET2;
            } else if (teleport==overworld) {
                worldType= TRANSPORT_TARGET1;
            } else if (teleport==nether) {
                worldType= TRANSPORT_TARGET3;
            }else{
                worldType="Not an illegal transportation";
            }

            return worldType;
        }


    }




    @Inject(method = "onEntityCollision",at = @At(value = "HEAD"),cancellable = true)
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        ci.cancel();

//        public boolean canUsePortals(boolean allowVehicles) {
//            return (allowVehicles || !this.hasVehicle()) && this.isAlive();
//        }

        if (entity.canUsePortals(true)) {
            entity.tryUsePortal(this, pos);
            if (entity instanceof PlayerEntity player) {
                String teleport = getTeleportWorld(world, entity);
                if (Objects.equals(teleport, TRANSPORT_TARGET1)) {
                    player.sendMessage(Text.of(Text.translatable("teleport.overworld").formatted(Formatting.YELLOW)),true);
                } else if (Objects.equals(teleport, TRANSPORT_TARGET2)) {
                    player.sendMessage(Text.of(Text.translatable("teleport.underworld").formatted(Formatting.YELLOW)),true);
                } else if (Objects.equals(teleport, TRANSPORT_TARGET3))
                    player.sendMessage(Text.of(Text.translatable("teleport.nether").formatted(Formatting.YELLOW)),true);
                else {
                    player.sendMessage(Text.of("You shouldn't receive the mesaage, it might you will teleport to the wrong dimension that no supporting"),true);
                }
            }


        }
    }





    @Unique
    private static final RegistryKey<World> overworld = World.OVERWORLD;
    @Unique
    private static final RegistryKey<World> nether = World.NETHER;
    @Unique
    private static final RegistryKey<World> underworld = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID, "underworld"));





    @Unique
    public TeleportTarget toSpawn(ServerWorld world, Entity entity) {
        RegistryKey<World> registryKey = world.getRegistryKey();
        ServerWorld serverWorld = world.getServer().getWorld(registryKey);
        if (serverWorld == null) {
            return null;
        } else {
            BlockPos blockPos = serverWorld.getSpawnPos();
            float f = entity.getYaw();
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                return serverPlayerEntity.getRespawnTarget(false, TeleportTarget.NO_OP);
            }
            Vec3d vec3d = entity.getWorldSpawnPos(serverWorld, blockPos).toBottomCenterPos();
            return new TeleportTarget(
                    serverWorld,
                    vec3d,
                    entity.getVelocity(),
                    f,
                    entity.getPitch(),
                    TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)
            );
        }
    }



    @Inject(method = "createTeleportTarget",at=@At(value = "HEAD"),cancellable = true)
    public void createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos, CallbackInfoReturnable<TeleportTarget> cir) {
        cir.cancel();

        //获取目前的世界类型(访问注册方法)
        RegistryKey<World> registryKey = world.getRegistryKey();
        //传送后的世界类型
        RegistryKey<World> teleport;

        if (registryKey == null) {
            cir.setReturnValue(null);
        } else {
            ServerWorld serverWorld;
            //缩放条件
            boolean inNether = world.getRegistryKey() == World.NETHER;
            //世界边界限制
            WorldBorder worldBorder = world.getWorldBorder();
            //缩放倍率
            double d = DimensionType.getCoordinateScaleFactor(world.getDimension(), world.getDimension());
            BlockPos blockPos = worldBorder.clamp(entity.getX() * d, entity.getY(), entity.getZ() * d);

            boolean atBottom = Math.abs(entity.getY()-world.getBottomY())<5;

            //3格的缓冲区,比如防止玩家在地下世界本应该传送到下界的时候跳跃造成y变化导致传送到了主世界
            boolean buffer = Math.abs(entity.getY()-world.getBottomY())>=5 && Math.abs(entity.getY()-world.getBottomY())<8;


            //world.getRegistryKey()获取现在的世界
            //主世界传地下世界,地下世界也可以传主世界






            if(world.getRegistryKey()==overworld && atBottom){
                teleport=underworld;
            } else if (world.getRegistryKey()==overworld && !atBottom && !buffer) {
                //不在底部,且不在缓冲区上
                teleport=overworld;
                if(entity instanceof ServerPlayerEntity player){
                    serverWorld=world.getServer().getWorld(teleport);

                    StatusEffectInstance statusEffectInstance1 = new StatusEffectInstance(StatusEffects.BLINDNESS, 60,255, false,false,false);
                    StatusEffectUtil.addEffectToPlayersWithinDistance(world, entity, entity.getPos(), 4, statusEffectInstance1,100);

                    StatusEffectInstance statusEffectInstance4 = new StatusEffectInstance(StatusEffects.SLOWNESS,40,255, false,false,false);
                    StatusEffectUtil.addEffectToPlayersWithinDistance(world, entity, entity.getPos(), 4, statusEffectInstance4,100);

                    StatusEffectInstance statusEffectInstance3 = new StatusEffectInstance(StatusEffects.NAUSEA,100,255, false,false,false);
                    StatusEffectUtil.addEffectToPlayersWithinDistance(world, entity, entity.getPos(), 4, statusEffectInstance3,100);


                    player.teleportTo(toSpawn(serverWorld,player));

//                    for(int i=0;i<=3;i++){
//                        world.breakBlock(spawnPos.add(1,i,1),true);
//                        world.breakBlock(spawnPos.add(1,i,0),true);
//                        world.breakBlock(spawnPos.add(1,i,-1),true);
//                        world.breakBlock(spawnPos.add(0,i,1),true);
//                        world.breakBlock(spawnPos.add(0,i,0),true);
//                        world.breakBlock(spawnPos.add(0,i,-1),true);
//                        world.breakBlock(spawnPos.add(-1,i,1),true);
//                        world.breakBlock(spawnPos.add(-1,i,0),true);
//                        world.breakBlock(spawnPos.add(-1,i,-1),true);
//                    }

                    return;
                }
                else{
                    teleport=underworld;
                }
            } else if (world.getRegistryKey()==underworld && !atBottom &&!buffer) {
                //不在底部,也不在缓冲区上,传送到主世界
                teleport=overworld;



            } else if (world.getRegistryKey()==underworld &&  atBottom) {
                teleport=nether;
            } else if(world.getRegistryKey()==nether){
                teleport=underworld;

            }
            else {
                teleport=world.getRegistryKey();
            }


            serverWorld=world.getServer().getWorld(teleport);


            cir.setReturnValue(this.getOrCreateExitPortalTarget(serverWorld, entity, pos, blockPos, inNether, worldBorder));

        }}











    }







