package net.xun.lib.common.api.client.gui.config.layout;

/**
 * Option-card geometry.
 * <p>
 * Actual child placement is handled by GridLayout.
 */
public record CardLayout(
        int rowHeight,
        int height,
        int entryPaddingY,

        int paddingLeft,
        int paddingRight,
        int controlGap,

        int titleTop,
        int titleHeight,

        int descriptionTop,
        int descriptionHeight
) {
    public static final CardLayout DEFAULT =
            new CardLayout(
                    44,
                    40,
                    2,

                    11,
                    10,
                    8,

                    6,
                    13,

                    22,
                    12
            );
}