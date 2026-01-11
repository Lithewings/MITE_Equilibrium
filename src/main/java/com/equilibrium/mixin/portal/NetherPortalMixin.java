package com.equilibrium.mixin.portal;

import net.minecraft.util.math.*;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.*;

@Mixin(NetherPortal.class)







public abstract class NetherPortalMixin {

    @Shadow
    private BlockPos lowerCorner;
    @Shadow
    private int height;
    @Shadow
    @Final
    private int width;
    @Shadow
    @Final
    private  WorldAccess world;
    @Shadow
    @Final
    private Direction.Axis axis;
    @Shadow
    @Final
    private  Direction negativeDir;













//
///* 测试用
//    @Inject(method = "getNewPortal",at = @At("HEAD"))
//    private static void getNewPortal(WorldAccess world, BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<Optional<NetherPortal>> cir) {
//        System.out.println("getNewPortal()");
//    }
//    @Inject(method = "getOrEmpty",at =@At("HEAD"))
//    private static void getOrEmpty(WorldAccess world, BlockPos pos, Predicate<NetherPortal> validator, Direction.Axis axis, CallbackInfoReturnable<Optional<NetherPortal>> cir) {
//        System.out.println("getOrEmpty()");
//    }
//    @Inject(method = "getLowerCorner",at = @At("HEAD"))
//    private void getLowerCorner(BlockPos pos, CallbackInfoReturnable<BlockPos> cir) {
//        System.out.println("getLowerCorner");
//    }
//    @Inject(method = "getWidth()I",at = @At("HEAD"))
//    private void getWidth(CallbackInfoReturnable<Integer> cir) {
//        System.out.println("getWidth()I");
//    }
//    @Inject(method = "getWidth(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)I",at = @At("HEAD"))
//    private void getWidth(BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
//        System.out.println("getWidth(2)");
//    }
//    @Inject(method = "getHeight",at = @At("HEAD"))
//    private void getHeight(CallbackInfoReturnable<Integer> cir) {
//        System.out.println("getHeight");
//    }
//    @Inject(method = "isHorizontalFrameValid",at = @At("HEAD"))
//    private void isHorizontalFrameValid(BlockPos.Mutable pos, int height, CallbackInfoReturnable<Boolean> cir) {
//        System.out.println("isHorizontalFrameValid");
//    }
//    @Inject(method = "getPotentialHeight",at =@At("HEAD"))
//    private void getPotentialHeight(BlockPos.Mutable pos, CallbackInfoReturnable<Integer> cir) {
//        System.out.println("getPotentialHeight");
//    }
//    @Inject(method = "validStateInsidePortal",at = @At("HEAD"))
//    private static void validStateInsidePortal(BlockState state, CallbackInfoReturnable<Boolean> cir) {
//        System.out.println("validStateInsidePortal");
//    }
//    @Inject(method = "isValid",at = @At("HEAD"))
//    public void isValid(CallbackInfoReturnable<Boolean> cir) {
//        System.out.println("isValid");
//    }
//    @Inject(method = "createPortal",at =@At("HEAD"))
//    public void createPortal(CallbackInfo ci) {
//        System.out.println("createPortal()");
//    }
//    @Inject(method = "wasAlreadyValid",at = @At("HEAD"))
//    public void wasAlreadyValid(CallbackInfoReturnable<Boolean> cir) {
//        System.out.println("wasAlreadyValid");
//    }
//    @Inject(method = "entityPosInPortal",at = @At("HEAD"))
//    private static void entityPosInPortal(BlockLocating.Rectangle portalRect, Direction.Axis portalAxis, Vec3d entityPos, EntityDimensions entityDimensions, CallbackInfoReturnable<Vec3d> cir) {
//        System.out.println("entityPosInPortal");
//    }
//    @Inject(method = "findOpenPosition",at =@At("HEAD"))
//    private static void findOpenPosition(Vec3d fallback, ServerWorld world, Entity entity, EntityDimensions dimensions, CallbackInfoReturnable<Vec3d> cir) {
//        System.out.println("findOpenPosition");
//    }
//    */
//
//    @Unique
//    private int getHeightFromBottom(){
//        if (this.lowerCorner != null) {
//            return Math.abs(world.getBottomY()-this.lowerCorner.getY());
//        }
//        else{
//            System.out.println("It might the portal doesn't exist");
//            return 384;
//        }
//    };
//
//    @Unique
//    public RegistryKey<World> getWorldDimension(ServerWorld world) {
//        return world.getRegistryKey();
//        //RegistryKey<World> registryKey = world.getRegistryKey() == World.NETHER ? World.OVERWORLD : World.NETHER;
//    }
//
//
//    @Shadow private @Nullable BlockPos lowerCorner;
//
//
//    @Shadow @Final
//    private WorldAccess world;
//
//
//
//    @Inject(method = "isHorizontalFrameValid",at=@At(value = "HEAD"))
//    //当传送门距离世界底部距离小于等于5时,传送门才可以被点亮
//    private void isHorizontalFrameValid(BlockPos.Mutable pos, int height, CallbackInfoReturnable<Boolean> cir) {
//
////        boolean getOldPortalStateIsTrue =cir.getReturnValue();
////        int theHeightOfThePortalFromTheWorldBottom;
////
////
////        if(getOldPortalStateIsTrue && this.lowerCorner != null){
////
////          theHeightOfThePortalFromTheWorldBottom=getHeightFromBottom();
////
////          if(theHeightOfThePortalFromTheWorldBottom<=5){
////              System.out.println("The height of the portal from world_bottom:"+(theHeightOfThePortalFromTheWorldBottom));
////              cir.setReturnValue(true);
////          }else{
////              System.out.println("The height of the portal from world_bottom:"+(theHeightOfThePortalFromTheWorldBottom)+">5");
////              cir.setReturnValue(false);
////          }
////        }else{
////            System.out.println("condition is false: getOldPortalStateIsTrue && this.lowerCorner != null ");
////            System.out.println("It might be this.lowerCorner is null(is the world now where the portal locate in existing?)");
////            cir.setReturnValue(false);
////        }
//    }
//
//
//    //不要用accessible访问类的私有变量,直接用@Shadow好不好
//    @Shadow @Final
//    private Direction.Axis axis;
//
//    @Shadow @Final
//    private Direction negativeDir;
//
//    @Shadow @Final
//    private int width;
//
//    @Shadow
//    private int height;
//
//    @Shadow
//    private final static AbstractBlock.ContextPredicate IS_VALID_FRAME_BLOCK = (state, world, pos) -> state.isOf(Blocks.OBSIDIAN);
//
//    @Shadow
//    private int foundPortalBlocks;
//
//
//
//
//
//
//
//
//
//
//
//
//
//    @Inject(method = "getPotentialHeight",at =@At("HEAD"),cancellable = true)
//    private void getPotentialHeight(BlockPos.Mutable pos, CallbackInfoReturnable<Integer> cir) {
//        for (int i = 0; i < 21; i++) {
//            pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, -1);
//            if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(pos), this.world, pos)) {
//                cir.setReturnValue(i);
//            }
//
//            pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, this.width);
//            if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(pos), this.world, pos)) {
//                cir.setReturnValue(i);
//            }
//
//            for (int j = 0; j < this.width; j++) {
//                pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, j);
//                BlockState blockState = this.world.getBlockState(pos);
//                if (!NetherPortal.validStateInsidePortal(blockState)) {
//                    cir.setReturnValue(i);
//                }
//
//                if (blockState.isOf(ModBlocks.UNDERWORLD_PORTAL)) {
//                    this.foundPortalBlocks++;
//                }
//            }
//        }
//
//        cir.setReturnValue(21);
//    }
//
////    @Unique
////    private static boolean validStateInsidePortalForMixin(BlockState state) {
////        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(ModBlocks.UNDERWORLD_PORTAL);
////    }
//
//
//    @Inject(method = "validStateInsidePortal",at=@At(value = "HEAD"), cancellable = true)
//    private static void validStateInsidePortal(BlockState state, CallbackInfoReturnable<Boolean> cir) {
//        cir.cancel();
//        cir.setReturnValue(state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(ModBlocks.UNDERWORLD_PORTAL));
//    }
//
//
//    @Inject(method = "createPortal",at=@At(value = "HEAD"), cancellable = true)
//    public void createPortal(CallbackInfo ci) {
//    /*
//    规则:当传送门距离世界底部距离小于等于5时,传送门才可以被点亮为自定义的传送门方块
//    1、在主世界,建立在距离底部小于等于5格距离的传送门,将传送到地下世界
//    2、在地下世界,建立在距离底部小于等于5格距离的传送门,将传送到下界
//    3、在地下世界,建立在距离底部大于5格距离的传送门,将传送到主世界
//    4、在下界,任意位置的传送门,都将传送到地下世界
//     */
//        ci.cancel();
//    //记录当前注册的世界
//        RegistryKey<World> registryKey = ((ServerWorld) world).getRegistryKey();
//
//        //是否距离底部小于5格距离
//        boolean locatedInBottom = getHeightFromBottom() <= 5;
//
//        //是否是主世界
//        boolean overworld = (getWorldDimension((ServerWorld) world) == World.OVERWORLD);
//        //是否是地下世界
//        boolean underworld = (getWorldDimension((ServerWorld) world) == RegistryKey.of(RegistryKeys.WORLD, Identifier.of("miteequilibrium", "underworld")));
//
//        //是否为下界
//        boolean netherworld = (getWorldDimension((ServerWorld) world) == World.NETHER);
//
//
//
//
//        if (locatedInBottom && overworld) {
//            //在主世界,建立在距离底部小于等于5格距离的传送门,将传送到地下世界
//            setUnderworldPortalBlock();
//        }else if ((!locatedInBottom) && underworld) {
//            //在地下世界,建立在距离底部大于5格距离的传送门,将传送到主世界
//            setUnderworldPortalBlock();
//        }
//        else{
//            ci.cancel();
//
//        }
//
//    }
//
//    @Unique
//    private void setUnderworldPortalBlock() {
//        BlockState blockState = Blocks.NETHER_PORTAL.getDefaultState().with(UnderworldPortalBlock.AXIS,this.axis);
//        BlockPos.iterate(this.lowerCorner, this.lowerCorner.offset(Direction.UP, this.height - 1).offset(this.negativeDir, this.width - 1))
//                .forEach(pos -> this.world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE));
//    }


}
