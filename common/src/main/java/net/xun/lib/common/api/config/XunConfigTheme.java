package net.xun.lib.common.api.config;

import net.xun.lib.common.api.util.color.IColor;

public record XunConfigTheme(
        PanelColors panel, SidebarColors sidebar, CardColors card, AccentColors accent, TextColors text, ToggleColors toggle, ScrollbarColors scrollbar,
        OverlayColors overlay, XunConfigLayout layout
) {
    /**
     * Main configuration panel colors.
     */
    public record PanelColors(int top, int bottom, int border, int shadow) {}

    /**
     * Category/sidebar colors.
     */
    public record SidebarColors(int background, int selected, int hover) {}

    /**
     * Option-card colors.
     */
    public record CardColors(int background, int hover, int disabled, int border, int disabledBorder) {}

    /**
     * Accent colors used for active/selected elements.
     */
    public record AccentColors(int primary, int secondary) {}

    /**
     * Text colors.
     */
    public record TextColors(int primary, int muted, int disabled) {}

    /**
     * Toggle-specific colors.
     */
    public record ToggleColors(int off, int on, int knob, int highlight, int knobShadow) {}

    /**
     * Scrollbar colors.
     */
    public record ScrollbarColors(int track, int thumb, int thumbHover) {}

    /**
     * Translucent overlays and small decorative effects that don't
     * belong naturally to a solid color group.
     */
    public record OverlayColors(int selection, int disabledBorder, int buttonHighlight) {}

    public static final XunConfigTheme DEFAULT = new XunConfigTheme(
            new PanelColors(
                    IColor.rgb(28, 28, 28).toHex(), IColor.rgb(14, 14, 14).toHex(),
                    IColor.rgb(58, 58, 58).toHex(), IColor.argb(120, 0, 0, 0).toHex()
            ),
            new SidebarColors(IColor.rgb(18, 18, 18).toHex(), IColor.rgb(42, 42, 42).toHex(), IColor.rgb(32, 32, 32).toHex()),
            new CardColors(
                    IColor.rgb(22, 22, 22).toHex(), IColor.rgb(34, 34, 34).toHex(), IColor.rgb(14, 14, 14).toHex(),
                    IColor.rgb(48, 48, 48).toHex(), IColor.argb(16, 255, 255, 255).toHex()
            ),
            new AccentColors(IColor.rgb(255, 255, 255).toHex(), IColor.rgb(190, 190, 190).toHex()),
            new TextColors(IColor.rgb(255, 255, 255).toHex(), IColor.rgb(170, 170, 170).toHex(), IColor.rgb(100, 100, 100).toHex()),
            new ToggleColors(
                    IColor.rgb(70, 70, 70).toHex(), IColor.rgb(255, 255, 255).toHex(), IColor.rgb(0, 0, 0).toHex(),
                    IColor.argb(64, 255, 255, 255).toHex(), IColor.argb(80, 0, 0, 0).toHex()
            ),
            new ScrollbarColors(IColor.rgb(32, 32, 32).toHex(), IColor.rgb(100, 100, 100).toHex(), IColor.rgb(150, 150, 150).toHex()),
            new OverlayColors(
                    IColor.argb(32, 255, 255, 255).toHex(), IColor.argb(16, 255, 255, 255).toHex(),
                    IColor.argb(69, 255, 255, 255).toHex()
            ),
            XunConfigLayout.DEFAULT
    );

    public static final XunConfigTheme LIGHT = new XunConfigTheme(
            new PanelColors(IColor.argb(248, 255, 255, 255).toHex(), IColor.argb(240, 228, 231, 236).toHex(), IColor.argb(48, 0, 0, 0).toHex(), IColor.argb(48, 0, 0, 0).toHex()),
            new SidebarColors(IColor.argb(64, 255, 255, 255).toHex(), IColor.argb(255, 109, 74, 255).toHex(), IColor.argb(24, 0, 0, 0).toHex()),
            new CardColors(
                    IColor.argb(96, 255, 255, 255).toHex(), IColor.argb(160, 255, 255, 255).toHex(),
                    IColor.argb(48, 0, 0, 0).toHex(), IColor.argb(37, 0, 0, 0).toHex(), IColor.argb(32, 0, 0, 0).toHex()
            ),
            new AccentColors(IColor.argb(255, 109, 74, 255).toHex(), IColor.argb(255, 90, 63, 224).toHex()),
            new TextColors(IColor.argb(255, 17, 19, 24).toHex(), IColor.argb(255, 95, 102, 114).toHex(), IColor.argb(255, 154, 161, 172).toHex()),
            new ToggleColors(
                    IColor.argb(48, 0, 0, 0).toHex(), IColor.argb(255, 109, 74, 255).toHex(), IColor.rgb(255, 255, 255).toHex(),
                    IColor.argb(64, 255, 255, 255).toHex(), IColor.argb(64, 0, 0, 0).toHex()
            ),
            new ScrollbarColors(IColor.argb(48, 0, 0, 0).toHex(), IColor.argb(255, 109, 74, 255).toHex(), IColor.rgb(255, 255, 255).toHex()),
            new OverlayColors(IColor.argb(32, 0, 0, 0).toHex(), IColor.argb(32, 0, 0, 0).toHex(), IColor.argb(69, 255, 255, 255).toHex()),
            XunConfigLayout.DEFAULT
    );

    public static final XunConfigTheme VANILLA = new XunConfigTheme(
            new PanelColors(
                    IColor.rgb(198, 198, 198).toHex(), IColor.rgb(176, 176, 176).toHex(),
                    IColor.rgb(255, 255, 255).toHex(), IColor.rgb(85, 85, 85).toHex()
            ),
            new SidebarColors(
                    IColor.rgb(139, 139, 139).toHex(), IColor.rgb(212, 212, 212).toHex(), IColor.argb(64, 255, 255, 255).toHex()
            ),
            new CardColors(
                    IColor.rgb(198, 198, 198).toHex(), IColor.rgb(212, 212, 212).toHex(),
                    IColor.rgb(139, 139, 139).toHex(), IColor.rgb(85, 85, 85).toHex(), IColor.argb(32, 0, 0, 0).toHex()
            ),
            new AccentColors(IColor.rgb(60, 133, 39).toHex(), IColor.rgb(42, 100, 28).toHex()),
            new TextColors(
                    IColor.rgb(64, 64, 64).toHex(), IColor.rgb(112, 112, 112).toHex(), IColor.rgb(160, 160, 160).toHex()
            ),
            new ToggleColors(
                    IColor.rgb(139, 139, 139).toHex(), IColor.rgb(60, 133, 39).toHex(),
                    IColor.rgb(255, 255, 255).toHex(), IColor.argb(48, 255, 255, 255).toHex(), IColor.argb(64, 0, 0, 0).toHex()
            ),
            new ScrollbarColors(
                    IColor.argb(64, 0, 0, 0).toHex(), IColor.rgb(198, 198, 198).toHex(),
                    IColor.rgb(255, 255, 255).toHex()
            ),
            new OverlayColors(
                    IColor.argb(32, 0, 0, 0).toHex(), IColor.argb(32, 0, 0, 0).toHex(), IColor.argb(69, 255, 255, 255).toHex()
            ),
            XunConfigLayout.DEFAULT
    );
}