package net.xun.lib.common.api.client.gui.config.layout;

public record PanelLayout(
        int titleLeft,
        int titleTop,
        int descriptionLeft,
        int descriptionTop
) {
    public static final PanelLayout DEFAULT =
            new PanelLayout(
                    16,
                    12,
                    16,
                    26
            );
}