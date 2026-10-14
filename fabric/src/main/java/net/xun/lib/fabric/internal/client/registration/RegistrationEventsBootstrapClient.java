package net.xun.lib.fabric.internal.client.registration;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.xun.lib.common.api.client.registration.ClientRegistrationEvents;
import net.xun.lib.common.api.registration.RegistryContainer;

public class RegistrationEventsBootstrapClient {
    public static void bootstrap() {
        RegistryContainer.dispatchCallbacks(ClientRegistrationEvents.ITEM_COLOR, ColorProviderRegistry.ITEM::register);
        RegistryContainer.dispatchCallbacks(ClientRegistrationEvents.BLOCK_COLOR, ColorProviderRegistry.BLOCK::register);
    }
}
