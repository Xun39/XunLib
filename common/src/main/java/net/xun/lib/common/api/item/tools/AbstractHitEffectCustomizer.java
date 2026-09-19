package net.xun.lib.common.api.item.tools;

import net.minecraft.world.entity.LivingEntity;

/**
 * @since 2.1.0
 */
public abstract class AbstractHitEffectCustomizer implements ToolCustomizer {

    /**
     * Handles the hit effect for a specific tool type on the target entity.
     * <p>
     * This method is called only on the server side and only when the attack successfully
     * damages the target. Subclasses must implement this method to define the actual effect,
     * such as applying potion effects, dealing additional damage, or playing sounds.
     * </p>
     *
     * @param piece    the type of tool that caused the hit
     * @param target   the entity that was hit
     * @param attacker the entity that performed the attack
     */
    protected abstract void onHit(ToolPieceType piece, LivingEntity target, LivingEntity attacker);

    /**
     * Invoked internally by the platform combat integration.
     */
    public final void triggerHit(ToolPieceType piece, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide) {
            onHit(piece, target, attacker);
        }
    }
}
