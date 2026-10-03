package net.xun.lib.common.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.config.ConfigDefinition;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigTheme;
import net.xun.lib.common.api.config.XunLibConfigManager;
import net.xun.lib.common.api.util.TranslationUtil;

import java.util.List;

public class XunConfigScreen extends Screen {

    private static final int MARGIN = 18;

    private static final int HEADER_HEIGHT = 48;
    private static final int FOOTER_HEIGHT = 34;

    private static final int SIDEBAR_WIDTH = 122;
    private static final int INFO_WIDTH = 190;
    private static final int GAP = 12;

    private final Screen parent;
    private final ConfigDefinition config;
    private final XunConfigTheme theme;

    private int selectedCategoryIndex;

    private XunConfigScreenComponents.CategoryList categoryList;
    private XunConfigScreenComponents.OptionList optionList;

    private XunConfigScreenComponents.ActionButton saveButton;
    private XunConfigScreenComponents.ActionButton backButton;

    private ConfigOption hoveredOption;

    public XunConfigScreen(Screen parent, String modId) {
        this(parent, modId, XunConfigTheme.DEFAULT);
    }

    public XunConfigScreen(Screen parent, String modId, XunConfigTheme theme) {
        super(Component.translatableWithFallback(TranslationUtil.translationKey("config", modId, "title"), modId.toUpperCase() + " Settings"));

        this.parent = parent;
        this.config = XunLibConfigManager.getConfig(modId);
        this.theme = theme;
    }

    @Override
    protected void init() {
        clearWidgets();

        hoveredOption = null;

        if (config == null) {
            backButton = addRenderableWidget(new XunConfigScreenComponents.ActionButton(Component.translatableWithFallback("xunlib.gui.back", "Back"), theme, this::closeScreen));

            backButton.setPosition(width / 2 - 38, height / 2 + 30);

            return;
        }

        createLayout();
    }

    private void createLayout() {
        int panelX = MARGIN;
        int panelY = MARGIN;

        int panelWidth = width - MARGIN * 2;

        int panelHeight = height - MARGIN * 2;

        int contentTop = panelY + HEADER_HEIGHT;

        int footerTop = panelY + panelHeight - FOOTER_HEIGHT;

        int contentHeight = footerTop - contentTop - 10;

        boolean compact = panelWidth < 760;

        int sidebarWidth = compact ? 108 : SIDEBAR_WIDTH;

        int infoWidth = compact ? 0 : INFO_WIDTH;

        int sidebarX = panelX + 10;

        int optionsX = sidebarX + sidebarWidth + GAP;

        int infoX = panelX + panelWidth - infoWidth - 10;

        int optionsRight = compact ? panelX + panelWidth - 10 : infoX - GAP;

        int optionsWidth = optionsRight - optionsX;

        drawLayoutSeparators(panelX, panelY, panelWidth, contentTop, footerTop, compact);

        // -------------------------------------------------------------
        // Category list
        // -------------------------------------------------------------

        categoryList = addRenderableWidget(new XunConfigScreenComponents.CategoryList(this, config, theme, minecraft, sidebarX, contentTop + 10, sidebarWidth, contentHeight - 10));

        categoryList.rebuild(selectedCategoryIndex);

        // -------------------------------------------------------------
        // Option list
        // -------------------------------------------------------------

        optionList = addRenderableWidget(new XunConfigScreenComponents.OptionList(this, theme, minecraft, optionsX, contentTop + 10, Math.max(80, optionsWidth), contentHeight - 10));

        if (!config.getCategoryKeys().isEmpty()) {
            selectCategory(Math.min(selectedCategoryIndex, config.getCategoryKeys().size() - 1));
        }

        // -------------------------------------------------------------
        // Save button
        // -------------------------------------------------------------

        saveButton = addRenderableWidget(new XunConfigScreenComponents.ActionButton(Component.translatableWithFallback("xunlib.gui.save", "Save"), theme, this::saveAndClose));

        saveButton.setPosition(panelX + panelWidth - 88, footerTop + 5);
    }

    private void drawLayoutSeparators(int panelX, int panelY, int panelWidth, int contentTop, int footerTop, boolean compact) {
        // The separators are drawn in render(), not here.
    }

    void selectCategory(int index) {
        if (config == null) {
            return;
        }

        List<String> categories = config.getCategoryKeys();

        if (index < 0 || index >= categories.size()) {
            return;
        }

        selectedCategoryIndex = index;
        hoveredOption = null;

        if (categoryList != null) {
            categoryList.updateSelection(index);
        }

        if (optionList != null) {
            optionList.rebuild(config.getOptionsInCategory(categories.get(index)));
        }
    }

    void setHoveredOption(ConfigOption option) {
        this.hoveredOption = option;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, theme.screenBackground());

        drawPanel(graphics);

        if (config == null) {
            drawMissingConfig(graphics);

            super.render(graphics, mouseX, mouseY, partialTick);

            return;
        }

        hoveredOption = null;

        drawHeader(graphics);

        drawSectionLabels(graphics);

        super.render(graphics, mouseX, mouseY, partialTick);

