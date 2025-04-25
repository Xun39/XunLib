package net.xun.lib.fabric.internal.platform;

import net.xun.lib.common.internal.item.ItemRegistrar;
import net.xun.lib.common.internal.platform.services.IRegistrationPlatform;
import net.xun.lib.fabric.internal.item.tools.FabricItemRegistrar;

public class FabricRegistrationPlatform implements IRegistrationPlatform {
    @Override
    public ItemRegistrar getItemRegistrar() {
        return new FabricItemRegistrar();
    }
}
