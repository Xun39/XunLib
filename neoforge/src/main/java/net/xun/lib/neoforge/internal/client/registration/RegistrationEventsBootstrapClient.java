package net.xun.lib.neoforge.internal.client.registration;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.xun.lib.common.api.client.registration.ClientRegistrationEvents;
import net.xun.lib.common.api.registration.RegistryContainer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@OnlyIn(Dist.CLIENT)
public class RegistrationEventsBootstrapClient {
    public static void bootstrap() {
        NeoForge.EVENT_BUS.addListener(RegistrationEventsBootstrapClient::onRegisterItemColors);
    }

    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        RegistryContainer.dispatchCallbacks(ClientRegistrationEvents.ITEM_COLOR, event::register);
    }

    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        RegistryContainer.dispatchCallbacks(ClientRegistrationEvents.BLOCK_COLOR, event::register);
    }
}
