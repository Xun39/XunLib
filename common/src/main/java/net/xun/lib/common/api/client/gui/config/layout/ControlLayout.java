package net.xun.lib.common.api.client.gui.config.layout;

/**
 * Generic control-column geometry.
 */
public record ControlLayout(
        int columnWidth,
        int defaultWidth,
        int defaultHeight
) {
    public static final ControlLayout DEFAULT =
            new ControlLayout(
                    110,
                    60,
                    20
            );
}