package com.equilibrium.mixin.structure_and_dimension;

import com.equilibrium.server_and_client.server.persistent_state.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(RandomSpreadStructurePlacement.class)
public class RandomSpreadStructurePlacementMixin {
    @Shadow
    @Final
    private int spacing;


    @Shadow
    @Final
    private int separation;

    @Shadow
    @Final
    private SpreadType spreadType;






    @Shadow
    public int getSpacing() {
        return this.spacing;
    }
    @Shadow
    public int getSeparation() {
        return this.separation;
    }
    @Shadow
    public SpreadType getSpreadType() {
        return this.spreadType;
    }


    private static boolean getStructureGenerateValidity(MinecraftServer server){
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        return serverState.isPickAxeCrafted;

    }

    @Inject(method = "getStartChunk",at = @At("HEAD"),cancellable = true)
    public void getStartChunk(long seed, int chunkX, int chunkZ, CallbackInfoReturnable<ChunkPos> cir) {
        cir.cancel();

        int spacing = this.spacing;
        int separation = this.separation;
//        if((-1000<chunkX&&chunkX<1000 && -1000<chunkZ&&chunkZ<1000)) {
//            spacing = spacing+160;
//            if(server!=null)
//                for(ServerWorld world :server.getWorlds()) {
//                    if (!world.getPlayers().isEmpty()) {
//                        world.getPlayers().getFirst().sendMessage(Text.of("spacing is " + spacing));
//                        world.getPlayers().getFirst().sendMessage(Text.of("separation is " + separation));
//                    }
//            }
//        }
        int i = Math.floorDiv(chunkX, spacing);
        int j = Math.floorDiv(chunkZ, spacing);
        ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
        Random random = new Random();
        double randomNumber = random.nextDouble();
        chunkRandom.setRegionSeed(seed, i, j, (int) (seed*randomNumber));
        int k = spacing - separation;
        int l = this.spreadType.get(chunkRandom, k);
        int m = this.spreadType.get(chunkRandom, k);

        int x = i * spacing+ l;
        int z = j * spacing + m;


        cir.setReturnValue(new ChunkPos(x,z));
    }











}
