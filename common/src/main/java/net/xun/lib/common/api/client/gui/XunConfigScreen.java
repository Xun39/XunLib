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

    private ConfigDefinition registeredConfig;
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

        categoryList = null;
        optionList = null;

        XunConfigLayout.Layout layout = XunConfigLayout.create(width, height, theme.layout());

        Map<ConfigType, ConfigDefinition> configs = XunConfigManager.getConfigs(modId);

        if (configs.isEmpty()) {
            var metrics = theme.layout().button();

            addRenderableWidget(
                    new ActionButton(Component.translatableWithFallback("gui.xunlib.config.back", "Back"), theme, this::closeScreen)
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

        addRenderableWidget(
                new ConfigPanel(
                        font, theme, this,
                        layout.panel().x(), layout.panel().y(), layout.panel().width(), layout.panel().height()
                )
        );

        rebuildConfigContent(layout);

        if (registeredConfig != null && XunConfigManager.canEdit(registeredConfig)) {
            addRenderableWidget(new ActionButton(
                    Component.translatableWithFallback("gui.xunlib.config.save", "Save"), theme, this::saveAndClose)
            ).setRectangle(layout.footerButton().width(), layout.footerButton().height(), layout.footerButton().x(), layout.footerButton().y());
        }
        else if (registeredConfig != null && selectedType == ConfigType.SERVER) {
            addRenderableWidget(
                    new ActionButton(Component.translatableWithFallback("gui.xunlib.config.read_only", "Read Only"), theme, () -> {})
            ).setRectangle(layout.footerButton().width(), layout.footerButton().height(), layout.footerButton().x(), layout.footerButton().y());
        }
    }

    private void rebuildConfigContent(XunConfigLayout.Layout layout) {
        categoryList = addRenderableWidget(
                new ConfigCategoryList(
                        this, config, theme, minecraft,
                        layout.sidebar().x(), layout.sidebar().y(), layout.sidebar().width(), layout.sidebar().height()
                )
        );
        optionList = addRenderableWidget(
                new ConfigOptionList(
                        this, theme, minecraft,
                        layout.options().x(), layout.contentWidgets().y(), layout.options().width(), layout.contentWidgets().height()
                )
        );

        if (!layout.compact()) {
            addRenderableWidget(
                    new ConfigInformationPanel(
                            font, theme, this,
                            layout.info().x(), layout.contentWidgets().y(), layout.info().width(), layout.contentWidgets().height()
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

        closeOpenDropdown();

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

        closeOpenDropdown();

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

    private EnumDropdownControlWidget getOpenDropdown() {
        return optionList == null ? null : optionList.getOpenDropdown();
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