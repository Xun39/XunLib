package net.xun.lib.common.api.registration;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record RegistrationEvent<C>(String id) {
    public RegistrationEvent(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    @Override
    public @NotNull String toString() {
        return "RegistrationEvent[" + id + "]";
    }
}
