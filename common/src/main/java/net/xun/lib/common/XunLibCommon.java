package net.xun.lib.common;

import net.xun.lib.common.platform.Services;
import net.xun.lib.common.registry.XLStructureTypes;

public class XunLibCommon {
    public static void init() {
        XunLibConstants.LOGGER.info("Loading XunLib version {} for {}!", XunLibConstants.VERSION, Services.PLATFORM.getPlatformName());
        XLStructureTypes.STRUCTURE_TYPES.register();
    }
}
