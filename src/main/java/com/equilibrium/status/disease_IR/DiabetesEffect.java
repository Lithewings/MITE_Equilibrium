package com.equilibrium.status.disease_IR;

import java.util.ArrayList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class DiabetesEffect {

    
    public static int getProgress(int diabetes){
        int progress = -1;
        if(diabetes>=90000)
            progress=3;
        else if (diabetes>=72000)
            progress=2;
        else if(diabetes>=60000)
            progress=1;
        else if(diabetes>=48000)
            progress=0;
        return progress;
    }
    public static void tryApplyDiabetesEffect(Player playerEntity, int diabetes){

        MobEffectInstance BLINDNESS= new MobEffectInstance(MobEffects.BLINDNESS,1200, 255, false, false, false);
        MobEffectInstance NAUSEA= new MobEffectInstance(MobEffects.CONFUSION, 600, 255, false, false, false);
        MobEffectInstance WEAKNESS= new MobEffectInstance(MobEffects.WEAKNESS, 600, 255, false, false, false);
        MobEffectInstance POISON= new MobEffectInstance(MobEffects.POISON, 80, 0, false, false, false);


        int progress = getProgress(diabetes);
        ArrayList<MobEffectInstance> effectInstanceList = new ArrayList<>();

        //覆盖式施加效果
        if(progress==3){
            effectInstanceList.add(BLINDNESS);
            effectInstanceList.add(WEAKNESS);
            effectInstanceList.add(NAUSEA);
            effectInstanceList.add(POISON);
        }
        else if (progress == 2) {
            effectInstanceList.add(WEAKNESS);
            effectInstanceList.add(NAUSEA);
            effectInstanceList.add(POISON);
        } else if (progress == 1) {
            effectInstanceList.add(NAUSEA);
            effectInstanceList.add(POISON);
        } else if (progress == 0) {
            effectInstanceList.add(NAUSEA);
            effectInstanceList.add(POISON);
        }
        effectInstanceList.forEach(effect-> playerEntity.forceAddEffect(effect,null));
    }
}
