package net.xun.lib.common.api.world.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface EffectStackingStrategy {

    void apply(LivingEntity entity, MobEffectInstance current, MobEffectInstance incoming);
}
