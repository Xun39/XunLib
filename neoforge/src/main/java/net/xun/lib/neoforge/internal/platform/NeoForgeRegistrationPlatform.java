package net.xun.lib.neoforge.internal.platform;

import net.xun.lib.common.internal.item.ItemRegistrar;
import net.xun.lib.common.internal.platform.services.IRegistrationPlatform;
import net.xun.lib.neoforge.internal.item.tools.NeoForgeItemRegistrar;

public class NeoForgeRegistrationPlatform implements IRegistrationPlatform {
    @Override
    public ItemRegistrar getItemRegistrar() {
        return new NeoForgeItemRegistrar();
    }
}
