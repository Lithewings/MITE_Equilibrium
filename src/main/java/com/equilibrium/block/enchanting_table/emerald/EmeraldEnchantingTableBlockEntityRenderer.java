package com.equilibrium.block.enchanting_table.emerald;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class EmeraldEnchantingTableBlockEntityRenderer implements BlockEntityRenderer<EmeraldEnchantingTableBlockEntity> {
    public static final SpriteIdentifier BOOK_TEXTURE = new SpriteIdentifier(
            SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("entity/enchanting_table_book")
    );
    private final BookModel book;

    public EmeraldEnchantingTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    public void render(
            EmeraldEnchantingTableBlockEntity emeraldEnchantingTableBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j
    ) {

        matrixStack.push();
        matrixStack.translate(0.5F, 0.75F, 0.5F);
        float g = (float) emeraldEnchantingTableBlockEntity.ticks + f;
        matrixStack.translate(0.0F, 0.1F + MathHelper.sin(g * 0.1F) * 0.01F, 0.0F);
        float h = emeraldEnchantingTableBlockEntity.bookRotation - emeraldEnchantingTableBlockEntity.lastBookRotation;

        while (h >= (float) Math.PI) {
            h -= (float) (Math.PI * 2);
        }

        while (h < (float) -Math.PI) {
            h += (float) (Math.PI * 2);
        }

        float k = emeraldEnchantingTableBlockEntity.lastBookRotation + h * f;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(-k));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0F));
        float l = MathHelper.lerp(f, emeraldEnchantingTableBlockEntity.pageAngle, emeraldEnchantingTableBlockEntity.nextPageAngle);
        float m = MathHelper.fractionalPart(l + 0.25F) * 1.6F - 0.3F;
        float n = MathHelper.fractionalPart(l + 0.75F) * 1.6F - 0.3F;
        float o = MathHelper.lerp(f, emeraldEnchantingTableBlockEntity.pageTurningSpeed, emeraldEnchantingTableBlockEntity.nextPageTurningSpeed);
        this.book.setPageAngles(g, MathHelper.clamp(m, 0.0F, 1.0F), MathHelper.clamp(n, 0.0F, 1.0F), o);
        VertexConsumer vertexConsumer = BOOK_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.book.renderBook(matrixStack, vertexConsumer, i, j, -1);
        matrixStack.pop();
    }
}
