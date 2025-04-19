package net.xun.lib.common.internal;

import net.xun.lib.common.internal.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

public class XunLibCommon {

    public static void init() {
        XunLibConstants.LOGGER.info("Loading XunLib version {} for {}!", XunLibConstants.VERSION, Services.PLATFORM.getPlatformName());
    }
}