package net.xun.lib.common.impl.item.tools;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.xun.lib.common.api.item.tools.AbstractHitEffectCustomizer;
import net.xun.lib.common.api.item.tools.ToolMetaData;
import net.xun.lib.common.api.item.tools.ToolMetaDataLookup;

public final class ToolHitEffectDispatcher {
    private ToolHitEffectDispatcher() {
    }

    /**
     * Looks up the given stack's tool metadata and, if its customizer defines a hit effect,
     * triggers it. No-ops silently if the stack has no tool metadata, the customizer isn't
     * an {@link AbstractHitEffectCustomizer}, or the call happens on the client.
     */
    public static void maybeTriggerHitEffect(LivingEntity target, LivingEntity attacker, ItemStack weaponStack) {
        if (target.level().isClientSide) return;

        ToolMetaData meta = ToolMetaDataLookup.get(weaponStack);
        if (meta == null) return;
        if (!(meta.customizer() instanceof AbstractHitEffectCustomizer customizer)) return;

        customizer.triggerHit(meta.piece(), target, attacker);
    }
}
