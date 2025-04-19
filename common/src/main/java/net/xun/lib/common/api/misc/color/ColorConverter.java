package net.xun.lib.common.api.misc.color;

public class ColorConverter {

    public static HSLColor RGBtoHSL(RGBColor rgb) {
        float rf = rgb.red() / 255f;
        float gf = rgb.green() / 255f;
        float bf = rgb.blue() / 255f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float h = 0, s, l = (max + min) / 2f;

        if (delta != 0) {
            s = delta / (1 - Math.abs(2 * l - 1));

            if (max == rf) {
                h = (gf - bf) / delta % 6;
            } else if (max == gf) {
                h = (bf - rf) / delta + 2;
            } else {
                h = (rf - gf) / delta + 4;
            }

            h *= 60;
            if (h < 0) h += 360;
        } else {
            s = 0;
        }

        return new HSLColor(h, s, l);
    }

    public static RGBColor HSLtoRGB(HSLColor hsl) {
        float hue = hsl.hue() / 360f;
        float saturation = hsl.saturation();
        float lightness = hsl.lightness();

        if (saturation == 0) {
            int gray = Math.round(lightness * 255);
            return new RGBColor(gray, gray, gray);
        }

        float chroma = (1 - Math.abs(2 * lightness - 1)) * saturation;
        float hPrime = hue * 6; // 0 ≤ hPrime < 6
        float x = chroma * (1 - Math.abs(hPrime % 2 - 1));

        float r1, g1, b1;
        int sector = (int) hPrime;
        switch (sector) {
            case 0: r1 = chroma; g1 = x;      b1 = 0;      break;
            case 1: r1 = x;      g1 = chroma; b1 = 0;      break;
            case 2: r1 = 0;      g1 = chroma; b1 = x;      break;
            case 3: r1 = 0;      g1 = x;      b1 = chroma; break;
            case 4: r1 = x;      g1 = 0;      b1 = chroma; break;
            default:r1 = chroma; g1 = 0;      b1 = x;      break;
        }

        float m = lightness - (chroma / 2);

        int red = Math.round((r1 + m) * 255);
        int green = Math.round((g1 + m) * 255);
        int blue = Math.round((b1 + m) * 255);

        return new RGBColor(red, green, blue);
    }

    public static HSVColor RGBtoHSV(RGBColor rgb) {
        float r = rgb.red() / 255f;
        float g = rgb.green() / 255f;
        float b = rgb.blue() / 255f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h = 0, s, v = max;

        if (delta != 0) {
            s = delta / max;

            if (max == r) {
                h = (g - b) / delta % 6;
            } else if (max == g) {
                h = (b - r) / delta + 2;
            } else {
                h = (r - g) / delta + 4;
            }

            h *= 60;
            if (h < 0) h += 360;
        } else {
            s = 0;
        }

        return new HSVColor(h, s, v);
    }

    public static RGBColor HSVtoRGB(HSVColor hsv) {
        float h = hsv.hue() % 360;
        float s = hsv.saturation();
        float v = hsv.value();

        if (s == 0) {
            int gray = Math.round(v * 255);
            return new RGBColor(gray, gray, gray);
        }

        float chroma = v * s;
        float hPrime = h / 60f;
        int sector = (int) hPrime;
        float x = chroma * (1 - Math.abs(hPrime % 2 - 1));

        float r1, g1, b1;
        switch (sector) {
            case 0 -> { r1 = chroma; g1 = x;      b1 = 0; }
            case 1 -> { r1 = x;      g1 = chroma; b1 = 0; }
            case 2 -> { r1 = 0;      g1 = chroma; b1 = x; }
            case 3 -> { r1 = 0;      g1 = x;      b1 = chroma; }
            case 4 -> { r1 = x;      g1 = 0;      b1 = chroma; }
            default-> { r1 = chroma; g1 = 0;      b1 = x; }
        }

        float m = v - chroma;
        return new RGBColor(
                Math.round((r1 + m) * 255),
                Math.round((g1 + m) * 255),
                Math.round((b1 + m) * 255)
        );
    }
}
