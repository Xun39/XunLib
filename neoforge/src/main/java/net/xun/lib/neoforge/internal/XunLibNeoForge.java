package net.xun.lib.neoforge.internal;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.xun.lib.common.internal.XunLibCommon;
import net.xun.lib.common.internal.XunLibConstants;

@Mod(XunLibConstants.MOD_ID)
public class XunLibNeoForge {

    public XunLibNeoForge(IEventBus eventBus) {
        XunLibCommon.init();
    }
}