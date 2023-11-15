package com.dsd.tbb.customs.damages;

import com.dsd.tbb.util.EnumTypes;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class GiantDamageSource extends DamageSource {

    private final EnumTypes.GiantAttackType attackType;

    public GiantDamageSource(EnumTypes.GiantAttackType attackType, @Nullable Entity causingEntity,
                             @Nullable Entity directEntity, @Nullable Vec3 pos) {
        super(Holder.direct(new DamageType("giantAttack",1)), causingEntity, directEntity, pos);
        this.attackType = attackType;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity victim) {
        String victimName = victim.getScoreboardName();
        StringBuilder sb = new StringBuilder();
        String attackMessage;
        switch (attackType){
            case CHARGE:
                attackMessage = " succumbed to the unstoppable Giants Charge";
                break;
            case SMACKDOWN:
                attackMessage = " was flattened by the Giants shattering Smash";
                break;
            default:
                attackMessage = " was beaten by the Giants fists";
                break;
        }

        sb.append(victimName).append(" ").append(attackMessage);
        return Component.literal(sb.toString());

    }
}

