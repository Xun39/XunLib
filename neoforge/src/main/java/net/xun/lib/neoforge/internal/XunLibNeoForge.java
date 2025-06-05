package net.xun.lib.neoforge.internal;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.lib.common.internal.XunLibCommon;
import net.xun.lib.common.internal.XunLibConstants;

import java.util.HashMap;
import java.util.Map;

@Mod(XunLibConstants.MOD_ID)
public class XunLibNeoForge {

    private static IEventBus modEventBus;

    private static final Map<String, DeferredRegister.Items> ITEM_REGISTERS = new HashMap<>();
    private static final Map<String, DeferredRegister.Blocks> BLOCK_REGISTERS = new HashMap<>();

    public XunLibNeoForge(IEventBus modEventBus) {
        XunLibNeoForge.modEventBus = modEventBus;

        XunLibCommon.init();
    }

    public static void addDeferredRegister(DeferredRegister<?> register) {
        register.register(modEventBus);
    }


    public static DeferredRegister.Items getOrCreateItemDeferredRegister(String namespace) {
        return ITEM_REGISTERS.computeIfAbsent(namespace, ns -> {
            DeferredRegister.Items register = DeferredRegister.createItems(ns);
            register.register(modEventBus);
            return register;
        });
    }

    public static DeferredRegister.Blocks getOrCreateBlockDeferredRegister(String namespace) {
        return BLOCK_REGISTERS.computeIfAbsent(namespace, ns -> {
            DeferredRegister.Blocks register = DeferredRegister.createBlocks(ns);
            register.register(modEventBus);
            return register;
        });
    }
}