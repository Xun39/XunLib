package net.xun.lib.common.api.util;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.xun.lib.common.api.exceptions.UtilityClassException;
import net.xun.lib.common.api.world.effect.EffectStackingStrategy;

import java.util.List;
import java.util.Objects;

/**
 * Utility methods for working with {@link MobEffectInstance}s.
 */
public final class MobEffectUtils {
    private MobEffectUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    /**
     * Returns whether the entity has an effect equivalent to or better than the provided one.
     * An effect is considered sufficient if:
     * <ul>
     *     <li>It will not expire within the threshold, OR</li>
     *     <li>Its amplifier and duration are both greater than or equal to the incoming effect</li>
     * </ul>
     *
     * @param entity          Target entity
     * @param effect          Incoming effect
     * @param expiryThreshold Refresh threshold in ticks
     * @return true if the current effect is sufficient
     */
    public static boolean hasSufficientEffect(LivingEntity entity, MobEffectInstance effect, int expiryThreshold) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(effect, "effect");

        MobEffectInstance current = entity.getEffect(effect.getEffect());

        if (current == null) {
            return false;
        }

        boolean durationSafe = !current.endsWithin(expiryThreshold);
        boolean strongerOrEqual = current.getAmplifier() >= effect.getAmplifier() && current.getDuration() >= effect.getDuration();

        return durationSafe || strongerOrEqual;
    }

    /**
     * Applies effects only if they are not already sufficiently active.
     *
     * @param entity Target entity
     * @param effects Effects to apply
     * @param expiryThreshold Refresh threshold in ticks
     * @param forceAdd Whether to bypass sufficiency checks
     */
    public static void applySmartEffects(LivingEntity entity, List<MobEffectInstance> effects, int expiryThreshold, boolean forceAdd) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(effects, "effects");

        for (MobEffectInstance effect : effects) {
            applyEffect(entity, effect, expiryThreshold, forceAdd);
        }
    }

    /**
     * Applies an effect if forced or not already sufficiently present.
     *
     * @param entity Target entity
     * @param effect Effect to apply
     * @param expiryThreshold Refresh threshold in ticks
     * @param forceAdd Whether to bypass sufficiency checks
     */
    public static void applyEffect(LivingEntity entity, MobEffectInstance effect, int expiryThreshold, boolean forceAdd) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(effect, "effect");

        if (forceAdd || !hasSufficientEffect(entity, effect, expiryThreshold)) {
            entity.addEffect(new MobEffectInstance(effect));
        }
    }

    public static void applyEffectWithStrategy(LivingEntity entity, MobEffectInstance effect, EffectStackingStrategy strategy) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(effect);
        Objects.requireNonNull(strategy);

        MobEffectInstance current = entity.getEffect(effect.getEffect());

        strategy.apply(entity, current, effect);
    }
}
