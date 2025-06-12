package net.xun.lib.common.api.util;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.xun.lib.common.api.exceptions.UtilityClassException;
import net.xun.lib.common.api.world.effect.EffectStackingStrategy;

import java.util.List;

/**
 * Utility class for applying mob effects to entities with various strategies and conditions.
 * Works in conjunction with {@link EffectStackingStrategy} to determine how effects interact.
 */
public class MobEffectUtils {

    private MobEffectUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    /**
     * Applies effects to an entity using a specified {@link EffectStackingStrategy}.
     *
     * @param entity   Target entity (must not be null)
     * @param effects  List of effects to apply (must not contain null elements)
     * @param strategy Application strategy from {@link EffectStackingStrategy}
     */
    public static void applyEffectsWithStrategy(LivingEntity entity, List<MobEffectInstance> effects, EffectStackingStrategy strategy) {
        effects.forEach(e -> strategy.apply(entity, e));
    }

    /**
     * Applies effects to an entity only if they are not sufficiently present or if forced.
     * Each effect is checked individually: if {@code forceAdd} is true, the effect is always applied.
     * Otherwise, it is applied only if the entity does not already have a sufficient version.
     *
     * @param entity           The target entity. Must not be null.
     * @param effects          The list of effects to apply. Must not be null and must not contain null elements.
     * @param expiryThreshold The minimum remaining duration (in ticks) an existing effect must have
     *                        to be considered sufficient.
     * @param forceAdd         If true, applies effects regardless of existing ones.
     * @throws NullPointerException if entity, effects, or any effect in the list is null.
     */
    public static void applySmartEffects(LivingEntity entity, List<MobEffectInstance> effects, int expiryThreshold, boolean forceAdd) {
        if (entity == null || effects == null || effects.isEmpty()) return;

        boolean shouldApply = effects.stream().noneMatch(e -> hasSufficientEffect(entity, e, expiryThreshold));
        if (shouldApply) {
            effects.forEach(e -> applySingleEffect(entity, e, expiryThreshold, forceAdd));
        }
    }

    /**
     * Applies a single effect to the entity if conditions are met or if forced.
     *
     * @param entity           The target entity. Must not be null.
     * @param effect           The effect to apply. Must not be null.
     * @param expiryThreshold The minimum remaining duration (in ticks) for an existing effect to be
     *                        considered sufficient.
     * @param forceAdd         If true, applies the effect regardless of existing ones.
     * @throws NullPointerException if entity or effect is null.
     */
    public static void applySingleEffect(LivingEntity entity, MobEffectInstance effect, int expiryThreshold, boolean forceAdd) {
        if (forceAdd || !hasSufficientEffect(entity, effect, expiryThreshold)) {
            entity.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
        }
    }

    /**
     * Checks if the entity has a sufficient version of the given effect.
     * A sufficient effect has:
     * <ul>
     *   <li>Remaining duration greater than {@code expiryThreshold}</li>
     *   <li>Amplifier equal to or higher than the new effect</li>
     *   <li>Remaining duration equal to or longer than the new effect's duration</li>
     * </ul>
     *
     * @param entity           The entity to check. Must not be null.
     * @param effect           The effect to check for. Must not be null.
     * @param expiryThreshold The threshold for remaining duration (in ticks).
     * @return true if the entity has a sufficient effect, false otherwise.
     * @throws NullPointerException if entity or effect is null.
     */
    public static boolean hasSufficientEffect(LivingEntity entity, MobEffectInstance effect, int expiryThreshold) {
        MobEffectInstance current = entity.getEffect(effect.getEffect());
        if (current == null) return false;
        return !current.endsWithin(expiryThreshold) ||
                (current.getAmplifier() >= effect.getAmplifier() && current.getDuration() >= effect.getDuration());
    }
}
