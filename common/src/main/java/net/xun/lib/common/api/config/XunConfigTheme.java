package net.xun.lib.common.api.config;

import net.xun.lib.common.api.util.color.IColor;

public record XunConfigTheme(
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
        int scrollbarThumbHover,

        XunConfigLayout layout
) {
    public static final XunConfigTheme DEFAULT = new XunConfigTheme(
            // Panel
            IColor.rgb(28, 28, 28).toHex(),
            IColor.rgb(14, 14, 14).toHex(),
            IColor.rgb(58, 58, 58).toHex(),
            IColor.argb(120, 0, 0, 0).toHex(),

            // Sidebar
            IColor.rgb(18, 18, 18).toHex(),
            IColor.rgb(42, 42, 42).toHex(),
            IColor.rgb(32, 32, 32).toHex(),

            // Cards
            IColor.rgb(22, 22, 22).toHex(),
            IColor.rgb(34, 34, 34).toHex(),
            IColor.rgb(14, 14, 14).toHex(),
            IColor.rgb(48, 48, 48).toHex(),

            // Accent
            IColor.rgb(255, 255, 255).toHex(),
            IColor.rgb(190, 190, 190).toHex(),

            // Text
            IColor.rgb(255, 255, 255).toHex(),
            IColor.rgb(170, 170, 170).toHex(),
            IColor.rgb(100, 100, 100).toHex(),

            // Toggle
            IColor.rgb(70, 70, 70).toHex(),
            IColor.rgb(255, 255, 255).toHex(),
            IColor.rgb(0, 0, 0).toHex(),

            // Scrollbar
            IColor.rgb(32, 32, 32).toHex(),
            IColor.rgb(100, 100, 100).toHex(),
            IColor.rgb(150, 150, 150).toHex(),

            XunConfigLayout.DEFAULT
    );

    public static final XunConfigTheme LIGHT = new XunConfigTheme(
            IColor.argb(248, 255, 255, 255).toHex(),
            IColor.argb(240, 228, 231, 236).toHex(),
            IColor.argb(48, 0, 0, 0).toHex(),
            IColor.argb(48, 0, 0, 0).toHex(),

            IColor.argb(64, 255, 255, 255).toHex(),
            IColor.argb(255, 109, 74, 255).toHex(),
            IColor.argb(24, 0, 0, 0).toHex(),

            IColor.argb(96, 255, 255, 255).toHex(),
            IColor.argb(160, 255, 255, 255).toHex(),
            IColor.argb(48, 0, 0, 0).toHex(),
            IColor.argb(37, 0, 0, 0).toHex(),

            IColor.argb(255, 109, 74, 255).toHex(),
            IColor.argb(255, 90, 63, 224).toHex(),

            IColor.argb(255, 17, 19, 24).toHex(),
            IColor.argb(255, 95, 102, 114).toHex(),
            IColor.argb(255, 154, 161, 172).toHex(),

            IColor.argb(48, 0, 0, 0).toHex(),
            IColor.argb(255, 109, 74, 255).toHex(),
            IColor.rgb(255, 255, 255).toHex(),

            IColor.argb(48, 0, 0, 0).toHex(),
            IColor.argb(255, 109, 74, 255).toHex(),
            IColor.rgb(255, 255, 255).toHex(),

            XunConfigLayout.DEFAULT
    );

    public static final XunConfigTheme VANILLA = new XunConfigTheme(
            IColor.rgb(198, 198, 198).toHex(),              // panelTop
            IColor.rgb(176, 176, 176).toHex(),              // panelBottom
            IColor.rgb(255, 255, 255).toHex(),              // panelBorder
            IColor.rgb(85, 85, 85).toHex(),                 // panelShadow

            IColor.rgb(139, 139, 139).toHex(),              // sidebarBackground
            IColor.rgb(212, 212, 212).toHex(),              // sidebarSelected
            IColor.argb(64, 255, 255, 255).toHex(),      // sidebarHover

            IColor.rgb(198, 198, 198).toHex(),              // cardBackground
            IColor.rgb(212, 212, 212).toHex(),              // cardHover
            IColor.rgb(139, 139, 139).toHex(),              // cardDisabled
            IColor.rgb(85, 85, 85).toHex(),                 // cardBorder

            IColor.rgb(60, 133, 39).toHex(),                // accent           #3C8527
            IColor.rgb(42, 100, 28).toHex(),                // accentSecondary  #2A641C

            IColor.rgb(212, 212, 212).toHex(),              // text             #404040
            IColor.rgb(112, 112, 112).toHex(),              // textMuted        #707070
            IColor.rgb(160, 160, 160).toHex(),              // textDisabled     #A0A0A0

            IColor.rgb(139, 139, 139).toHex(),              // toggleOff
            IColor.rgb(60, 133, 39).toHex(),                // toggleOn
            IColor.rgb(255, 255, 255).toHex(),              // toggleKnob

            IColor.argb(64, 0, 0, 0).toHex(),
            IColor.rgb(198, 198, 198).toHex(),              // scrollbarThumb
            IColor.rgb(255, 255, 255).toHex(),               // scrollbarThumbHover

            XunConfigLayout.DEFAULT
    );
}