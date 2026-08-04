package com.equilibrium.server_and_client.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Function;

/**
 * Represents the model of a biped living entity.
 *
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value PartNames#HAT}</td><td>Root part</td><td>{@link #hat}</td>
 * </tr>
 * <tr>
 *   <td>{@value PartNames#HEAD}</td><td>Root part</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value PartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value PartNames#RIGHT_ARM}</td><td>Root part</td><td>{@link #rightArm}</td>
 * </tr>
 * <tr>
 *   <td>{@value PartNames#LEFT_ARM}</td><td>Root part</td><td>{@link #leftArm}</td>
 * </tr>
 * <tr>
 *   <td>{@value PartNames#RIGHT_LEG}</td><td>Root part</td><td>{@link #rightLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value PartNames#LEFT_LEG}</td><td>Root part</td><td>{@link #leftLeg}</td>
 * </tr>
 * </table>
 * </div>
 *
 */
@Environment(EnvType.CLIENT)
public class TransparentBipedEntityModel<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {
    public static final float field_32505 = 0.25F;
    public static final float field_32506 = 0.5F;
    public static final float field_42513 = -0.1F;
    private static final float field_42512 = 0.005F;
    private static final float SPYGLASS_ARM_YAW_OFFSET = (float) (Math.PI / 12);
    private static final float SPYGLASS_ARM_PITCH_OFFSET = 1.9198622F;
    private static final float SPYGLASS_SNEAKING_ARM_PITCH_OFFSET = (float) (Math.PI / 12);
    private static final float field_46576 = (float) (-Math.PI * 4.0 / 9.0);
    private static final float field_46577 = 0.43633232F;
    private static final float field_46724 = (float) (Math.PI / 6);
    public static final float field_39069 = 1.4835298F;
    public static final float field_39070 = (float) (Math.PI / 6);
    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;
    public net.minecraft.client.model.HumanoidModel.ArmPose leftArmPose = net.minecraft.client.model.HumanoidModel.ArmPose.EMPTY;
    public net.minecraft.client.model.HumanoidModel.ArmPose rightArmPose = net.minecraft.client.model.HumanoidModel.ArmPose.EMPTY;
    public boolean sneaking;
    public float leaningPitch;

    public TransparentBipedEntityModel(ModelPart root) {
        this(root, RenderType::entityNoOutline);
    }

