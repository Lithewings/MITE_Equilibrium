package com.equilibrium.mixin.vanilla_blocksmixin;

import net.minecraft.block.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LeavesBlock.class)
public abstract class LetLeavesBlockNoBoxMixin extends Block implements Waterloggable {

    public LetLeavesBlockNoBoxMixin(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context instanceof EntityShapeContext entityShapeContext){
            if(entityShapeContext.getEntity() instanceof ArrowEntity || entityShapeContext.getEntity() instanceof ItemEntity){
                return VoxelShapes.empty();
            }
        }
        return super.getCollisionShape(state,world,pos,context);
    }

}
