package net.xun.lib.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.xun.lib.fabric.internal.client.registration.RegistrationEventsBootstrapClient;

public class XunLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RegistrationEventsBootstrapClient.bootstrap();
    }
}
