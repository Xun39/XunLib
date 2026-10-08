package net.xun.lib.fabric.internal.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.xun.lib.common.api.config.XunConfigManager;
import net.xun.lib.common.internal.config.XunConfigRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FabricConfigEvents {
    private FabricConfigEvents() {
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(XunConfigRegistry::loadServerConfigs);
        ServerLifecycleEvents.SERVER_STOPPING.register(XunConfigRegistry::unloadServerConfigs);
    }
}