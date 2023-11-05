package com.dsd.tbb.customs.damages;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class GiantDamageSource extends DamageSource {

    protected GiantDamageSource(DamageType damageTypeIn) {
        super(Holder.direct(damageTypeIn));
    }

    // You can override methods or add new methods here to customize the behavior of your damage source
}

