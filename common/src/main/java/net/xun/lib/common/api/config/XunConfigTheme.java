package net.xun.lib.common.api.config;

import net.xun.lib.common.api.util.color.ARGBColor;
import net.xun.lib.common.api.util.color.RGBColor;

public record XunConfigTheme(
        int screenBackground,

        int panelTop,
        int panelBottom,
        int panelBorder,
        int panelShadow,

        int sidebarBackground,
        int sidebarSelected,
        int sidebarHover,

        int cardBackground,
        int cardHover,
        int cardDisabled,
        int cardBorder,

        int accent,
        int accentSecondary,

        int text,
        int textMuted,
        int textDisabled,

        int toggleOff,
        int toggleOn,
        int toggleKnob,

        int scrollbarTrack,
        int scrollbarThumb,
        int scrollbarThumbHover
) {
    public static final XunConfigTheme DEFAULT = new XunConfigTheme(
            new ARGBColor(255, 11, 13, 20).toHex(),

            new ARGBColor(224, 24, 27, 39).toHex(),
            new ARGBColor(224, 15, 17, 26).toHex(),
            new ARGBColor(53, 255, 255, 255).toHex(),
            new ARGBColor(80, 0, 0, 0).toHex(),

            new ARGBColor(80, 17, 21, 32).toHex(),
            new ARGBColor(255, 139, 92, 246).toHex(),
            new ARGBColor(48, 44, 48, 66).toHex(),

            new ARGBColor(64, 30, 32, 46).toHex(),
            new ARGBColor(96, 38, 41, 59).toHex(),
            new ARGBColor(26, 17, 19, 31).toHex(),
            new ARGBColor(48, 255, 255, 255).toHex(),

            new ARGBColor(255, 139, 92, 246).toHex(),
            new ARGBColor(255, 109, 92, 231).toHex(),

            new RGBColor(255, 255, 255).toHex(),
            new ARGBColor(255, 156, 163, 175).toHex(),
            new ARGBColor(255, 75, 85, 99).toHex(),

            new ARGBColor(96, 255, 255, 255).toHex(),
            new ARGBColor(255, 139, 92, 246).toHex(),
            new RGBColor(255, 255, 255).toHex(),

            new ARGBColor(96, 255, 255, 255).toHex(),
            new ARGBColor(255, 139, 92, 246).toHex(),
            new RGBColor(255, 255, 255).toHex()
    );

    public static final XunConfigTheme LIGHT = new XunConfigTheme(
            new ARGBColor(255, 238, 241, 245).toHex(),

            new ARGBColor(248, 255, 255, 255).toHex(),
            new ARGBColor(240, 228, 231, 236).toHex(),
            new ARGBColor(48, 0, 0, 0).toHex(),
            new ARGBColor(48, 0, 0, 0).toHex(),

            new ARGBColor(64, 255, 255, 255).toHex(),
            new ARGBColor(255, 109, 74, 255).toHex(),
            new ARGBColor(24, 0, 0, 0).toHex(),

            new ARGBColor(96, 255, 255, 255).toHex(),
            new ARGBColor(160, 255, 255, 255).toHex(),
            new ARGBColor(48, 0, 0, 0).toHex(),
            new ARGBColor(37, 0, 0, 0).toHex(),

            new ARGBColor(255, 109, 74, 255).toHex(),
            new ARGBColor(255, 90, 63, 224).toHex(),

            new ARGBColor(255, 17, 19, 24).toHex(),
            new ARGBColor(255, 95, 102, 114).toHex(),
            new ARGBColor(255, 154, 161, 172).toHex(),

            new ARGBColor(48, 0, 0, 0).toHex(),
            new ARGBColor(255, 109, 74, 255).toHex(),
            new RGBColor(255, 255, 255).toHex(),

            new ARGBColor(48, 0, 0, 0).toHex(),
            new ARGBColor(255, 109, 74, 255).toHex(),
            new RGBColor(255, 255, 255).toHex()
    );
}