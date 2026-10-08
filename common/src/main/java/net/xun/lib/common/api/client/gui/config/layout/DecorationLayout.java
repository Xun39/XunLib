package net.xun.lib.common.api.client.gui.config.layout;
/**
 * Shared visual decoration geometry.
 */
public record DecorationLayout(
        int borderWidth,
        int shadowDx,
        int shadowDy,
        int accentStripWidth,
        int accentStripInset
) {
    public static final DecorationLayout DEFAULT =
            new DecorationLayout(
                    1,
                    1,
                    2,
                    2,
                    4
            );
}
