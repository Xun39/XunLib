package net.xun.lib.common.api.misc.color;

public class InvalidColorFormatException extends IllegalArgumentException {
    public InvalidColorFormatException(String message) {
        super("Invalid color format: " + message);
    }
}
