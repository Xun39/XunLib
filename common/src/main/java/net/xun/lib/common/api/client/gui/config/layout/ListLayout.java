package net.xun.lib.common.api.client.gui.config.layout;

/**
 * List-specific geometry.
 */
public record ListLayout(
        int rowLeftInset,
        int rowRightInset,
        int scrollbarInset
) {
    public static final ListLayout DEFAULT =
            new ListLayout(
                    4,
                    10,
                    2
            );

    public int rowWidth(int width) {
        return Math.max(0, width - rowLeftInset - rowRightInset);
    }
}