package com.equilibrium.mixin.structure_and_dimension.strong_hold;

import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import static net.minecraft.structure.StrongholdGenerator.*;


@Mixin(Start.class)
public class StrongholdGeneratorStartMixin extends SpiralStaircase {
    public StrongholdGeneratorStartMixin(StructurePieceType structurePieceType, int chainLength, int x, int z, Direction orientation) {
        super(structurePieceType, chainLength, x, z, orientation);
    }
    @ModifyArg(method = "<init>(Lnet/minecraft/util/math/random/Random;II)V",at= @At(value = "INVOKE", target = "Lnet/minecraft/structure/StrongholdGenerator$SpiralStaircase;<init>(Lnet/minecraft/structure/StructurePieceType;IIILnet/minecraft/util/math/Direction;)V"),index = 2)
    private static int modifyConstructorArgsX(int x) {
        return x;
    }
    @ModifyArg(method = "<init>(Lnet/minecraft/util/math/random/Random;II)V",at= @At(value = "INVOKE", target = "Lnet/minecraft/structure/StrongholdGenerator$SpiralStaircase;<init>(Lnet/minecraft/structure/StructurePieceType;IIILnet/minecraft/util/math/Direction;)V"),index = 3)
    private static int modifyConstructorArgsZ(int z) {
        return z;
    }

    @ModifyArgs(method = "<init>(Lnet/minecraft/util/math/random/Random;II)V",at= @At(value = "INVOKE", target = "Lnet/minecraft/structure/StrongholdGenerator$SpiralStaircase;<init>(Lnet/minecraft/structure/StructurePieceType;IIILnet/minecraft/util/math/Direction;)V"))
    private static void modifyConstructorArgsZ(Args args){
//        System.out.println(Optional.ofNullable(args.get(2)));
//        System.out.println(Optional.ofNullable(args.get(3)));

    }



//    @Inject(method = "<init>(Lnet/minecraft/util/math/random/Random;II)V", at = @At("TAIL"))
//    public void start(Random random, int i, int j, CallbackInfo ci) {
//        System.out.println("x= "+i+"z= "+j);
}









