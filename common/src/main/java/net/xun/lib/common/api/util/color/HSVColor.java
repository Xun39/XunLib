package net.xun.lib.common.api.util.color;

import net.xun.lib.common.impl.except.InvalidColorFormatException;

public record HSVColor(float hue, float saturation, float value) implements IColor {
    public HSVColor {
        if (hue < 0 || hue > 360) {
            throw new InvalidColorFormatException("Hue must be between 0 and 360");
        }
        if (saturation < 0 || saturation > 1) {
            throw new InvalidColorFormatException("Saturation must be between 0 and 1");
        }
        if (value < 0 || value > 1) {
            throw new InvalidColorFormatException("Value must be between 0 and 1");
        }
    }

    @Override
    public int toHex() {
        return ColorConverter.hsv2Rgb(this).toHex();
    }
}
