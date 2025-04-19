package net.xun.lib.common.api.misc.color;

/**
 * Represents a color in the RGB (Red, Green, Blue) color space.
 */
public record RGBColor(float red, float green, float blue) implements ColorBase {

    /**
     * Constructs an RGBColor with the specified red, green, and blue.
     *
     * @param red   the red component (0-255)
     * @param green the green component (0-255)
     * @param blue  the blue component (0-255)
     * @throws InvalidColorFormatException if any component is outside the range [0, 255]
     */
    public RGBColor {
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            throw new InvalidColorFormatException("RGB components must be between 0 and 255");
        }
    }

    @Override
    public String asHex() {
        return String.format("#%02X%02X%02X",
                Math.round(red),
                Math.round(green),
                Math.round(blue));
    }

    @Override
    public RGBColor asRGB() {
        return this;
    }
}