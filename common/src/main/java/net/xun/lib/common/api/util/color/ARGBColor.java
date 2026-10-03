package net.xun.lib.common.api.util.color;

import net.xun.lib.common.impl.except.InvalidColorFormatException;

public record ARGBColor(int alpha, int red, int green, int blue) implements IColor {
    public ARGBColor {
        if (alpha < 0 || alpha > 255 || red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            throw new InvalidColorFormatException("ARGB components must be between 0 and 255");
        }
    }

    public ARGBColor(int red, int green, int blue) {
        this(255, red, green, blue);
    }

    public static ARGBColor fromHex(int argb) {
        return new ARGBColor(
                (argb >>> 24) & 0xFF,
                (argb >> 16) & 0xFF,
                (argb >> 8) & 0xFF,
                argb & 0xFF
        );
    }

    @Override
    public int toHex() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}