package net.xun.lib.common.api.client.gui.config.layout;

public record SliderLayout(
        int width,
        int height,
        int handleMinWidth,
        int handlePadding,
        int valueTextOffsetY
) {
    public static final SliderLayout DEFAULT =
            new SliderLayout(
                    100,
                    20,
                    4,
                    1,
                    1
            );
}