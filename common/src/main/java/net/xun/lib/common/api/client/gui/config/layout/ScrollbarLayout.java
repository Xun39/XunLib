package net.xun.lib.common.api.client.gui.config.layout;

public record ScrollbarLayout(
        int width,
        int minThumbHeight
) {
    public static final ScrollbarLayout DEFAULT =
            new ScrollbarLayout(
                    4,
                    12
            );
}