package net.xun.lib.common.api.world.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.xun.lib.common.api.exceptions.UtilityClassException;

public final class EffectStackingStrategies {
    private EffectStackingStrategies() throws UtilityClassException {
        throw new UtilityClassException();
    }

    /**
     * Only upgrades stronger effects.
     */
    public static final EffectStackingStrategy UPGRADE_EXISTING =
            (entity, current, incoming) -> {
                if (current == null || incoming.getAmplifier() > current.getAmplifier()) {
                    entity.addEffect(new MobEffectInstance(incoming));
                }
            };

    /**
     * Extends duration while preserving the strongest amplifier.
     */
    public static final EffectStackingStrategy EXTEND_DURATION =
            (entity, current, incoming) -> {

                if (current == null) {
                    entity.addEffect(new MobEffectInstance(incoming));
                    return;
                }

                MobEffectInstance combined = new MobEffectInstance(
                        incoming.getEffect(),
                        current.getDuration() + incoming.getDuration(),
                        Math.max(current.getAmplifier(), incoming.getAmplifier()),
                        incoming.isAmbient(),
                        incoming.isVisible(),
                        incoming.showIcon()
                );

                entity.addEffect(combined);
            };

    /**
     * Prevents duplicate application.
     */
    public static final EffectStackingStrategy PREVENT_STACKING =
            (entity, current, incoming) -> {
                if (current == null) {
                    entity.addEffect(new MobEffectInstance(incoming));
                }
            };

    /**
     * Always replaces current effect.
     */
    public static final EffectStackingStrategy FORCE_OVERRIDE =
            (entity, current, incoming) -> {
                entity.addEffect(new MobEffectInstance(incoming));
            };
}
