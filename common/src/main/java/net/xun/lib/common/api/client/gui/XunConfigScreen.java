package net.xun.lib.common.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.components.*;
import net.xun.lib.common.api.config.*;
import net.xun.lib.common.api.util.TranslationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class XunConfigScreen extends Screen implements XunConfigOwner {
    private final Screen parent;
    private final String modId;
    private final XunConfigTheme theme;

    private ConfigType selectedType;

    /**
     * Actual registered/live config.
     */
    private ConfigDefinition registeredConfig;

    /**
     * Temporary config being edited.
     */
    private ConfigDefinition config;

    private int selectedCategory;
    private ConfigOption hoveredOption;

    private ConfigCategoryList categoryList;
    private ConfigOptionList optionList;

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

        var layout = XunConfigLayout.create(width, height, theme.layout());

        Map<ConfigType, ConfigDefinition> configs = XunConfigManager.getConfigs(modId);

        if (configs.isEmpty()) {
            addRenderableWidget(
                    new ActionButton(Component.translatableWithFallback("gui.xunlib.config.back", "Back"), theme, this::closeScreen)
            ).setPosition(width / 2 - 38, height / 2 + 30);

            return;
        }

        addRenderableWidget(
                new ConfigTypeSelector(
                        this, font, theme,
                        layout.optionsX(), layout.panelY() + 8, layout.optionsWidth(), 22,
                        configs.keySet(), selectedType
                )
        );

        if (config == null) return;

        addRenderableWidget(new ConfigPanel(font, theme, this, layout.panelX(), layout.panelY(), layout.panelWidth(), layout.panelHeight()));

        rebuildConfigContent(layout);

        // Only editable in a world (where an integrated server is running)
        int saveButtonX = layout.panelX() + layout.panelWidth() - 88;
        if (registeredConfig != null && XunConfigManager.canEdit(registeredConfig)) {
            addRenderableWidget(
                    new ActionButton(
                            Component.translatableWithFallback("gui.xunlib.config.save", "Save"), theme, this::saveAndClose)
            ).setPosition(saveButtonX, layout.footerY() + 5);
        }
        else if (registeredConfig != null && selectedType == ConfigType.SERVER) {
            addRenderableWidget(
                    new ActionButton(
                            Component.translatableWithFallback("gui.xunlib.config.read_only", "Read Only"), theme, () -> {})
            ).setPosition(saveButtonX, layout.footerY() + 5);
        }
    }

    private void rebuildConfigContent(XunConfigLayout.Layout layout) {
        categoryList = addRenderableWidget(
                new ConfigCategoryList(
                        this, config, theme, minecraft,
                        layout.sidebarX(), layout.contentWidgetY(), layout.sidebarWidth(), layout.contentWidgetHeight()
                )
        );
        optionList = addRenderableWidget(
                new ConfigOptionList(
                        this, theme, minecraft,
                        layout.optionsX(), layout.contentWidgetY(), layout.optionsWidth(), layout.contentWidgetHeight()
                )
        );
        if (!layout.compact()) {
            addRenderableWidget(
                    new ConfigInformationPanel(
                            font, theme, this,
                            layout.infoX(), layout.contentWidgetY(), layout.infoWidth(), layout.contentWidgetHeight()
                    )
            );
        }
        categoryList.rebuild(selectedCategory);

        List<String> categories = config.getCategoryKeys();

        if (!categories.isEmpty()) {
            selectCategory(Math.min(selectedCategory, categories.size() - 1));
        }
    }

    @Override
    public void selectCategory(int index) {
        if (config == null) {
            return;
        }

        List<String> categories = config.getCategoryKeys();

        if (index < 0 || index >= categories.size()) {

            return;
        }

        selectedCategory = index;
        hoveredOption = null;

        categoryList.updateSelection(index);

        optionList.rebuild(config.getOptionsInCategory(categories.get(index)));
    }

    @Override
    public void selectConfigType(ConfigType type) {
        if (selectedType == type) {
            return;
        }
        selectedType = type;
        selectedCategory = 0;

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

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        hoveredOption = null;
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void saveAndClose() {
        if (registeredConfig != null && config != null) {
            XunConfigManager.saveEditedConfig(registeredConfig, config);
        }

        closeScreen();
    }

    private void closeScreen() {
        assert minecraft != null;
        minecraft.setScreen(parent);
    }

    @Override
    public void onClose() {
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