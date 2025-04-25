package net.xun.lib.forge.internal;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.xun.lib.common.internal.XunLibCommon;
import net.xun.lib.common.internal.XunLibConstants;
import net.xun.lib.forge.internal.item.tools.ForgeItemRegistrar;

@Mod(XunLibConstants.MOD_ID)
public class XunLibForge {

    public XunLibForge() {

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        XunLibCommon.init();
        ForgeItemRegistrar.setRegistry(eventBus);
    }
}