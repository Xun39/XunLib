package net.xun.lib.common.api.client.gui.config.layout;

public record ToggleLayout(
        int width,
        int height,
        int knobInset,
        int highlightHeight,
        int minKnobSize,
        int knobShadowOffset
) {
    public static final ToggleLayout DEFAULT =
            new ToggleLayout(
                    34,
                    16,
                    2,
                    1,
                    2,
                    1
            );
}