package net.xun.lib.common.api.client.gui.config.layout;

public record DropdownLayout(
        int width,
        int height,
        int itemHeight,
        int popupGap,
        int textPadding,
        int textOffsetY,
        int itemTextOffsetY,
        int arrowSize,
        int chevronThickness
) {
    public static final DropdownLayout DEFAULT =
            new DropdownLayout(
                    80,
                    20,
                    16,
                    1,
                    6,
                    1,
                    1,
                    4,
                    1
            );
}