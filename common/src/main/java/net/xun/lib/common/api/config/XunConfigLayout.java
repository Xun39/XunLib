package net.xun.lib.common.api.config;

public record XunConfigLayout(
        int margin,
        int headerHeight,
        int footerHeight,
        int sidebarWidth,
        int sidebarWidthCompact,
        int infoWidth,
        int gap,
        int compactThreshold
) {
    public static final XunConfigLayout DEFAULT = new XunConfigLayout(
            18,
            48,
            34,
            122,
            108,
            109,
            12,
            760
    );

    public record Layout(
            int panelX,
            int panelY,
            int panelWidth,
            int panelHeight,
            int contentY,
            int contentHeight,
            int footerY,
            int sidebarX,
            int sidebarWidth,
            int optionsX,
            int optionsWidth,
            int infoX,
            int infoWidth,
            boolean compact
    ) {
        public int contentWidgetY() {
            return contentY + 10;
        }

        public int contentWidgetHeight() {
            return contentHeight - 10;
        }
    }

    public static Layout create(int screenWidth, int screenHeight, XunConfigLayout layout) {
        int panelX = layout.margin;
        int panelY = layout.margin;
        int panelWidth = screenWidth - layout.margin * 2;
        int panelHeight = screenHeight - layout.margin * 2;

        int contentY = panelY + layout.headerHeight;
        int footerY = panelY + panelHeight - layout.footerHeight;
        int contentHeight = footerY - contentY - 10;

        boolean compact = panelWidth < layout.compactThreshold;

        int sidebarWidth = compact
                ? layout.sidebarWidthCompact
                : layout.sidebarWidth;

        int infoWidth = compact ? 0 : layout.infoWidth;

        int sidebarX = panelX + 10;
        int optionsX = sidebarX + sidebarWidth + layout.gap;
        int infoX = panelX + panelWidth - infoWidth - 10;

        int optionsRight = compact
                ? panelX + panelWidth - 10
                : infoX - layout.gap;

        int optionsWidth = Math.max(80, optionsRight - optionsX);

        return new Layout(
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                contentY,
                contentHeight,
                footerY,
                sidebarX,
                sidebarWidth,
                optionsX,
                optionsWidth,
                infoX,
                infoWidth,
                compact
        );
    }
}
