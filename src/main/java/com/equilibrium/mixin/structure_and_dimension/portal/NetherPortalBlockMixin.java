package com.equilibrium.mixin.structure_and_dimension.portal;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
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


    @Shadow
    @Nullable protected abstract DimensionTransition getExitPortal(ServerLevel world, Entity entity, BlockPos pos, BlockPos scaledPos, boolean inNether, WorldBorder worldBorder);


    public NetherPortalBlockMixin(Properties settings) {
        super(settings);
    }




    @Unique
    private static final String TRANSPORT_TARGET1="You will transport to overworld";
    @Unique
    private static final String TRANSPORT_TARGET2="You will transport to underworld";
    @Unique
    private static final String TRANSPORT_TARGET3="You will transport to nether";
    @Unique
    private static String getTeleportWorld(Level world, Entity entity) {

        //获取目前的世界类型(访问注册方法)
        ResourceKey<Level> registryKey = world.dimension();
        //传送后的世界类型
        ResourceKey<Level> teleport;
        //避免空指针错误

        if (registryKey == null) {
            return "Not an illegal transportation";
        } else {
            boolean atBottom = Math.abs(entity.getY()-world.getMinBuildHeight())<5;
            //3格的缓冲区,防止玩家在地下世界本应该传送到下界的时候跳跃造成y变化导致传送到了主世界
            boolean buffer = Math.abs(entity.getY()-world.getMinBuildHeight())>=5 && Math.abs(entity.getY()-world.getMinBuildHeight())<8;

            if(world.dimension()==overworld && atBottom){
                teleport=underworld;
            }
            else if (world.dimension()==overworld && !atBottom && !buffer) {
                teleport=overworld;
            } else if (world.dimension()==underworld && !atBottom && !buffer ) {
                teleport=overworld;
            } else if (world.dimension()==underworld &&  atBottom) {
                teleport=nether;
            } else if(world.dimension()==nether){
                teleport=underworld;
            }
            else {
                //不传,获取目前世界,因为在缓冲区上
                teleport=world.dimension();
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




    @Inject(method = "entityInside",at = @At(value = "HEAD"),cancellable = true)
    protected void onEntityCollision(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo ci) {
        ci.cancel();

//        public boolean canUsePortals(boolean allowVehicles) {
//            return (allowVehicles || !this.hasVehicle()) && this.isAlive();
//        }

        if (entity.canUsePortal(true)) {
            entity.setAsInsidePortal(this, pos);
            if (entity instanceof Player player) {
                String teleport = getTeleportWorld(world, entity);
                if (Objects.equals(teleport, TRANSPORT_TARGET1)) {
                    player.displayClientMessage(Component.translationArg(Component.translatable("teleport.overworld").withStyle(ChatFormatting.YELLOW)),true);
                } else if (Objects.equals(teleport, TRANSPORT_TARGET2)) {
                    player.displayClientMessage(Component.translationArg(Component.translatable("teleport.underworld").withStyle(ChatFormatting.YELLOW)),true);
                } else if (Objects.equals(teleport, TRANSPORT_TARGET3))
                    player.displayClientMessage(Component.translationArg(Component.translatable("teleport.nether").withStyle(ChatFormatting.YELLOW)),true);
                else {
                    player.displayClientMessage(Component.nullToEmpty("You shouldn't receive the mesaage, it might you will teleport to the wrong dimension that no supporting"),true);
                }
            }


        }
    }





    @Unique
    private static final ResourceKey<Level> overworld = Level.OVERWORLD;
    @Unique
    private static final ResourceKey<Level> nether = Level.NETHER;
    @Unique
    private static final ResourceKey<Level> underworld = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(MOD_ID, "underworld"));





    @Unique
    public DimensionTransition toSpawn(ServerLevel world, Entity entity) {
        ResourceKey<Level> registryKey = world.dimension();
        ServerLevel serverWorld = world.getServer().getLevel(registryKey);
        if (serverWorld == null) {
            return null;
        } else {
            BlockPos blockPos = serverWorld.getSharedSpawnPos();
            float f = entity.getYRot();
            if (entity instanceof ServerPlayer serverPlayerEntity) {
                return serverPlayerEntity.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING);
            }
            Vec3 vec3d = entity.adjustSpawnLocation(serverWorld, blockPos).getBottomCenter();
            return new DimensionTransition(
                    serverWorld,
                    vec3d,
                    entity.getDeltaMovement(),
                    f,
                    entity.getXRot(),
                    DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
            );
        }
    }



    @Inject(method = "getPortalDestination",at=@At(value = "HEAD"),cancellable = true)
    public void createTeleportTarget(ServerLevel world, Entity entity, BlockPos pos, CallbackInfoReturnable<DimensionTransition> cir) {
        cir.cancel();

        //获取目前的世界类型(访问注册方法)
        ResourceKey<Level> registryKey = world.dimension();
        //传送后的世界类型
        ResourceKey<Level> teleport;

        if (registryKey == null) {
            cir.setReturnValue(null);
        } else {
            ServerLevel serverWorld;
            //缩放条件
            boolean inNether = world.dimension() == Level.NETHER;
            //世界边界限制
            WorldBorder worldBorder = world.getWorldBorder();
            //缩放倍率
            double d = DimensionType.getTeleportationScale(world.dimensionType(), world.dimensionType());
            BlockPos blockPos = worldBorder.clampToBounds(entity.getX() * d, entity.getY(), entity.getZ() * d);

            boolean atBottom = Math.abs(entity.getY()-world.getMinBuildHeight())<5;

            //3格的缓冲区,比如防止玩家在地下世界本应该传送到下界的时候跳跃造成y变化导致传送到了主世界
            boolean buffer = Math.abs(entity.getY()-world.getMinBuildHeight())>=5 && Math.abs(entity.getY()-world.getMinBuildHeight())<8;


            //world.getRegistryKey()获取现在的世界
            //主世界传地下世界,地下世界也可以传主世界






            if(world.dimension()==overworld && atBottom){
                teleport=underworld;
            } else if (world.dimension()==overworld && !atBottom && !buffer) {
                //不在底部,且不在缓冲区上
                teleport=overworld;
                if(entity instanceof ServerPlayer player){
                    serverWorld=world.getServer().getLevel(teleport);

                    MobEffectInstance statusEffectInstance1 = new MobEffectInstance(MobEffects.BLINDNESS, 60,255, false,false,false);
                    MobEffectUtil.addEffectToPlayersAround(world, entity, entity.position(), 4, statusEffectInstance1,100);

                    MobEffectInstance statusEffectInstance4 = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,40,255, false,false,false);
                    MobEffectUtil.addEffectToPlayersAround(world, entity, entity.position(), 4, statusEffectInstance4,100);

                    MobEffectInstance statusEffectInstance3 = new MobEffectInstance(MobEffects.CONFUSION,100,255, false,false,false);
                    MobEffectUtil.addEffectToPlayersAround(world, entity, entity.position(), 4, statusEffectInstance3,100);


                    player.changeDimension(toSpawn(serverWorld,player));

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
            } else if (world.dimension()==underworld && !atBottom &&!buffer) {
                //不在底部,也不在缓冲区上,传送到主世界
                teleport=overworld;



            } else if (world.dimension()==underworld &&  atBottom) {
                teleport=nether;
            } else if(world.dimension()==nether){
                teleport=underworld;

            }
            else {
                teleport=world.dimension();
            }


            serverWorld=world.getServer().getLevel(teleport);


            cir.setReturnValue(this.getExitPortal(serverWorld, entity, pos, blockPos, inNether, worldBorder));

        }}











    }







