package net.xun.lib.common.internal.except;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class InvalidColorFormatException extends IllegalArgumentException {
    public InvalidColorFormatException(String message) {
        super("Invalid color format: " + message);
    }
}