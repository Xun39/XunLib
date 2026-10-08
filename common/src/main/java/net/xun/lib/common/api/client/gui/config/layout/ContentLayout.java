package net.xun.lib.common.api.client.gui.config.layout;

/**
 * Widths of the major content regions.
 */
public record ContentLayout(
        int sidebarWidth,
        int sidebarWidthCompact,
        int infoWidth
) {
    public static final ContentLayout DEFAULT =
            new ContentLayout(
                    122,
                    108,
                    109
            );
}