package net.xun.lib.fabric.internal;

import net.fabricmc.api.ModInitializer;
import net.xun.lib.common.internal.XunLibCommon;

public class XunLibFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        XunLibCommon.init();
    }
}
