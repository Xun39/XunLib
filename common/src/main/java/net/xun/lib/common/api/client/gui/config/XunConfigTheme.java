package net.xun.lib.common.api.client.gui.config;

import net.xun.lib.common.api.util.color.IColor;

public record XunConfigTheme(
        PanelColors panel, SidebarColors sidebar, CardColors card, AccentColors accent, TextColors text, ToggleColors toggle, ScrollbarColors scrollbar,
        OverlayColors overlay, XunConfigLayout layout
) {
    public record PanelColors(int top, int bottom, int border, int shadow) {}

    public record SidebarColors(int background, int selected, int hover) {}

    public record CardColors(int background, int hover, int disabled, int border, int disabledBorder) {}

    public record AccentColors(int primary, int secondary) {}

    public record TextColors(int primary, int muted, int disabled) {}

    public record ToggleColors(int off, int on, int knob, int highlight, int knobShadow) {}

    public record ScrollbarColors(int track, int thumb, int thumbHover) {}

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
}