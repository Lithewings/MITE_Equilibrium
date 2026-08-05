package com.equilibrium.mixin.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN;

@Mixin(LocalPlayer.class)

//GameMenuScreenMixin中必须让暂停延迟1tick来让这些逻辑生效






public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {
    @Shadow @Final protected Minecraft minecraft;

    @Shadow public abstract boolean isTextFilteringEnabled();

    public ClientPlayerEntityMixin(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }



    /*
    * 规则:
    * 第一,玩家接受缓慢效果时,鼠标灵敏度被强制降低
    * 第二,玩家在缓慢效果内,不受鼠标灵敏度设置影响,但只要切换到了界面,鼠标灵敏度就会恢复正常
    * 第三,玩家不管是否暂停游戏,打开了何种界面,鼠标灵敏度的调试到多少,最终恢复的值与其无关,最终恢复的值为受缓慢之前的值
    *
    *
    *
    *
    *
    *第四,玩家受缓慢效果,退出到主界面修改鼠标灵敏度为x,回到世界,那就恢复到灵敏度x了
    *
    *
    *
    * 第六,玩家在缓慢期间退出游戏,再登录上去,等到玩家缓慢效果消失时,会恢复到缓慢之前的鼠标灵敏度,而不是变成零不恢复
    *
    * 第七,不管玩家如何退出游戏,就算直接打叉关闭,也一定会经历一次esc暂停界面,所以等到玩家缓慢效果消失时,也会恢复到缓慢之前的鼠标灵敏度
    *
    * wiki语言:
    * 玩家受缓慢效果影响之前,会记录当前鼠标灵敏度,然后,鼠标灵敏度会被强制降低至零
    * 缓慢效果消失之前,鼠标灵敏度不受设置影响
    * 缓慢效果消失之后,恢复到记录之前的值,而不是最后一次鼠标灵敏度的设置值x,最后重新激活鼠标灵敏度设置
    * 只有玩家退出游戏之后再登入世界,记录才会更新一次,设置为当前鼠标灵敏度的设置值x,然后与上文条件一致,此时鼠标灵敏度不受设置影响,最后一次设置的值y将被抛弃,待到缓慢效果消失之后,将鼠标灵敏度恢复到x,最后重新激活鼠标灵敏度设置
    *
    * 简而言之,这套系统确保你在缓慢效果结束之后恢复到正常鼠标灵敏度,除非你趁缓慢每消失,马上退出游戏在主界面设置了一个哈欠的鼠标灵敏度,那么登录世界之后就还原到哈欠的灵敏度了
    *
    *
    *
    *
    *
    * */










    //原始灵敏度
    public double mouseSensitivityBefore;
    //应该触发灵敏度降低
    public boolean shouldTrigger = true;
    //应该恢复灵敏度
    public boolean shouldRecoverSoon = false;




    @Inject(method = "tick",at = @At("HEAD"))

    public void tick(CallbackInfo ci) {

        if(this.minecraft.screen!=null){
         //存在界面时,将鼠标灵敏度恢复到记录之前的值
            if(shouldRecoverSoon) {
                //设置的值是缓慢之前的鼠标灵敏度
                this.minecraft.options.sensitivity().set(mouseSensitivityBefore);
            }
            return;
        }
        if (this.isDeadOrDying()) {
            if (shouldRecoverSoon) {
                //设置的值是缓慢之前的鼠标灵敏度
                this.minecraft.options.sensitivity().set(mouseSensitivityBefore);
            }
            //死了之后还会有一些tick处于缓慢之中,此刻不应该再修改鼠标灵敏度了
            return;
        }

        if (this.hasEffect(MOVEMENT_SLOWDOWN)) {
            //只记录一次mouseSensitivityBefore

            //可以保证,在降低鼠标灵敏度之前,鼠标灵敏度已经被存储中
            if(shouldTrigger) {
                mouseSensitivityBefore = this.minecraft.options.sensitivity().get();
                shouldTrigger = false;
                //当效果结束时恢复玩家灵敏度
                shouldRecoverSoon =true;
            }
            //一旦覆盖了,就找不到原来的灵敏度值了
            this.minecraft.options.sensitivity().set(0d);
            //带着缓慢死亡时,应该恢复到原来的灵敏度,死亡后这个函数就不会再被执行了
        } else {
            //如果一开始玩家就没有缓慢效果,那么这段逻辑跳过
            //这段逻辑是恢复,但注意死亡后tick函数不再执行
            if(shouldRecoverSoon) {
                //设置的值是缓慢之前的鼠标灵敏度
                this.minecraft.options.sensitivity().set(mouseSensitivityBefore);
                shouldTrigger = true;
                shouldRecoverSoon =false;
            }
        }


    }

    @Inject(method = "hasEnoughFoodToStartSprinting",at = @At("HEAD"),cancellable = true)
    private void canSprint(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.isPassenger() || (float)this.getFoodData().getFoodLevel() > 0.0F || this.getAbilities().mayfly);

    }


}
