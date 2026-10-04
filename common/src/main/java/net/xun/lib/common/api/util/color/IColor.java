package net.xun.lib.common.api.util.color;

public interface IColor {
    int toHex();

    static ARGBColor argb(int a, int r, int g, int b) {
        return new ARGBColor(a, r, g, b);
    }

    static ARGBColor rgb(int r, int g, int b) {
        return new ARGBColor(r, g, b);
    }
}
