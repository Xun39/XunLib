package net.xun.lib.common.api.registration;

@FunctionalInterface
public interface RegistrationCallback<C> {
    void register(C context);
}
