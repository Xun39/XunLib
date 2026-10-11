package net.xun.lib.common;

import net.xun.lib.common.api.registration.RegistryContainer;
import net.xun.lib.common.platform.Services;
import net.xun.lib.common.internal.content.XunLibStructureTypes;

public class XunLibCommon {
    public static final RegistryContainer REGISTRY = RegistryContainer.of(XunLibConstants.MOD_ID);

    public static void init() {
        XunLibConstants.LOGGER.info("Loading XunLib version {} for {}!", XunLibConstants.VERSION, Services.PLATFORM.getPlatformName());

        XunLibStructureTypes.register();
        REGISTRY.registerAll();
    }
}
