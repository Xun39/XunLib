package net.xun.lib.common.api.world.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.xun.lib.common.api.util.MobEffectUtils;

import java.util.List;

public class MobEffectInstanceBuilder {

    private final Holder<MobEffect> effect;
    private int duration = 0;
    private int amplifier = 0;
    private boolean isAmbient = true;
    private boolean isVisible = true;
    private boolean showIcon = true;
    private EffectStackingStrategy strategy = EffectStackingStrategy.FORCE_OVERRIDE;

    public MobEffectInstanceBuilder(Holder<MobEffect> effect) {
        this.effect = effect;
    }

    public static MobEffectInstanceBuilder of(Holder<MobEffect> effect) {
        return new MobEffectInstanceBuilder(effect);
    }

    public MobEffectInstanceBuilder withDuration(int ticks) {
        this.duration = Math.max(0, ticks);
        return this;
    }

    public MobEffectInstanceBuilder withAmplifier(int amplifier) {
        this.amplifier = Math.max(0, Math.min(amplifier, 127));
        return this;
    }

    public MobEffectInstanceBuilder ambient() {
        this.isAmbient = true;
        return this;
    }

    public MobEffectInstanceBuilder hidden() {
        this.isVisible = false;
        return this;
    }

    public MobEffectInstanceBuilder hideIcon() {
        this.showIcon = false;
        return this;
    }

    public MobEffectInstanceBuilder withApplyStrategy(EffectStackingStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public MobEffectInstance build() {
        return new MobEffectInstance(effect, duration, amplifier, isAmbient, isVisible, showIcon);
    }

    public void applyTo(LivingEntity living) {
        if (living == null || living.level().isClientSide) return;
        MobEffectUtils.applyEffectsWithStrategy(living, List.of(build()), this.strategy);
    }
}
