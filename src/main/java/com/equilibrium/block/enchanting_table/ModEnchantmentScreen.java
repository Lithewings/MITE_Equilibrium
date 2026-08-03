package com.equilibrium.block.enchanting_table;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class ModEnchantmentScreen extends AbstractContainerScreen<ModEnchantmentScreenHandler> {

    private static final ResourceLocation[] LEVEL_TEXTURES = new ResourceLocation[]{
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1"),
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2"),
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3")
    };
    private static final ResourceLocation[] LEVEL_DISABLED_TEXTURES = new ResourceLocation[]{
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1_disabled"),
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2_disabled"),
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3_disabled")
    };
    private static final ResourceLocation ENCHANTMENT_SLOT_DISABLED_TEXTURE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot_disabled");
    private static final ResourceLocation ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot_highlighted");
    private static final ResourceLocation ENCHANTMENT_SLOT_TEXTURE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot");
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/enchanting_table_book.png");
    private final RandomSource random = RandomSource.create();
    private BookModel BOOK_MODEL;
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack stack = ItemStack.EMPTY;
    private final int[] revealedCount = new int[]{0, 0, 0};

    public ModEnchantmentScreen(ModEnchantmentScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.BOOK_MODEL = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.doTick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        for (int k = 0; k < 3; k++) {
            double d = mouseX - (double)(i + 60);
            double e = mouseY - (double)(j + 14 + 19 * k);
            if (d >= 0.0 && e >= 0.0 && d < 108.0 && e < 19.0 && this.menu.clickMenuButton(this.minecraft.player, k)) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, k);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
        this.drawBook(context, i, j, delta);
        EnchantmentNames.getInstance().initSeed((long)this.menu.getSeed());
        int k = this.menu.getLapisCount();

        for (int l = 0; l < 3; l++) {
            int m = i + 60;
            int n = m + 20;
            int o = this.menu.enchantmentPower[l];
            if (o == 0) {
                RenderSystem.enableBlend();
                context.blitSprite(ENCHANTMENT_SLOT_DISABLED_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                RenderSystem.disableBlend();
            } else {
                String string = o + "";
                int p = 86 - this.font.width(string);
                FormattedText stringVisitable = EnchantmentNames.getInstance().getRandomName(this.font, p);
                int q = 6839882;
                if ((k < l + 1 || this.minecraft.player.totalExperience < o) && !this.minecraft.player.getAbilities().instabuild) {
                    RenderSystem.enableBlend();
                    context.blitSprite(ENCHANTMENT_SLOT_DISABLED_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                    context.blitSprite(LEVEL_DISABLED_TEXTURES[l], m + 1, j + 15 + 19 * l, 16, 16);
                    RenderSystem.disableBlend();
                    context.drawWordWrap(this.font, stringVisitable, n, j + 16 + 19 * l, p, (q & 16711422) >> 1);
                    q = 4226832;
                } else {
                    int r = mouseX - (i + 60);
                    int s = mouseY - (j + 14 + 19 * l);
                    RenderSystem.enableBlend();
                    if (r >= 0 && s >= 0 && r < 108 && s < 19) {
                        context.blitSprite(ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                        q = 16777088;
                    } else {
                        context.blitSprite(ENCHANTMENT_SLOT_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                    }
                    context.blitSprite(LEVEL_TEXTURES[l], m + 1, j + 15 + 19 * l, 16, 16);
                    RenderSystem.disableBlend();
                    context.drawWordWrap(this.font, stringVisitable, n, j + 16 + 19 * l, p, q);
                    q = 8453920;
                }
                context.drawString(this.font, string, n + 86 - this.font.width(string), j + 16 + 19 * l + 7, q);
            }
        }
    }

    private void drawBook(GuiGraphics context, int x, int y, float delta) {
        float f = Mth.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
        float g = Mth.lerp(delta, this.pageAngle, this.nextPageAngle);
        Lighting.setupForEntityInInventory();
        context.pose().pushPose();
        context.pose().translate((float)x + 33.0F, (float)y + 31.0F, 100.0F);
        float h = 40.0F;
        context.pose().scale(-40.0F, 40.0F, 40.0F);
        context.pose().mulPose(Axis.XP.rotationDegrees(25.0F));
        context.pose().translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
        float i = -(1.0F - f) * 90.0F - 90.0F;
        context.pose().mulPose(Axis.YP.rotationDegrees(i));
        context.pose().mulPose(Axis.XP.rotationDegrees(180.0F));
        float j = Mth.clamp(Mth.frac(g + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
        float k = Mth.clamp(Mth.frac(g + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
        this.BOOK_MODEL.setupAnim(0.0F, j, k, f);
        VertexConsumer vertexConsumer = context.bufferSource().getBuffer(this.BOOK_MODEL.renderType(BOOK_TEXTURE));
        this.BOOK_MODEL.renderToBuffer(context.pose(), vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY);
        context.flush();
        context.pose().popPose();
        Lighting.setupFor3DItems();
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
        boolean bl = this.minecraft.player.getAbilities().instabuild;
        int i = this.menu.getLapisCount();

        for (int j = 0; j < 3; j++) {
            int k = this.menu.enchantmentPower[j];
            Optional<Holder.Reference<Enchantment>> optional = this.minecraft
                    .level
                    .registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolder(this.menu.enchantmentId[j]);
            if (optional.isPresent()) {
                int l = this.menu.enchantmentLevel[j];
                int m = j + 1;
                if (this.isHovering(60, 14 + 19 * j, 108, 17, (double)mouseX, (double)mouseY) && k > 0 && l >= 0) {
                    List<Component> list = Lists.newArrayList();
                    list.add(Component.translatable("container.enchant.clue",
                            Enchantment.getFullname(optional.get(), l)).withStyle(ChatFormatting.WHITE));
                    if (!bl) {
                        list.add(CommonComponents.EMPTY);
                        if (this.minecraft.player.totalExperience < k) {
                            list.add(Component.translatable("container.xp.requirement", this.menu.enchantmentPower[j])
                                    .withStyle(ChatFormatting.RED));
                        } else {
                            MutableComponent mutableText;
                            if (m == 1) {
                                mutableText = Component.translatable("container.enchant.lapis.one");
                            } else {
                                mutableText = Component.translatable("container.enchant.lapis.many", m);
                            }
                            list.add(mutableText.withStyle(i >= m ? ChatFormatting.GRAY : ChatFormatting.RED));
                            MutableComponent mutableText2 = Component.translatable("container.xp.cost", k);
                            list.add(mutableText2.withStyle(ChatFormatting.GRAY));
                        }
                    }
                    context.renderComponentTooltip(this.font, list, mouseX, mouseY);
                    break;
                }
            }
        }
    }

    public void doTick() {
        ItemStack itemStack = this.menu.getSlot(0).getItem();
        if (!ItemStack.matches(itemStack, this.stack)) {
            this.stack = itemStack;
            do {
                this.approximatePageAngle = this.approximatePageAngle + (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.nextPageAngle <= this.approximatePageAngle + 1.0F && this.nextPageAngle >= this.approximatePageAngle - 1.0F);
        }

        this.ticks++;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = false;
        for (int i = 0; i < 3; i++) {
            if (this.menu.enchantmentPower[i] != 0) {
                bl = true;
            }
        }
        if (bl) {
            this.nextPageTurningSpeed += 0.2F;
        } else {
            this.nextPageTurningSpeed -= 0.2F;
        }
        this.nextPageTurningSpeed = Mth.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
        float g = 0.2F;
        f = Mth.clamp(f, -0.2F, 0.2F);
        this.pageRotationSpeed = this.pageRotationSpeed + (f - this.pageRotationSpeed) * 0.9F;
        this.nextPageAngle = this.nextPageAngle + this.pageRotationSpeed;
    }
}