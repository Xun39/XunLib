package net.xun.lib.neoforge.internal.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.xun.lib.common.XunLibConstants;
import net.xun.lib.common.api.config.XunConfigManager;
import org.jetbrains.annotations.ApiStatus;

@EventBusSubscriber(modid = XunLibConstants.MOD_ID)
@ApiStatus.Internal
public final class NeoForgeConfigEvents {
    private NeoForgeConfigEvents() {
    }

    @SubscribeEvent
    private static void onServerAboutToStart(ServerAboutToStartEvent event) {
        XunConfigManager.loadServerConfigs(event.getServer());
    }

    @SubscribeEvent
    private static void onServerStopping(ServerStoppingEvent event) {
        XunConfigManager.unloadServerConfigs(event.getServer());
    }
}