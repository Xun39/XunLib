package net.xun.lib.common.api.client.gui.config.layout;

/**
 * Shared chevron geometry.
 */
public record ChevronLayout(
        int size,
        int gap,
        int hitPadding
) {
    public static final ChevronLayout DEFAULT =
            new ChevronLayout(
                    8,
                    7,
                    4
            );

    public int slotWidth() {
        return size + gap;
    }

    public int hitSize() {
        return size + hitPadding * 2;
    }
}