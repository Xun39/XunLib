package net.xun.lib.neoforge.internal;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.lib.common.internal.XunLibCommon;
import net.xun.lib.common.internal.XunLibConstants;

@Mod(XunLibConstants.MOD_ID)
public class XunLibNeoForge {

    private static IEventBus modEventBus;

    public XunLibNeoForge(IEventBus modEventBus) {
        XunLibNeoForge.modEventBus = modEventBus;

        XunLibCommon.init();
    }

    public static void addDeferredRegister(DeferredRegister<?> register) {
        register.register(modEventBus);
    }
}