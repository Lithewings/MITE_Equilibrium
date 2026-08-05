package com.equilibrium.mixin.structure_and_dimension.strong_hold;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StrongholdPieces.class)

public abstract class StrongholdGeneratorMixin {
    @Inject(method = "findAndCreatePieceFactory", at = @At(value = "HEAD"),cancellable = true)
    private static void createPiece(Class<? extends StrongholdPieces.StrongholdPiece> pieceType, StructurePieceAccessor holder, RandomSource random, int x, int y, int z, @Nullable Direction orientation, int chainLength, CallbackInfoReturnable<StrongholdPieces.StrongholdPiece> cir) {
        cir.cancel();
        StrongholdPieces.StrongholdPiece piece = null;

        if (pieceType == StrongholdPieces.Straight.class) {
            piece = StrongholdPieces.Straight.createPiece(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdPieces.PrisonHall.class) {
            piece = StrongholdPieces.PrisonHall.createPiece(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdPieces.LeftTurn.class) {
            piece = StrongholdPieces.LeftTurn.createPiece(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdPieces.RightTurn.class) {
            piece = StrongholdPieces.RightTurn.createPiece(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdPieces.RoomCrossing.class) {
            piece = StrongholdPieces.RoomCrossing.createPiece(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdPieces.StraightStairsDown.class) {
            piece = StrongholdPieces.StraightStairsDown.createPiece(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdPieces.StairsDown.class) {
            piece = StrongholdPieces.StairsDown.createPiece(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdPieces.FiveCrossing.class) {
            piece = StrongholdPieces.FiveCrossing.createPiece(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdPieces.ChestCorridor.class) {
            piece = StrongholdPieces.ChestCorridor.createPiece(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdPieces.Library.class) {
            piece = StrongholdPieces.Library.createPiece(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdPieces.PortalRoom.class) {
            if(Math.abs(x)<12000 && Math.abs(z)<12000) {
                x = x+12000;
                z = z+12000;
                piece = StrongholdPieces.PortalRoom.createPiece(holder, x, y, z, orientation, chainLength);
            }else{
                piece = StrongholdPieces.PortalRoom.createPiece(holder, x, y, z, orientation, chainLength);
            }

        }
        cir.setReturnValue(piece);
    }}





