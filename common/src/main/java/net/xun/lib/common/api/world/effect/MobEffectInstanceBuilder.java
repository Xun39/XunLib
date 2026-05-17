package net.xun.lib.common.api.world.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.xun.lib.common.api.util.MobEffectUtils;

import java.util.List;
import java.util.Objects;

public class MobEffectInstanceBuilder {

    private final Holder<MobEffect> effect;

    private int duration = 0;
    private int amplifier = 0;

    private boolean ambient;
    private boolean visible = true;
    private boolean showIcon = true;

    public MobEffectInstanceBuilder(Holder<MobEffect> effect) {
        this.effect = Objects.requireNonNull(effect);
    }

    public static MobEffectInstanceBuilder of(Holder<MobEffect> effect) {
        return new MobEffectInstanceBuilder(effect);
    }

    public MobEffectInstanceBuilder duration(int ticks) {
        this.duration = Math.max(0, ticks);
        return this;
    }

    public MobEffectInstanceBuilder durationSeconds(int seconds) {
        return duration(seconds * 20);
    }

    public MobEffectInstanceBuilder amplifier(int amplifier) {
        this.amplifier = Math.max(0, amplifier);
        return this;
    }

    public MobEffectInstanceBuilder ambient(boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    public MobEffectInstanceBuilder visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public MobEffectInstanceBuilder showIcon(boolean showIcon) {
        this.showIcon = showIcon;
        return this;
    }

    public MobEffectInstance build() {
        return new MobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon);
    }
}
