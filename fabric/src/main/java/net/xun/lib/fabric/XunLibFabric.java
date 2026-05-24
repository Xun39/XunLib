package net.xun.lib.fabric;

import net.fabricmc.api.ModInitializer;
import net.xun.lib.common.impl.XunLibCommon;

public class XunLibFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        XunLibCommon.init();
    }
}
