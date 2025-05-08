package net.xun.lib.neoforge.internal;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.lib.common.api.registries.Registrar;
import net.xun.lib.common.internal.XunLibCommon;
import net.xun.lib.common.internal.XunLibConstants;
import net.xun.lib.neoforge.api.registries.NeoForgeRegistrar;
import net.xun.lib.neoforge.internal.platform.NeoForgeRegistrationPlatform;

@Mod(XunLibConstants.MOD_ID)
public class XunLibNeoForge {

    public XunLibNeoForge(IEventBus modEventBus) {
        XunLibCommon.init();
        NeoForgeRegistrationPlatform.registerAllToEventBus(modEventBus);
    }
}