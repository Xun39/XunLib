package net.xun.lib.common.api.exceptions;

public class UtilityClassException extends IllegalAccessException {
    public UtilityClassException() {
      super("Cannot instantiate utility class");
    }
}
