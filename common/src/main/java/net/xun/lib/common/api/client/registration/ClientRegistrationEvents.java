package net.xun.lib.common.api.client.registration;

import net.xun.lib.common.api.client.registration.context.BlockColorContext;
import net.xun.lib.common.api.client.registration.context.ItemColorContext;
import net.xun.lib.common.api.registration.RegistrationEvent;

public final class ClientRegistrationEvents {
    private ClientRegistrationEvents() {}

    public static final RegistrationEvent<ItemColorContext> ITEM_COLOR = new RegistrationEvent<>("item_color");
    public static final RegistrationEvent<BlockColorContext> BLOCK_COLOR = new RegistrationEvent<>("block_color");
}
