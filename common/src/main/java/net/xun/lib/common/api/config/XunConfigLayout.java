package net.xun.lib.common.api.config;

import net.xun.lib.common.api.util.Area;

public record XunConfigLayout(
        Screen screen, Content content, List list, Decoration decoration, Panel panel, Card card, Category category, Control control, Toggle toggle,
        Slider slider, Dropdown dropdown, Scrollbar scrollbar, Button button,
        Information information, TypeSelector typeSelector
) {
    public static final XunConfigLayout DEFAULT = new XunConfigLayout(
            Screen.DEFAULT, Content.DEFAULT, List.DEFAULT, Decoration.DEFAULT, Panel.DEFAULT, Card.DEFAULT,
            Category.DEFAULT, Control.DEFAULT, Toggle.DEFAULT, Slider.DEFAULT, Dropdown.DEFAULT, Scrollbar.DEFAULT,
            Button.DEFAULT, Information.DEFAULT, TypeSelector.DEFAULT
    );

    public record Screen(int margin, int headerHeight, int footerHeight, int compactThreshold, int contentFooterGap, int panelPadding, int contentPadding, int gap) {
        public static final Screen DEFAULT = new Screen(18, 48, 34, 760, 10, 10, 10, 12);
    }

    public record Content(int sidebarWidth, int sidebarWidthCompact, int infoWidth, int minOptionsWidth) {
        public static final Content DEFAULT = new Content(122, 108, 109, 80);
    }

    public record List(int rowLeftInset, int rowRightInset, int scrollbarInset) {
        public static final List DEFAULT = new List(4, 10, 2);

        public int rowWidth(int width) {
            return Math.max(0, width - rowLeftInset - rowRightInset);
        }
    }

    public record Decoration(int borderWidth, int shadowDx, int shadowDy, int accentStripWidth, int accentStripInset) {
        public static final Decoration DEFAULT = new Decoration(1, 1, 2, 2, 4);
    }

    public record Panel(int titleLeft, int titleTop, int descriptionLeft, int descriptionTop) {
        public static final Panel DEFAULT = new Panel(16, 12, 16, 26);
    }

    public record Card(int rowHeight, int height, int entryPaddingY,
                       int paddingLeft, int paddingRight, int controlGap,
                       int titleTop, int titleHeight,
                       int descriptionTop, int descriptionHeight
    ) {
        public static final Card DEFAULT = new Card(
                44, 40, 2,
                11, 10, 8,
                6, 13,
                22, 12
        );
    }

    public record Category(int rowHeight, int width, int height, int entryPaddingY,
                           int textLeft, int textTop, int textRight, int textBottom,
                           int selectedStripWidth, int selectedStripInset) {
        public static final Category DEFAULT = new Category(
                28, 100, 22, 3,
                10, 6, 8, 5,
                2, 3
        );
    }

    public record Control(int columnWidth, int defaultWidth, int defaultHeight, int valueRightPadding) {
        public static final Control DEFAULT = new Control(110, 60, 20, 0);
    }

    public record Toggle(int width, int height,
                         int knobInset, int highlightHeight, int minKnobSize, int knobShadowOffset
    ) {
        public static final Toggle DEFAULT = new Toggle(
                34, 16,
                2, 1, 2, 1
        );
    }

    public record Slider(int width, int height, int handleMinWidth, int handlePadding, int valueTextOffsetY) {
        public static final Slider DEFAULT = new Slider(100, 20, 4, 1, 1);
    }

    public record Dropdown(int width, int height,
                           int itemHeight, int popupGap,
                           int textPadding, int textOffsetY, int itemTextOffsetY,
                           int chevronSize, int chevronThickness
    ) {
        public static final Dropdown DEFAULT = new Dropdown(
                80, 20,
                16, 1,
                6, 1, 1,
                4, 1
        );
    }

    public record Scrollbar(int width, int minThumbHeight) {
        public static final Scrollbar DEFAULT = new Scrollbar(4, 12);
    }

    public record Button(int width, int height,
                         int textPadding, int textTop, int textBottom, int highlightHeight,
                         int footerOffsetY, int footerRightInset,
                         int emptyStateWidth, int emptyStateHeight, int emptyStateOffsetY
    ) {
        public static final Button DEFAULT = new Button(
                76, 22,
                8, 6, 5, 1,
                5, 10,
                76, 22, 30
        );
    }

    public record Information(
            int paddingLeft, int paddingRight,
            int titleTop, int descriptionTop,
            int typeSeparatorTop, int typeSeparatorHeight,
            int typeLabelTop, int typeValueTop
    ) {
        public static final Information DEFAULT = new Information(
                10, 10,
                12, 34,
                64, 1,
                75, 89
        );
    }

    public record TypeSelector(int height, int topOffset) {
        public static final TypeSelector DEFAULT = new TypeSelector(22, 8);
    }

    public record Layout(
            Area panel, Area header, Area content, Area contentWidgets, Area sidebar, Area options, Area info, Area footer, Area typeSelector, Area footerButton,
            Area emptyStateButton, boolean compact
    ) {
        public int contentWidgetY() {
            return contentWidgets.y();
        }

        public int contentWidgetHeight() {
            return contentWidgets.height();
        }

        public int panelX() {
            return panel.x();
        }

        public int panelY() {
            return panel.y();
        }

        public int panelWidth() {
            return panel.width();
        }

        public int panelHeight() {
            return panel.height();
        }

        public int footerY() {
            return footer.y();
        }

        public int sidebarX() {
            return sidebar.x();
        }

        public int sidebarWidth() {
            return sidebar.width();
        }

        public int optionsX() {
            return options.x();
        }

        public int optionsWidth() {
            return options.width();
        }

        public int infoX() {
            return info.x();
        }

        public int infoWidth() {
            return info.width();
        }
    }

    public static Layout create(int screenWidth, int screenHeight, XunConfigLayout layout) {
        Screen s = layout.screen();
        Content c = layout.content();
        TypeSelector selector = layout.typeSelector();
        Button button = layout.button();

        Area panel = Area.of(s.margin(), s.margin(), Math.max(0, screenWidth - s.margin() * 2), Math.max(0, screenHeight - s.margin() * 2));
        Area header = Area.of(panel.x(), panel.y(), panel.width(), Math.min(s.headerHeight(), panel.height()));
        Area footer = Area.of(panel.x(), Math.max(panel.y(), panel.y2() - s.footerHeight()), panel.width(), Math.min(s.footerHeight(), panel.height()));
        Area content = Area.fromCorners(panel.x(), header.y2(), panel.x2(), Math.max(header.y2(), footer.y() - s.contentFooterGap()));
        boolean compact = panel.width() < s.compactThreshold();

        int sidebarWidth = compact ? c.sidebarWidthCompact() : c.sidebarWidth();

        Area sidebar = Area.of(content.x() + s.panelPadding(), content.y(), Math.max(0, sidebarWidth), content.height());
        Area info = compact ? Area.of(content.x2() - s.panelPadding(), content.y(), 0, 0) : Area.of(
                content.x2() - s.panelPadding() - c.infoWidth(), content.y(), Math.max(0, c.infoWidth()), content.height());

        int optionsLeft = sidebar.x2() + s.gap();
        int optionsRight = compact ? content.x2() - s.panelPadding() : info.x() - s.gap();
        int optionsWidth = Math.max(0, optionsRight - optionsLeft);

        Area options = Area.of(optionsLeft, content.y(), optionsWidth, content.height());
        Area contentWidgets = content.insetY(s.contentPadding());
        Area typeSelector = Area.of(options.x(), panel.y() + selector.topOffset(), options.width(), selector.height());
        Area footerButton = Area.of(panel.x2() - button.footerRightInset() - button.width(), footer.y() + button.footerOffsetY(), button.width(), button.height());
        Area emptyStateButton = Area.of(
                screenWidth / 2 - button.emptyStateWidth() / 2, screenHeight / 2 + button.emptyStateOffsetY(), button.emptyStateWidth(), button.emptyStateHeight());

        return new Layout(panel, header, content, contentWidgets, sidebar, options, info, footer, typeSelector, footerButton, emptyStateButton, compact);
    }
}