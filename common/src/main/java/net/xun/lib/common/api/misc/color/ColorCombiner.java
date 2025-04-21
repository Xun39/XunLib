package net.xun.lib.common.api.misc.color;

public class ColorCombiner {

    public static RGBColor blendAdd(IColorBase color1, IColorBase color2) {
        RGBColor rgb1 = color1.asRGB();
        RGBColor rgb2 = color2.asRGB();

        float red = clamp(rgb1.red() + rgb2.red());
        float green = clamp(rgb1.green() + rgb2.green());
        float blue = clamp(rgb1.blue() + rgb2.blue());

        return new RGBColor(red, green, blue);
    }

    public static RGBColor blendAverage(IColorBase color1, IColorBase color2) {
        RGBColor rgb1 = color1.asRGB();
        RGBColor rgb2 = color2.asRGB();

        float red = (rgb1.red() + rgb2.red()) / 2;
        float green = (rgb1.green() + rgb2.green()) / 2;
        float blue = (rgb1.blue() + rgb2.blue()) / 2;

        return new RGBColor(red, green, blue);
    }

    public static RGBColor blendMultiply(IColorBase color1, IColorBase color2) {
        RGBColor rgb1 = color1.asRGB();
        RGBColor rgb2 = color2.asRGB();

        float red = (rgb1.red() * rgb2.red()) / 255f;
        float green = (rgb1.green() * rgb2.green()) / 255f;
        float blue = (rgb1.blue() * rgb2.blue()) / 255f;

        return new RGBColor(red, green, blue);
    }

    public static RGBColor blendScreen(IColorBase color1, IColorBase color2) {
        RGBColor rgb1 = color1.asRGB();
        RGBColor rgb2 = color2.asRGB();

        float red = 255f - ((255f - rgb1.red()) * (255f - rgb2.red()) / 255f);
        float green = 255f - ((255f - rgb1.green()) * (255f - rgb2.green()) / 255f);
        float blue = 255f - ((255f - rgb1.blue()) * (255f - rgb2.blue()) / 255f);

        return new RGBColor(red, green, blue);
    }

    public static RGBColor blendOverlay(IColorBase color1, IColorBase color2) {
        RGBColor rgb1 = color1.asRGB();
        RGBColor rgb2 = color2.asRGB();

        float red = calculateOverlay(rgb1.red(), rgb2.red());
        float green = calculateOverlay(rgb1.green(), rgb2.green());
        float blue = calculateOverlay(rgb1.blue(), rgb2.blue());

        return new RGBColor(red, green, blue);
    }

    private static float calculateOverlay(float base, float blend) {
        if (base < 128f) {
            return (2 * base * blend) / 255f;
        } else {
            return 255f - (2 * (255f - base) * (255f - blend) / 255f);
        }
    }

    public static RGBColor blendLighten(IColorBase color1, IColorBase color2) {
        RGBColor rgb1 = color1.asRGB();
        RGBColor rgb2 = color2.asRGB();

        float red = Math.max(rgb1.red(), rgb2.red());
        float green = Math.max(rgb1.green(), rgb2.green());
        float blue = Math.max(rgb1.blue(), rgb2.blue());

        return new RGBColor(red, green, blue);
    }

    public static RGBColor blendDarken(IColorBase color1, IColorBase color2) {
        RGBColor rgb1 = color1.asRGB();
        RGBColor rgb2 = color2.asRGB();

        float red = Math.min(rgb1.red(), rgb2.red());
        float green = Math.min(rgb1.green(), rgb2.green());
        float blue = Math.min(rgb1.blue(), rgb2.blue());

        return new RGBColor(red, green, blue);
    }

    private static float clamp(float value) {
        return Math.max(0, Math.min(255, value));
    }
}
