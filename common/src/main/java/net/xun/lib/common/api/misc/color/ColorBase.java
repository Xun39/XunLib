package net.xun.lib.common.api.misc.color;

public interface ColorBase {

    String toString();
    boolean equals(Object obj);
    int hashCode();

    RGBColor asRGB();
    String asHex();
    default HSLColor asHSL() {
        return this instanceof HSLColor color ? color : ColorConverter.RGBtoHSL(this.asRGB());
    }
    default HSVColor asHSV() {
        return this instanceof HSVColor color ? color : ColorConverter.RGBtoHSV(this.asRGB());
    }
}