    public TransparentBipedEntityModel(ModelPart root, Function<ResourceLocation, RenderType> renderLayerFactory) {
        super(renderLayerFactory, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
        this.head = root.getChild(PartNames.HEAD);
        this.hat = root.getChild(PartNames.HAT);
        this.body = root.getChild(PartNames.BODY);
        this.rightArm = root.getChild(PartNames.RIGHT_ARM);
        this.leftArm = root.getChild(PartNames.LEFT_ARM);
        this.rightLeg = root.getChild(PartNames.RIGHT_LEG);
        this.leftLeg = root.getChild(PartNames.LEFT_LEG);
    }

    public static MeshDefinition getModelData(CubeDeformation dilation, float pivotOffsetY) {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(
                PartNames.HEAD,
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation),
                PartPose.offset(0.0F, 0.0F + pivotOffsetY, 0.0F)
        );
        modelPartData.addOrReplaceChild(
                PartNames.HAT,
                CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation.extend(0.5F)),
                PartPose.offset(0.0F, 0.0F + pivotOffsetY, 0.0F)
        );
        modelPartData.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation),
                PartPose.offset(0.0F, 0.0F + pivotOffsetY, 0.0F)
        );
        modelPartData.addOrReplaceChild(
                PartNames.RIGHT_ARM,
                CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
                PartPose.offset(-5.0F, 2.0F + pivotOffsetY, 0.0F)
        );
        modelPartData.addOrReplaceChild(
                PartNames.LEFT_ARM,
                CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
                PartPose.offset(5.0F, 2.0F + pivotOffsetY, 0.0F)
        );
        modelPartData.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
                PartPose.offset(-1.9F, 12.0F + pivotOffsetY, 0.0F)
        );
        modelPartData.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
                PartPose.offset(1.9F, 12.0F + pivotOffsetY, 0.0F)
        );
        return modelData;
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.<ModelPart>of(this.head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.<ModelPart>of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
    }

    public void animateModel(T livingEntity, float f, float g, float h) {
        this.leaningPitch = livingEntity.getSwimAmount(h);
        super.prepareMobModel(livingEntity, f, g, h);
    }

    public void setupAnim(T livingEntity, float f, float g, float h, float i, float j) {
        boolean bl = livingEntity.getFallFlyingTicks() > 4;
        boolean bl2 = livingEntity.isVisuallySwimming();
        this.head.yRot = i * (float) (Math.PI / 180.0);
        if (bl) {
            this.head.xRot = (float) (-Math.PI / 4);
        } else if (this.leaningPitch > 0.0F) {
            if (bl2) {
                this.head.xRot = this.lerpAngle(this.leaningPitch, this.head.xRot, (float) (-Math.PI / 4));
            } else {
                this.head.xRot = this.lerpAngle(this.leaningPitch, this.head.xRot, j * (float) (Math.PI / 180.0));
            }
        } else {
            this.head.xRot = j * (float) (Math.PI / 180.0);
        }

        this.body.yRot = 0.0F;
        this.rightArm.z = 0.0F;
        this.rightArm.x = -5.0F;
        this.leftArm.z = 0.0F;
        this.leftArm.x = 5.0F;
        float k = 1.0F;
        if (bl) {
            k = (float)livingEntity.getDeltaMovement().lengthSqr();
            k /= 0.2F;
            k *= k * k;
        }

        if (k < 1.0F) {
            k = 1.0F;
        }

        this.rightArm.xRot = Mth.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / k;
        this.leftArm.xRot = Mth.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        this.rightLeg.xRot = Mth.cos(f * 0.6662F) * 1.4F * g / k;
        this.leftLeg.xRot = Mth.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / k;
        this.rightLeg.yRot = 0.005F;
        this.leftLeg.yRot = -0.005F;
        this.rightLeg.zRot = 0.005F;
        this.leftLeg.zRot = -0.005F;
        if (this.riding) {
            this.rightArm.xRot += (float) (-Math.PI / 5);
            this.leftArm.xRot += (float) (-Math.PI / 5);
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = (float) (Math.PI / 10);
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = (float) (-Math.PI / 10);
            this.leftLeg.zRot = -0.07853982F;
        }

        this.rightArm.yRot = 0.0F;
        this.leftArm.yRot = 0.0F;
        boolean bl3 = livingEntity.getMainArm() == HumanoidArm.RIGHT;
        if (livingEntity.isUsingItem()) {
            boolean bl4 = livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND;
            if (bl4 == bl3) {
                this.positionRightArm(livingEntity);
            } else {
                this.positionLeftArm(livingEntity);
            }
        } else {
            boolean bl4 = bl3 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
            if (bl3 != bl4) {
                this.positionLeftArm(livingEntity);
                this.positionRightArm(livingEntity);
            } else {
                this.positionRightArm(livingEntity);
                this.positionLeftArm(livingEntity);
            }
        }

        this.animateArms(livingEntity, h);
        if (this.sneaking) {
            this.body.xRot = 0.5F;
            this.rightArm.xRot += 0.4F;
            this.leftArm.xRot += 0.4F;
            this.rightLeg.z = 4.0F;
            this.leftLeg.z = 4.0F;
            this.rightLeg.y = 12.2F;
            this.leftLeg.y = 12.2F;
            this.head.y = 4.2F;
            this.body.y = 3.2F;
            this.leftArm.y = 5.2F;
            this.rightArm.y = 5.2F;
        } else {
            this.body.xRot = 0.0F;
            this.rightLeg.z = 0.0F;
            this.leftLeg.z = 0.0F;
            this.rightLeg.y = 12.0F;
            this.leftLeg.y = 12.0F;
            this.head.y = 0.0F;
            this.body.y = 0.0F;
            this.leftArm.y = 2.0F;
            this.rightArm.y = 2.0F;
        }

        if (this.rightArmPose != net.minecraft.client.model.HumanoidModel.ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.rightArm, h, 1.0F);
        }

        if (this.leftArmPose != net.minecraft.client.model.HumanoidModel.ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.leftArm, h, -1.0F);
        }

        if (this.leaningPitch > 0.0F) {
            float l = f % 26.0F;
            HumanoidArm arm = this.getPreferredArm(livingEntity);
            float m = arm == HumanoidArm.RIGHT && this.attackTime > 0.0F ? 0.0F : this.leaningPitch;
            float n = arm == HumanoidArm.LEFT && this.attackTime > 0.0F ? 0.0F : this.leaningPitch;
            if (!livingEntity.isUsingItem()) {
                if (l < 14.0F) {
                    this.leftArm.xRot = this.lerpAngle(n, this.leftArm.xRot, 0.0F);
                    this.rightArm.xRot = Mth.lerp(m, this.rightArm.xRot, 0.0F);
                    this.leftArm.yRot = this.lerpAngle(n, this.leftArm.yRot, (float) Math.PI);
                    this.rightArm.yRot = Mth.lerp(m, this.rightArm.yRot, (float) Math.PI);
                    this.leftArm.zRot = this.lerpAngle(n, this.leftArm.zRot, (float) Math.PI + 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
                    this.rightArm.zRot = Mth.lerp(m, this.rightArm.zRot, (float) Math.PI - 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
                } else if (l >= 14.0F && l < 22.0F) {
                    float o = (l - 14.0F) / 8.0F;
                    this.leftArm.xRot = this.lerpAngle(n, this.leftArm.xRot, (float) (Math.PI / 2) * o);
                    this.rightArm.xRot = Mth.lerp(m, this.rightArm.xRot, (float) (Math.PI / 2) * o);
                    this.leftArm.yRot = this.lerpAngle(n, this.leftArm.yRot, (float) Math.PI);
                    this.rightArm.yRot = Mth.lerp(m, this.rightArm.yRot, (float) Math.PI);
                    this.leftArm.zRot = this.lerpAngle(n, this.leftArm.zRot, 5.012389F - 1.8707964F * o);
                    this.rightArm.zRot = Mth.lerp(m, this.rightArm.zRot, 1.2707963F + 1.8707964F * o);
                } else if (l >= 22.0F && l < 26.0F) {
                    float o = (l - 22.0F) / 4.0F;
                    this.leftArm.xRot = this.lerpAngle(n, this.leftArm.xRot, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
                    this.rightArm.xRot = Mth.lerp(m, this.rightArm.xRot, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
                    this.leftArm.yRot = this.lerpAngle(n, this.leftArm.yRot, (float) Math.PI);
                    this.rightArm.yRot = Mth.lerp(m, this.rightArm.yRot, (float) Math.PI);
                    this.leftArm.zRot = this.lerpAngle(n, this.leftArm.zRot, (float) Math.PI);
                    this.rightArm.zRot = Mth.lerp(m, this.rightArm.zRot, (float) Math.PI);
                }
            }

            float o = 0.3F;
            float p = 0.33333334F;
            this.leftLeg.xRot = Mth.lerp(this.leaningPitch, this.leftLeg.xRot, 0.3F * Mth.cos(f * 0.33333334F + (float) Math.PI));
            this.rightLeg.xRot = Mth.lerp(this.leaningPitch, this.rightLeg.xRot, 0.3F * Mth.cos(f * 0.33333334F));
        }

        this.hat.copyFrom(this.head);
    }

    private void positionRightArm(T entity) {
        switch (this.rightArmPose) {
            case EMPTY:
                this.rightArm.yRot = 0.0F;
                break;
            case ITEM:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) (Math.PI / 10);
                this.rightArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.positionBlockingArm(this.rightArm, true);
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot;
                this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
                this.rightArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
                this.leftArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
                break;
            case THROW_SPEAR:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) Math.PI;
                this.rightArm.yRot = 0.0F;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                break;
            case SPYGLASS:
                this.rightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (entity.isCrouching() ? (float) (Math.PI / 12) : 0.0F), -2.4F, 3.3F);
                this.rightArm.yRot = this.head.yRot - (float) (Math.PI / 12);
                break;
            case TOOT_HORN:
                this.rightArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
                this.rightArm.yRot = this.head.yRot - (float) (Math.PI / 6);
                break;
            case BRUSH:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) (Math.PI / 5);
                this.rightArm.yRot = 0.0F;
        }
    }

    private void positionLeftArm(T entity) {
        switch (this.leftArmPose) {
            case EMPTY:
                this.leftArm.yRot = 0.0F;
                break;
            case ITEM:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) (Math.PI / 10);
                this.leftArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.positionBlockingArm(this.leftArm, false);
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
                this.leftArm.yRot = 0.1F + this.head.yRot;
                this.rightArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
                this.leftArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
                break;
            case THROW_SPEAR:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) Math.PI;
                this.leftArm.yRot = 0.0F;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, false);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
                break;
            case SPYGLASS:
                this.leftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (entity.isCrouching() ? (float) (Math.PI / 12) : 0.0F), -2.4F, 3.3F);
                this.leftArm.yRot = this.head.yRot + (float) (Math.PI / 12);
                break;
            case TOOT_HORN:
                this.leftArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
                this.leftArm.yRot = this.head.yRot + (float) (Math.PI / 6);
                break;
            case BRUSH:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) (Math.PI / 5);
                this.leftArm.yRot = 0.0F;
        }
    }

    private void positionBlockingArm(ModelPart arm, boolean rightArm) {
        arm.xRot = arm.xRot * 0.5F - 0.9424779F + Mth.clamp(this.head.xRot, (float) (-Math.PI * 4.0 / 9.0), 0.43633232F);
        arm.yRot = (rightArm ? -30.0F : 30.0F) * (float) (Math.PI / 180.0) + Mth.clamp(this.head.yRot, (float) (-Math.PI / 6), (float) (Math.PI / 6));
    }

    protected void animateArms(T entity, float animationProgress) {
        if (!(this.attackTime <= 0.0F)) {
            HumanoidArm arm = this.getPreferredArm(entity);
            ModelPart modelPart = this.getArm(arm);
            float f = this.attackTime;
            this.body.yRot = Mth.sin(Mth.sqrt(f) * (float) (Math.PI * 2)) * 0.2F;
            if (arm == HumanoidArm.LEFT) {
                this.body.yRot *= -1.0F;
            }

            this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F;
            this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
            this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F;
            this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
            this.rightArm.yRot = this.rightArm.yRot + this.body.yRot;
            this.leftArm.yRot = this.leftArm.yRot + this.body.yRot;
            this.leftArm.xRot = this.leftArm.xRot + this.body.yRot;
            f = 1.0F - this.attackTime;
            f *= f;
            f *= f;
            f = 1.0F - f;
            float g = Mth.sin(f * (float) Math.PI);
            float h = Mth.sin(this.attackTime * (float) Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
            modelPart.xRot -= g * 1.2F + h;
            modelPart.yRot = modelPart.yRot + this.body.yRot * 2.0F;
            modelPart.zRot = modelPart.zRot + Mth.sin(this.attackTime * (float) Math.PI) * -0.4F;
        }
    }

    protected float lerpAngle(float angleOne, float angleTwo, float magnitude) {
        float f = (magnitude - angleTwo) % (float) (Math.PI * 2);
        if (f < (float) -Math.PI) {
            f += (float) (Math.PI * 2);
        }

        if (f >= (float) Math.PI) {
            f -= (float) (Math.PI * 2);
        }

        return angleTwo + angleOne * f;
    }

    private float method_2807(float f) {
        return -65.0F * f + f * f;
    }

    public void copyBipedStateTo(net.minecraft.client.model.HumanoidModel<T> model) {
        super.copyPropertiesTo(model);
        model.leftArmPose = this.leftArmPose;
        model.rightArmPose = this.rightArmPose;
        model.crouching = this.sneaking;
        model.head.copyFrom(this.head);
        model.hat.copyFrom(this.hat);
        model.body.copyFrom(this.body);
        model.rightArm.copyFrom(this.rightArm);
        model.leftArm.copyFrom(this.leftArm);
        model.rightLeg.copyFrom(this.rightLeg);
        model.leftLeg.copyFrom(this.leftLeg);
    }

    public void setVisible(boolean visible) {
        this.head.visible = visible;
        this.hat.visible = visible;
        this.body.visible = visible;
        this.rightArm.visible = visible;
        this.leftArm.visible = visible;
        this.rightLeg.visible = visible;
        this.leftLeg.visible = visible;
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack matrices) {
        this.getArm(arm).translateAndRotate(matrices);
    }

    protected ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    private HumanoidArm getPreferredArm(T entity) {
        HumanoidArm arm = entity.getMainArm();
        return entity.swingingArm == InteractionHand.MAIN_HAND ? arm : arm.getOpposite();
    }

    @Environment(EnvType.CLIENT)
    public static enum ArmPose {
        EMPTY(false),
        ITEM(false),
        BLOCK(false),
        BOW_AND_ARROW(true),
        THROW_SPEAR(false),
        CROSSBOW_CHARGE(true),
        CROSSBOW_HOLD(true),
        SPYGLASS(false),
        TOOT_HORN(false),
        BRUSH(false);

        private final boolean twoHanded;

        private ArmPose(final boolean twoHanded) {
            this.twoHanded = twoHanded;
        }

        public boolean isTwoHanded() {
            return this.twoHanded;
        }
    }
}
