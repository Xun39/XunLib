package net.xun.lib.forge.internal;

import net.minecraftforge.fml.common.Mod;
import net.xun.lib.common.internal.XunLibCommon;
import net.xun.lib.common.internal.XunLibConstants;

@Mod(XunLibConstants.MOD_ID)
public class XunLibForge {

    public XunLibForge() {
        XunLibCommon.init();
    }
}