package net.xun.lib.common.api.client.gui.config.layout;

public record TypeSelectorLayout(
        int height,
        int topOffset
) {
    public static final TypeSelectorLayout DEFAULT =
            new TypeSelectorLayout(
                    22,
                    8
            );
}