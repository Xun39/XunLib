package net.xun.lib.fabric.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.xun.lib.common.api.item.tools.ToolMetaData;
import net.xun.lib.common.api.item.tools.ToolMetaDataLookup;
import net.xun.lib.common.impl.item.tools.ToolHitEffectDispatcher;

public final class FabricCombatEvents {
    private FabricCombatEvents() {
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (entity, source, baseDamageTaken, damageTaken, blocked) -> {
                    if (!(source.getEntity() instanceof LivingEntity attacker))
                        return;

                    ItemStack stack = attacker.getMainHandItem();
                    ToolMetaData meta = ToolMetaDataLookup.get(stack);
                    if (meta == null) {
                        stack = attacker.getOffhandItem();
                        meta = ToolMetaDataLookup.get(stack);
                    }
                    if (meta == null) return;

                    ToolHitEffectDispatcher.maybeTriggerHitEffect(entity, attacker, stack);
                }
        );
    }
}
