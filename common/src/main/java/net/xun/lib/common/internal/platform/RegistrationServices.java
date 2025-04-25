package net.xun.lib.common.internal.platform;

import net.xun.lib.common.internal.item.ItemRegistrar;
import net.xun.lib.common.internal.platform.services.IRegistrationPlatform;

public class RegistrationServices {

    public static final IRegistrationPlatform PLATFORM = Services.load(IRegistrationPlatform.class);

    public static ItemRegistrar getItemRegistrar() {
        return PLATFORM.getItemRegistrar();
    }
}
