package com.equilibrium.mixin.structure_and_dimension.strong_hold;

import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StrongholdGenerator.class)

public abstract class StrongholdGeneratorMixin {
    @Inject(method = "createPiece", at = @At(value = "HEAD"),cancellable = true)
    private static void createPiece(Class<? extends StrongholdGenerator.Piece> pieceType, StructurePiecesHolder holder, Random random, int x, int y, int z, @Nullable Direction orientation, int chainLength, CallbackInfoReturnable<StrongholdGenerator.Piece> cir) {
        cir.cancel();
        StrongholdGenerator.Piece piece = null;

        if (pieceType == StrongholdGenerator.Corridor.class) {
            piece = StrongholdGenerator.Corridor.create(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdGenerator.PrisonHall.class) {
            piece = StrongholdGenerator.PrisonHall.create(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdGenerator.LeftTurn.class) {
            piece = StrongholdGenerator.LeftTurn.create(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdGenerator.RightTurn.class) {
            piece = StrongholdGenerator.RightTurn.create(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdGenerator.SquareRoom.class) {
            piece = StrongholdGenerator.SquareRoom.create(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdGenerator.Stairs.class) {
            piece = StrongholdGenerator.Stairs.create(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdGenerator.SpiralStaircase.class) {
            piece = StrongholdGenerator.SpiralStaircase.create(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdGenerator.FiveWayCrossing.class) {
            piece = StrongholdGenerator.FiveWayCrossing.create(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdGenerator.ChestCorridor.class) {
            piece = StrongholdGenerator.ChestCorridor.create(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdGenerator.Library.class) {
            piece = StrongholdGenerator.Library.create(holder, random, x, y, z, orientation, chainLength);
        } else if (pieceType == StrongholdGenerator.PortalRoom.class) {
            if(Math.abs(x)<12000 && Math.abs(z)<12000) {
                x = x+12000;
                z = z+12000;
                piece = StrongholdGenerator.PortalRoom.create(holder, x, y, z, orientation, chainLength);
            }else{
                piece = StrongholdGenerator.PortalRoom.create(holder, x, y, z, orientation, chainLength);
            }

        }
        cir.setReturnValue(piece);
    }}





