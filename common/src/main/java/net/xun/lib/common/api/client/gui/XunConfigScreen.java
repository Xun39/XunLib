package net.xun.lib.common.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.config.IConfigOwnerScreen;
import net.xun.lib.common.api.client.gui.config.components.ConfigPanel;
import net.xun.lib.common.api.client.gui.config.components.ConfigTypeSelector;
import net.xun.lib.common.api.client.gui.config.components.EnumDropdownControlWidget;
import net.xun.lib.common.api.client.gui.config.components.ThemedActionButton;
import net.xun.lib.common.api.client.gui.config.layout.ButtonLayout;
import net.xun.lib.common.api.config.ConfigDefinition;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.ConfigType;
import net.xun.lib.common.api.client.gui.config.XunConfigLayout;
import net.xun.lib.common.api.config.XunConfigManager;
import net.xun.lib.common.api.client.gui.config.XunConfigTheme;
import net.xun.lib.common.api.util.TranslationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class XunConfigScreen extends Screen implements IConfigOwnerScreen {
    private final Screen parent;
    private final String modId;
    private final XunConfigTheme theme;

    private ConfigType selectedType;

    private ConfigDefinition registeredConfig;
    private ConfigDefinition config;

    private String selectedCategoryKey;
    private ConfigOption hoveredOption;

    private ConfigPanel configPanel;

    public XunConfigScreen(Screen parent, String modId) {
        this(parent, modId, XunConfigTheme.DEFAULT);
    }

    public XunConfigScreen(Screen parent, String modId, XunConfigTheme theme) {
        super(Component.translatableWithFallback(TranslationUtil.translationKey("config", modId, "title"), modId.toUpperCase() + " Settings"));

        this.parent = parent;
        this.modId = modId;
        this.theme = theme;

        Map<ConfigType, ConfigDefinition> configs = XunConfigManager.getConfigs(modId);

        selectedType = getInitialType(configs);

        selectRegisteredConfig(selectedType);
    }

    private void selectRegisteredConfig(ConfigType type) {
        registeredConfig = XunConfigManager.getConfig(modId, type);
        config = registeredConfig != null ? registeredConfig.createEditorCopy() : null;
    }

    @Override
    protected void init() {
        clearWidgets();
        configPanel = null;

        XunConfigLayout.Layout layout = XunConfigLayout.create(width, height, theme.layout());

        Map<ConfigType, ConfigDefinition> configs = XunConfigManager.getConfigs(modId);

        if (configs.isEmpty()) {
            var metrics = theme.layout().button();

            addRenderableWidget(
                    new ThemedActionButton(
                            theme,
                            Component.translatableWithFallback("gui.xunlib.config.back", "Back"), this::closeScreen
                    )
            ).setRectangle(metrics.emptyStateWidth(), metrics.emptyStateHeight(), layout.emptyStateButton().x(), layout.emptyStateButton().y());

            return;
        }

        addRenderableWidget(
                new ConfigTypeSelector(
                        this, font, theme,
                        layout.typeSelector().x(), layout.typeSelector().y(), layout.typeSelector().width(), layout.typeSelector().height(),
                        configs.keySet(), selectedType
                )
        );

        if (config == null) {
            return;
        }

        configPanel = addRenderableWidget(new ConfigPanel(this, config, font, theme, layout));

        rebuildConfigContent();
        addFooterButtons(layout);
    }

    private void rebuildConfigContent() {
        if (configPanel == null || config == null) {
            return;
        }

        selectedCategoryKey = configPanel.rebuild(selectedCategoryKey);
    }

    private void addFooterButtons(XunConfigLayout.Layout layout) {
        ButtonLayout metrics = theme.layout().button();
        LinearLayout row = LinearLayout.horizontal().spacing(metrics.footerSpacing());

        addButton(
                row,
                new ThemedActionButton(
                        theme, Component.translatableWithFallback("gui.xunlib.config.expand_all", "Expand All"), this::expandAll
                ),
                metrics
        );

        addButton(
                row,
                new ThemedActionButton(
                        theme, Component.translatableWithFallback("gui.xunlib.config.collapse_all", "Collapse All"), this::collapseAll
                ),
                metrics
        );

        if (registeredConfig != null && XunConfigManager.canEdit(registeredConfig)) {
            addButton(row,
                    new ThemedActionButton(
                            theme, Component.translatableWithFallback("gui.xunlib.config.save", "Save"), this::saveAndClose
                    ),
                    metrics
            );
        }
        else if (registeredConfig != null && selectedType == ConfigType.SERVER) {
            addButton(
                    row,
                    new ThemedActionButton(
                            theme, Component.translatableWithFallback("gui.xunlib.config.read_only", "Read Only"), () -> {}
                    ), metrics
            );
        }

        row.arrangeElements();

        int rowX = layout.footerButtons().centerX() - row.getWidth() / 2;
        int rowY = layout.footerButtons().centerY() - row.getHeight() / 2;

        row.setPosition(rowX, rowY);
        row.arrangeElements();

        row.visitWidgets(this::addRenderableWidget);
    }

    private void addButton(LinearLayout row, ThemedActionButton button, ButtonLayout metrics) {
        button.setRectangle(metrics.width(), metrics.height(), 0, 0);
        row.addChild(button);
    }

    private void expandAll() {
        if (configPanel == null) {
            return;
        }

        closeOpenDropdown();
        configPanel.categoryList().expandAll();
    }

    private void collapseAll() {
        if (configPanel == null) {
            return;
        }

        closeOpenDropdown();
        configPanel.categoryList().collapseAll();
    }

    @Override
    public void selectCategory(String categoryKey) {
        if (config == null || configPanel == null) {
            return;
        }
        if (config.getCategory(categoryKey) == null) {
            return;
        }

        closeOpenDropdown();

        selectedCategoryKey = categoryKey;
        hoveredOption = null;

        configPanel.selectCategory(categoryKey);
    }

    @Override
    public void selectConfigType(ConfigType type) {
        if (selectedType == type) {
            return;
        }

        closeOpenDropdown();

        selectedType = type;
        selectedCategoryKey = null;
        hoveredOption = null;

        selectRegisteredConfig(type);

        init();
    }

    @Override
    public void setHoveredOption(ConfigOption option) {
        hoveredOption = option;
    }

    @Override
    public ConfigOption getHoveredOption() {
        return hoveredOption;
    }

    private EnumDropdownControlWidget getOpenDropdown() {
        return configPanel == null ? null : configPanel.getOpenDropdown();
    }

    private void closeOpenDropdown() {
        EnumDropdownControlWidget dropdown = getOpenDropdown();

        if (dropdown != null) {
            dropdown.setExpanded(false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        EnumDropdownControlWidget dropdown = getOpenDropdown();

        if (dropdown != null) {
            if (dropdown.handlePopupClick(mouseX, mouseY, button)) {
                return true;
            }

            if (!dropdown.contains(mouseX, mouseY)) {
                dropdown.setExpanded(false);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        hoveredOption = null;

        super.render(graphics, mouseX, mouseY, partialTick);

        EnumDropdownControlWidget dropdown = getOpenDropdown();

        if (dropdown != null) {
            dropdown.renderPopup(graphics, mouseX, mouseY);
        }
    }

    private void saveAndClose() {
        if (registeredConfig != null && config != null) {
            XunConfigManager.saveEditedConfig(registeredConfig, config);
        }

        closeScreen();
    }

    private void closeScreen() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }

    @Override
    public void onClose() {
        closeOpenDropdown();
        closeScreen();
    }

    private static ConfigType getInitialType(Map<ConfigType, ConfigDefinition> configs) {
        if (configs.containsKey(ConfigType.COMMON)) {
            return ConfigType.COMMON;
        }

        if (configs.containsKey(ConfigType.CLIENT)) {
            return ConfigType.CLIENT;
        }

        if (configs.containsKey(ConfigType.SERVER)) {
            return ConfigType.SERVER;
        }

        return ConfigType.COMMON;
    }
}