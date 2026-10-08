package net.xun.lib.common.api.client.gui.config;

import net.xun.lib.common.api.client.gui.config.layout.*;
import net.xun.lib.common.api.util.Area;

public record XunConfigLayout(
        ScreenLayout screen,
        ContentLayout content,
        ListLayout list,
        DecorationLayout decoration,
        PanelLayout panel,
        CardLayout card,
        CategoryLayout category,
        ControlLayout control,
        ChevronLayout chevron,
        ToggleLayout toggle,
        SliderLayout slider,
        DropdownLayout dropdown,
        ScrollbarLayout scrollbar,
        ButtonLayout button,
        InformationLayout information,
        TypeSelectorLayout typeSelector
) {
    public static final XunConfigLayout DEFAULT =
            new XunConfigLayout(
                    ScreenLayout.DEFAULT,
                    ContentLayout.DEFAULT,
                    ListLayout.DEFAULT,
                    DecorationLayout.DEFAULT,
                    PanelLayout.DEFAULT,
                    CardLayout.DEFAULT,
                    CategoryLayout.DEFAULT,
                    ControlLayout.DEFAULT,
                    ChevronLayout.DEFAULT,
                    ToggleLayout.DEFAULT,
                    SliderLayout.DEFAULT,
                    DropdownLayout.DEFAULT,
                    ScrollbarLayout.DEFAULT,
                    ButtonLayout.DEFAULT,
                    InformationLayout.DEFAULT,
                    TypeSelectorLayout.DEFAULT
    );

    public record Layout(
            Area panel,
            Area contentWidgets,
            Area sidebar,
            Area options,
            Area info,
            Area typeSelector,
            Area footerButtons,
            Area emptyStateButton,
            boolean compact) {}

    public static Layout create(int screenWidth, int screenHeight, XunConfigLayout layout) {
        ScreenLayout s = layout.screen();
        ContentLayout c = layout.content();
        TypeSelectorLayout selector = layout.typeSelector();

        // Outer panel
        int panelWidth = Math.max(0, screenWidth - s.margin() * 2);
        int panelHeight = Math.max(0, screenHeight - s.margin() * 2);

        Area panel = Area.of(s.margin(), s.margin(), panelWidth, panelHeight);

        int headerHeight = Math.min(s.headerHeight(), panel.height());
        int footerHeight = Math.min(s.footerHeight(), panel.height());

        int headerBottom = panel.y() + headerHeight;
        int footerY = Math.max(panel.y(), panel.y2() - footerHeight);

        int contentTop = Math.min(panel.y2(), headerBottom);
        int contentBottom = Math.max(contentTop, footerY - s.contentFooterGap());
        int contentHeight = Math.max(0, contentBottom - contentTop);

        boolean compact = panel.width() < s.compactThreshold();

        // Horizontal content bounds
        int horizontalPadding = Math.max(0, s.panelPadding());
        int contentX = panel.x() + horizontalPadding;
        int contentWidth = Math.max(0, panel.width() - horizontalPadding * 2);

        // Vertical content bounds
        int verticalPadding = Math.min(Math.max(0, s.contentPadding()), contentHeight / 2);
        int contentWidgetsY = contentTop + verticalPadding;
        int contentWidgetsHeight = Math.max(0, contentHeight - verticalPadding * 2);

        Area contentWidgets = Area.of(contentX, contentWidgetsY, contentWidth, contentWidgetsHeight);

        // Sidebar
        int requestedSidebarWidth = compact ? c.sidebarWidthCompact() : c.sidebarWidth();

        int sidebarWidth = Math.min(Math.max(0, requestedSidebarWidth), contentWidgets.width());
        Area sidebar = Area.of(contentWidgets.x(), contentWidgets.y(), sidebarWidth, contentWidgets.height());

        Area info;
        if (compact) {
            info = Area.of(contentWidgets.x2(), contentWidgets.y(), 0, 0);
        }
        else {
            int infoWidth = Math.min(Math.max(0, c.infoWidth()), contentWidgets.width());
            info = Area.of(Math.max(contentWidgets.x(), contentWidgets.x2() - infoWidth), contentWidgets.y(), infoWidth, contentWidgets.height());
        }

        // Option List
        int optionsLeft = sidebar.x2() + s.gap();
        int optionsRight = compact ? contentWidgets.x2() : info.x() - s.gap();
        int optionsWidth = Math.max(0, optionsRight - optionsLeft);

        Area options = Area.of(optionsLeft, contentWidgets.y(), optionsWidth, contentWidgets.height());

        // Config type selector
        Area typeSelector = Area.of(
                options.x(),
                panel.y() + selector.topOffset(),
                options.width(),
                selector.height()
        );

        Area footerButtons = Area.of(
                panel.x(),
                footerY,
                panel.width(),
                footerHeight
        );

        // Fallback button
        Area emptyStateButton = Area.of(
                screenWidth / 2 - layout.button().emptyStateWidth() / 2,
                screenHeight / 2 + layout.button().emptyStateOffsetY(),
                layout.button().emptyStateWidth(),
                layout.button().emptyStateHeight()
        );

        return new Layout(panel, contentWidgets, sidebar, options, info, typeSelector, footerButtons, emptyStateButton, compact);
    }
}