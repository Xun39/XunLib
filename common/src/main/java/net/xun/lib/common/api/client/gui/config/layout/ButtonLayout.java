package net.xun.lib.common.api.client.gui.config.layout;

public record ButtonLayout(
        int width,
        int height,

        int textPadding,
        int textTop,
        int textBottom,
        int highlightHeight,

        int footerOffsetY,
        int footerRightInset,
        int footerSpacing,

        int emptyStateWidth,
        int emptyStateHeight,
        int emptyStateOffsetY
) {
    public static final ButtonLayout DEFAULT =
            new ButtonLayout(
                    76,
                    22,

                    8,
                    6,
                    5,
                    1,

                    5,
                    10,
                    6,

                    76,
                    22,
                    30
            );
}