package com.equilibrium.mixin.structure_and_dimension.portal;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.PortalForcer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PortalForcer.class)
public abstract class PortalForcerMixin {


    @Final
    @Shadow private ServerWorld world;


//    @Inject(method = "createPortal",at = @At("HEAD"))
//    public void createPortal(BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<Optional<BlockLocating.Rectangle>> cir) {
//        System.out.println("getPortalPos");
//    }
//    @Inject(method = "isBlockStateValid",at = @At("HEAD"))
//    private void isBlockStateValid(BlockPos.Mutable pos, CallbackInfoReturnable<Boolean> cir) {
//        System.out.println("isBlockStateValid");
//    }
//    @Inject(method = "isValidPortalPos",at = @At("HEAD"))
//    private void isValidPortalPos(BlockPos pos, BlockPos.Mutable temp, Direction portalDirection, int distanceOrthogonalToPortal, CallbackInfoReturnable<Boolean> cir) {
//        System.out.println("isValidPortalPos");
//    }
//    @Inject(method = "getPortalPos",at = @At("HEAD"), cancellable = true)
//    public void getPortalPos(BlockPos pos, boolean destIsNether, WorldBorder worldBorder, CallbackInfoReturnable<Optional<BlockPos>> cir) {
//        cir.cancel();
//        PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
//        int i = destIsNether ? 16 : 128;
//        pointOfInterestStorage.preloadChunks(this.world, pos, i);
//
//        Comparator<BlockPos> distanceComparator = Comparator.comparingDouble(blockPos2 -> blockPos2.getSquaredDistance(pos));
//        Comparator<BlockPos> yComparator = Comparator.comparingInt(BlockPos::getY);
//        Comparator<BlockPos> combinedComparator = distanceComparator.thenComparing(yComparator);
//
//
//        RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.of("miteequilibrium","underworld_portal"));
//        PointOfInterestTypes.registerAndGetDefault((Registry<PointOfInterestType>) UNDERWORLD_PORTAL);
//
//
//
//
//        cir.setReturnValue(pointOfInterestStorage.getInSquare(
//                        poiType -> poiType.matchesKey(PointOfInterestTypes.NETHER_PORTAL), pos, i, PointOfInterestStorage.OccupationStatus.ANY
//                )
//                .map(PointOfInterest::getPos)
//                .filter(worldBorder::contains)
//                .filter(blockPos -> this.world.getBlockState(blockPos).contains(Properties.HORIZONTAL_AXIS))
//                .min(combinedComparator));
//    }



}
