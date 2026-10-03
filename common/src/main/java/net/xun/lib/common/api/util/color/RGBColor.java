package net.xun.lib.common.api.util.color;

import net.xun.lib.common.impl.except.InvalidColorFormatException;

public record RGBColor(int red, int green, int blue) implements IColor {
    public RGBColor {
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            throw new InvalidColorFormatException("RGB components must be between 0 and 255");
        }
    }

    @Override
    public int toHex() {
        return (red << 16) | (green << 8) | blue;
    }
}
