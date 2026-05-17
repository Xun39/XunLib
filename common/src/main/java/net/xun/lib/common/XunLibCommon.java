package net.xun.lib.common;

import net.xun.lib.common.internal.platform.Services;

public class XunLibCommon {

    public static void init() {
        XunLibConstants.LOGGER.info("Loading XunLib version {} for {}!", XunLibConstants.VERSION, Services.PLATFORM.getPlatformName());
    }
}