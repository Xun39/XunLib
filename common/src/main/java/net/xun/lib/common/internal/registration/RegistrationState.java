package net.xun.lib.common.internal.registration;

import org.jetbrains.annotations.ApiStatus;

// To assure registration behaviors are not loader dependent
@ApiStatus.Internal
public enum RegistrationState {
    STARTED, REGISTERING, REGISTERED
}
