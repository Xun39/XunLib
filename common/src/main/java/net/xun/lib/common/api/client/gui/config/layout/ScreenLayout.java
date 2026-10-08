package net.xun.lib.common.api.client.gui.config.layout;

/**
 * Screen-level geometry.
 * <p>
 * These values describe the overall configuration screen rather than
 * individual child widget positions.
 */
public record ScreenLayout(
        int margin,
        int headerHeight,
        int footerHeight,
        int compactThreshold,
        int contentFooterGap,
        int panelPadding,
        int contentPadding,
        int gap
) {
    public static final ScreenLayout DEFAULT =
            new ScreenLayout(
                    18,
                    48,
                    34,
                    760,
                    10,
                    10,
                    10,
                    12
            );
}