package net.xun.lib.common.api.client.gui.config.layout;

/**
 * Category-row geometry.
 * <p>
 * The tree hierarchy is represented by depth/indentation.
 * Arrow sizing itself belongs to Chevron.
 */
public record CategoryLayout(
        int rowHeight,
        int width,
        int height,
        int entryPaddingY,

        int textLeft,
        int textTop,
        int textRight,
        int textBottom,

        int childIndent,

        int selectedStripWidth,
        int selectedStripInset
) {
    public static final CategoryLayout DEFAULT =
            new CategoryLayout(
                    28,
                    100,
                    22,
                    3,

                    10,
                    6,
                    8,
                    5,

                    10,

                    2,
                    3
            );
}