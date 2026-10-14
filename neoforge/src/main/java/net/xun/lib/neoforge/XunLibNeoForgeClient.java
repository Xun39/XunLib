package net.xun.lib.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.xun.lib.common.XunLibConstants;
import net.xun.lib.neoforge.internal.client.registration.RegistrationEventsBootstrapClient;

@Mod(value = XunLibConstants.MOD_ID, dist = Dist.CLIENT)
public class XunLibNeoForgeClient {
    public XunLibNeoForgeClient(IEventBus modEventBus, ModContainer modContainer) {
        RegistrationEventsBootstrapClient.bootstrap();
    }
}
