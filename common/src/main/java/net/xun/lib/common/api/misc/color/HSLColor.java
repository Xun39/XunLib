package net.xun.lib.common.api.misc.color;

import net.xun.lib.common.api.exceptions.InvalidColorFormatException;

/**
 * Represents a color in the HSL (Hue, Saturation, Lightness) color space.
 */
public record HSLColor(float hue, float saturation, float lightness) implements IColorBase {

    /**
     * Constructs an HSLColor with the specified hue, saturation, and lightness.
     *
     * @param hue        the hue component (0-360 degrees)
     * @param saturation the saturation component (0-1)
     * @param lightness  the lightness component (0-1)
     * @throws InvalidColorFormatException if hue, saturation, or lightness are out of range
     */
    public HSLColor {
        validateParameters();
    }

    @Override
    public RGBColor asRGB() {
        return ColorConverter.HSLtoRGB(this);
    }

    @Override
    public String asHex() {
        return this.asRGB().asHex();
    }

    private void validateParameters() {
        if (hue < 0 || hue > 360) {
            throw new InvalidColorFormatException("Hue must be between 0 and 360");
        }
        if (saturation < 0 || saturation > 1) {
            throw new InvalidColorFormatException("Saturation must be between 0 and 1");
        }
        if (lightness < 0 || lightness > 1) {
            throw new InvalidColorFormatException("Lightness must be between 0 and 1");
        }
    }
}