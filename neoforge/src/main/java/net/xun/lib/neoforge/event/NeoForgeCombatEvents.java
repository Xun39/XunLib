package net.xun.lib.neoforge.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.xun.lib.common.XunLibConstants;
import net.xun.lib.common.api.item.tools.ToolMetaData;
import net.xun.lib.common.api.item.tools.ToolMetaDataLookup;
import net.xun.lib.common.impl.item.tools.ToolHitEffectDispatcher;

@EventBusSubscriber(modid = XunLibConstants.MOD_ID)
public final class NeoForgeCombatEvents {

    private NeoForgeCombatEvents() {
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker))
            return;

        ItemStack stack = attacker.getMainHandItem();
        ToolMetaData meta = ToolMetaDataLookup.get(stack);
        if (meta == null) {
            stack = attacker.getOffhandItem();
            meta = ToolMetaDataLookup.get(stack);
        }
        if (meta == null) return;

        ToolHitEffectDispatcher.maybeTriggerHitEffect(event.getEntity(), attacker, stack);
    }
}
