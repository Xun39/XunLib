package net.xun.lib.forge.internal;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.xun.lib.common.internal.XunLibCommon;
import net.xun.lib.common.internal.XunLibConstants;
import net.xun.lib.forge.internal.platform.ForgeRegistrationPlatform;

@Mod(XunLibConstants.MOD_ID)
public class XunLibForge {

    public XunLibForge() {
        XunLibCommon.init();
        ForgeRegistrationPlatform.registerAllToEventBus(FMLJavaModLoadingContext.get().getModEventBus());
    }
}