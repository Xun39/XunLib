package net.xun.lib.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.xun.lib.common.XunLibCommon;
import net.xun.lib.common.XunLibConstants;

@Mod(XunLibConstants.MOD_ID)
public class XunLibNeoForge {

    public XunLibNeoForge(IEventBus modEventBus) {
        XunLibCommon.init();
    }
}