        drawInformationPanel(graphics);
    }

    private void drawPanel(GuiGraphics graphics) {
        int panelX = MARGIN;
        int panelY = MARGIN;

        int panelWidth = width - MARGIN * 2;

        int panelHeight = height - MARGIN * 2;

        // Shadow
        graphics.fill(panelX + 2, panelY + 3, panelX + panelWidth + 2, panelY + panelHeight + 3, theme.panelShadow());

        // Gradient
        graphics.fillGradient(panelX, panelY, panelX + panelWidth, panelY + panelHeight, theme.panelTop(), theme.panelBottom());

        // Border
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + 1, theme.panelBorder());

        graphics.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, theme.panelBorder());

        graphics.fill(panelX, panelY, panelX + 1, panelY + panelHeight, theme.panelBorder());

        graphics.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, theme.panelBorder());
    }

    private void drawHeader(GuiGraphics graphics) {
        int x = MARGIN + 16;
        int y = MARGIN + 12;

        graphics.drawString(font, title, x, y, theme.text(), false);

        Component subtitle = Component.translatableWithFallback("xunlib.gui.configure_description", "Configure this mod's settings");

        graphics.drawString(font, subtitle, x, y + 14, theme.textMuted(), false);
    }

    private void drawSectionLabels(GuiGraphics graphics) {
        int panelX = MARGIN;
        int panelWidth = width - MARGIN * 2;

        boolean compact = panelWidth < 760;

        int sidebarWidth = compact ? 108 : SIDEBAR_WIDTH;

        int infoWidth = compact ? 0 : INFO_WIDTH;

        int contentTop = MARGIN + HEADER_HEIGHT;

        int sidebarX = panelX + 10;

        int optionsX = sidebarX + sidebarWidth + GAP;

        graphics.drawString(font, Component.translatableWithFallback("xunlib.gui.categories", "Categories"), sidebarX + 4, contentTop, theme.textMuted(), false);

        graphics.drawString(font, Component.translatableWithFallback("xunlib.gui.settings", "Settings"), optionsX + 4, contentTop, theme.textMuted(), false);

        if (infoWidth > 0) {
            int infoX = panelX + panelWidth - infoWidth - 10;

            graphics.drawString(font, Component.translatableWithFallback("xunlib.gui.information", "Information"), infoX + 4, contentTop, theme.textMuted(), false);
        }
    }

    private void drawInformationPanel(GuiGraphics graphics) {
        int panelX = MARGIN;
        int panelWidth = width - MARGIN * 2;

        boolean compact = panelWidth < 760;

        if (compact) {
            return;
        }

        int infoX = panelX + panelWidth - INFO_WIDTH - 10;

        int infoY = MARGIN + HEADER_HEIGHT + 10;

        int infoWidth = INFO_WIDTH;

        int infoHeight = height - MARGIN - infoY - FOOTER_HEIGHT - 8;

        drawCard(graphics, infoX, infoY, infoWidth, infoHeight, false, true, theme);

        Component titleComponent = Component.translatableWithFallback("xunlib.gui.information", "Information");

        Component descriptionComponent;

        if (hoveredOption == null) {
            descriptionComponent = Component.translatableWithFallback("xunlib.gui.hover_option", "Hover over an option to view its details.");
        }
        else {
            titleComponent = Component.translatableWithFallback(hoveredOption.nameKey, hoveredOption.nameFallback);

            if (!hoveredOption.isVisible()) {
                descriptionComponent = Component.translatableWithFallback("xunlib.gui.disabled_by_dependency", "Disabled by dependency setting: " + String.valueOf(hoveredOption.dependsOnField));
            }
            else {
                descriptionComponent = Component.translatableWithFallback(hoveredOption.descriptionKey, hoveredOption.descriptionFallback);
            }
        }

        graphics.drawString(font, titleComponent, infoX + 10, infoY + 12, theme.accent(), false);

        graphics.drawWordWrap(font, descriptionComponent, infoX + 10, infoY + 34, infoWidth - 20, theme.textMuted());

        if (hoveredOption != null && hoveredOption.isVisible()) {

            graphics.fill(infoX + 10, infoY + 64, infoX + infoWidth - 10, infoY + 65, theme.panelBorder());

            Component type = Component.translatableWithFallback("xunlib.gui.type", "Type");

            graphics.drawString(font, type, infoX + 10, infoY + 75, theme.textMuted(), false);

            String typeName = hoveredOption.type.getSimpleName();

            graphics.drawString(font, typeName, infoX + 10, infoY + 89, theme.text(), false);
        }
    }

    private void drawMissingConfig(GuiGraphics graphics) {
        int centerX = width / 2;

        int centerY = height / 2 - 20;

        Component title = Component.translatableWithFallback("xunlib.gui.missing_definition", "Config definition not found");

        Component description = Component.translatableWithFallback("xunlib.gui.missing_definition_description", "The config screen is registered, but no config definition was registered for this mod.");

        graphics.drawCenteredString(font, title, centerX, centerY, theme.text());

        graphics.drawWordWrap(font, description, centerX - 180, centerY + 18, 360, theme.textMuted());
    }

    private static void drawCard(GuiGraphics graphics, int x, int y, int width, int height, boolean hovered, boolean enabled, XunConfigTheme theme) {
        int background = !enabled ? theme.cardDisabled() : hovered ? theme.cardHover() : theme.cardBackground();

        int border = !enabled ? 0x10FFFFFF : hovered ? theme.accent() : theme.cardBorder();

        graphics.fill(x, y, x + width, y + height, background);

        graphics.fill(x, y, x + width, y + 1, border);

        graphics.fill(x, y + height - 1, x + width, y + height, border);

        graphics.fill(x, y, x + 1, y + height, border);

        graphics.fill(x + width - 1, y, x + width, y + height, border);
    }

    private void saveAndClose() {
        if (config == null) {
            closeScreen();
            return;
        }

        XunLibConfigManager.saveConfig(config.modId);

        closeScreen();
    }

    private void closeScreen() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }

    @Override
    public void onClose() {
        // ESC leaves without saving, matching the
        // usual config-screen expectation.
        closeScreen();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // We draw our own background.
    }
}