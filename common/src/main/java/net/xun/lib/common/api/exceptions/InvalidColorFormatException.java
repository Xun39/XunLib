package net.xun.lib.common.api.exceptions;

public class InvalidColorFormatException extends IllegalArgumentException {
    public InvalidColorFormatException(String message) {
        super("Invalid color format: " + message);
    }
}
