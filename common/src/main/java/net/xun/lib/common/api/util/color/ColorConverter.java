package net.xun.lib.common.api.util.color;

public final class ColorConverter {
    private ColorConverter() {
    }

    public static HSVColor rgb2Hsv(ARGBColor rgb) {
        return rgb2Hsv(rgb.red(), rgb.green(), rgb.blue());
    }

    public static HSVColor rgb2Hsv(int red, int green, int blue) {
        float r = clamp255(red) / 255f;
        float g = clamp255(green) / 255f;
        float b = clamp255(blue) / 255f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h = 0f;
        float s = 0f;
        float v = max;

        if (delta != 0f) {
            s = delta / max;

            if (max == r) {
                h = 60f * (((g - b) / delta) % 6f);
            }
            else if (max == g) {
                h = 60f * (((b - r) / delta) + 2f);
            }
            else {
                h = 60f * (((r - g) / delta) + 4f);
            }

            h = normalizeHue(h);
        }

        return new HSVColor(h, s, v);
    }

    public static ARGBColor hsv2Rgb(HSVColor hsv) {
        return hsv2Rgb(hsv, 255);
    }

    public static ARGBColor hsv2Rgb(HSVColor hsv, int alpha) {
        float h = normalizeHue(hsv.hue());
        float s = clamp01(hsv.saturation());
        float v = clamp01(hsv.value());
        int a = clamp255(alpha);

        if (s == 0f) {
            int gray = Math.round(v * 255f);
            return new ARGBColor(a, gray, gray, gray); // (a, r, g, b)
        }

        float chroma = v * s;
        float hPrime = h / 60f;
        int sector = (int) hPrime; // hPrime is in [0, 6)
        float x = chroma * (1f - Math.abs((hPrime % 2f) - 1f));

        float r1, g1, b1;
        switch (sector) {
            case 0 -> {
                r1 = chroma;
                g1 = x;
                b1 = 0f;
            }
            case 1 -> {
                r1 = x;
                g1 = chroma;
                b1 = 0f;
            }
            case 2 -> {
                r1 = 0f;
                g1 = chroma;
                b1 = x;
            }
            case 3 -> {
                r1 = 0f;
                g1 = x;
                b1 = chroma;
            }
            case 4 -> {
                r1 = x;
                g1 = 0f;
                b1 = chroma;
            }
            default -> {
                r1 = chroma;
                g1 = 0f;
                b1 = x;
            }
        }

        float m = v - chroma;
        int r = clamp255(Math.round((r1 + m) * 255f));
        int g = clamp255(Math.round((g1 + m) * 255f));
        int b = clamp255(Math.round((b1 + m) * 255f));

        return new ARGBColor(a, r, g, b); // (a, r, g, b)
    }

    public static ARGBColor hsv2Argb(HSVColor hsv, int alpha) {
        return hsv2Rgb(hsv, alpha);
    }

    public static ARGBColor hsv2Argb(HSVColor hsv, ARGBColor alphaSource) {
        return hsv2Rgb(hsv, alphaSource.alpha());
    }

    private static float normalizeHue(float h) {
        h %= 360f;
        if (h < 0f) {
            h += 360f;
        }
        return h;
    }

    private static float clamp01(float value) {
        if (value < 0f) return 0f;
        if (value > 1f) return 1f;
        return value;
    }

    private static int clamp255(int value) {
        if (value < 0) return 0;
        if (value > 255) return 255;
        return value;
    }
}