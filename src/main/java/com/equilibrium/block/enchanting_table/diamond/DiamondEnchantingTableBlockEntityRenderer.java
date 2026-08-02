package com.equilibrium.block.enchanting_table.diamond;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DiamondEnchantingTableBlockEntityRenderer implements BlockEntityRenderer<DiamondEnchantingTableBlockEntity> {
    public static final Material BOOK_TEXTURE = new Material(
            TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/enchanting_table_book")
    );
    private final BookModel book;

    public DiamondEnchantingTableBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.book = new BookModel(ctx.bakeLayer(ModelLayers.BOOK));
    }

    public void render(
            DiamondEnchantingTableBlockEntity diamondEnchantingTableBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j
    ) {

        matrixStack.pushPose();
        matrixStack.translate(0.5F, 0.75F, 0.5F);
        float g = (float) diamondEnchantingTableBlockEntity.ticks + f;
        matrixStack.translate(0.0F, 0.1F + Mth.sin(g * 0.1F) * 0.01F, 0.0F);
        float h = diamondEnchantingTableBlockEntity.bookRotation - diamondEnchantingTableBlockEntity.lastBookRotation;

        while (h >= (float) Math.PI) {
            h -= (float) (Math.PI * 2);
        }

        while (h < (float) -Math.PI) {
            h += (float) (Math.PI * 2);
        }

        float k = diamondEnchantingTableBlockEntity.lastBookRotation + h * f;
        matrixStack.mulPose(Axis.YP.rotation(-k));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(80.0F));
        float l = Mth.lerp(f, diamondEnchantingTableBlockEntity.pageAngle, diamondEnchantingTableBlockEntity.nextPageAngle);
        float m = Mth.frac(l + 0.25F) * 1.6F - 0.3F;
        float n = Mth.frac(l + 0.75F) * 1.6F - 0.3F;
        float o = Mth.lerp(f, diamondEnchantingTableBlockEntity.pageTurningSpeed, diamondEnchantingTableBlockEntity.nextPageTurningSpeed);
        this.book.setupAnim(g, Mth.clamp(m, 0.0F, 1.0F), Mth.clamp(n, 0.0F, 1.0F), o);
        VertexConsumer vertexConsumer = BOOK_TEXTURE.buffer(vertexConsumerProvider, RenderType::entitySolid);
        this.book.render(matrixStack, vertexConsumer, i, j, -1);
        matrixStack.popPose();
    }
}
