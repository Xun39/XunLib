package net.xun.lib.forge.internal.platform;

import net.xun.lib.common.internal.item.ItemRegistrar;
import net.xun.lib.common.internal.platform.services.IRegistrationPlatform;
import net.xun.lib.forge.internal.item.tools.ForgeItemRegistrar;

public class ForgeRegistrationPlatform implements IRegistrationPlatform {
    @Override
    public ItemRegistrar getItemRegistrar() {
        return new ForgeItemRegistrar();
    }
}
