package net.xun.lib.common.impl;

import net.xun.lib.common.platform.Services;

public class XunLibCommon {

    public static void init() {
        XunLibConstants.LOGGER.info("Loading XunLib version {} for {}!", XunLibConstants.VERSION, Services.PLATFORM.getPlatformName());
    }
}
