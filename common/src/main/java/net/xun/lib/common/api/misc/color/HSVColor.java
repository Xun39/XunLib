package net.xun.lib.common.api.misc.color;

/**
 * Represents a color in the HSV (Hue, Saturation, Value) color space.
 */
public record HSVColor(float hue, float saturation, float value) implements IColorBase {

    /**
     * Constructs an HSVColor with the specified hue, saturation, and lightness.
     *
     * @param hue        the hue component (0-360 degrees)
     * @param saturation the saturation component (0-1)
     * @param value  the lightness component (0-1)
     * @throws InvalidColorFormatException if hue, saturation, or lightness are out of range
     */
    public HSVColor {
        validateParameters();
    }

    @Override
    public RGBColor asRGB() {
        return ColorConverter.HSVtoRGB(this);
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
        if (value < 0 || value > 1) {
            throw new InvalidColorFormatException("Value must be between 0 and 1");
        }
    }
}
