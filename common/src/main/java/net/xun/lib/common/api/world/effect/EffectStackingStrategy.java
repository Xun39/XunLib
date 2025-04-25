package net.xun.lib.common.api.world.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiConsumer;

/**
 * Defines strategies for applying mob effects to entities, controlling how new effects
 * interact with existing ones. Each strategy implements different merging logic.
 */
public enum EffectStackingStrategy {

    /**
     * Applies effect only if it doesn't exist or has a higher amplifier than current.
     * Duration remains unchanged.
     * <P>
     * Example: Applying Strength II when Strength I is active.
     */
    UPGRADE_EXISTING((entity, effect) -> {
        MobEffectInstance current = entity.getEffect(effect.getEffect());
        if (current == null || current.getAmplifier() < effect.getAmplifier()) {
            entity.addEffect(effect);
        }
    }),

    /**
     * Combines durations and keeps the highest amplifier when reapplying.
     * <p>
     * Example: Adding 30s of Fire Resistance to existing 60s gives 90s total.
     */
    EXTEND_DURATION((entity, effect) -> {
        MobEffectInstance current = entity.getEffect(effect.getEffect());
        if (current != null) {
            entity.addEffect(new MobEffectInstance(
                    effect.getEffect(),
                    current.getDuration() + effect.getDuration(),
                    Math.max(current.getAmplifier(), effect.getAmplifier()),
                    effect.isAmbient(),
                    effect.isVisible()
            ));
        } else {
            entity.addEffect(effect);
        }
    }),

    PREVENT_STACKING((entity, effect) -> {
        MobEffectInstance current = entity.getEffect(effect.getEffect());
        if (current == null) {
            entity.addEffect(new MobEffectInstance(
                    effect.getEffect(),
                    effect.getDuration(),
                    effect.getAmplifier(),
                    effect.isAmbient(),
                    effect.isVisible()
            ));
        }
    }),

    /**
     * Always applies the new effect, overwriting any existing version.
     * <p>
     * Example: Applying a new Poison effect replaces current Poison completely.
     */
    FORCE_OVERRIDE(LivingEntity::addEffect);

    private final BiConsumer<LivingEntity, MobEffectInstance> applier;

    EffectStackingStrategy(BiConsumer<LivingEntity, MobEffectInstance> applier) {
        this.applier = applier;
    }

    public void apply(LivingEntity entity, MobEffectInstance effect) {
        applier.accept(entity, effect);
    }
}
