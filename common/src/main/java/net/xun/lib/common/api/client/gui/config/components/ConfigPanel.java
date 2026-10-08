package net.xun.lib.common.api.client.gui.config.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.components.IAreaWidget;
import net.xun.lib.common.api.client.gui.config.IConfigOwnerScreen;
import net.xun.lib.common.api.client.gui.config.IThemedConfigGui;
import net.xun.lib.common.api.config.ConfigDefinition;
import net.xun.lib.common.api.client.gui.config.XunConfigLayout;
import net.xun.lib.common.api.client.gui.config.XunConfigTheme;
import net.xun.lib.common.api.client.gui.config.layout.PanelLayout;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigPanel extends AbstractContainerWidget implements IAreaWidget, IThemedConfigGui {
    private static final Component DESCRIPTION = Component.translatableWithFallback(
            "gui.xunlib.config.configure_description", "Configure this mod's settings"
    );

    private final Font font;
    private final ConfigDefinition config;
    private final IConfigOwnerScreen owner;
    private final XunConfigTheme theme;
    private final XunConfigLayout.Layout screenLayout;

    private final ConfigCategoryList categoryList;
    private final ConfigOptionList optionList;
    private final ConfigInformationPanel informationPanel;

    public ConfigPanel(IConfigOwnerScreen owner, ConfigDefinition config, Font font, XunConfigTheme theme, XunConfigLayout.Layout screenLayout) {
        super(screenLayout.panel().x(), screenLayout.panel().y(), screenLayout.panel().width(), screenLayout.panel().height(), Component.empty());

        this.font = font;
        this.config = config;
        this.owner = owner;
        this.theme = theme;
        this.screenLayout = screenLayout;

        Minecraft minecraft = Minecraft.getInstance();

        this.categoryList = new ConfigCategoryList(owner, config, theme, minecraft, 0, 0, screenLayout.sidebar().width(), screenLayout.contentWidgets().height());
        this.optionList = new ConfigOptionList(owner, theme, minecraft, 0, 0, screenLayout.options().width(), screenLayout.contentWidgets().height());
        this.informationPanel = screenLayout.compact() ? null : new ConfigInformationPanel(font, theme, owner, 0, 0, screenLayout.info().width(), screenLayout.contentWidgets().height());

        updateLayout();
    }

    private void updateLayout() {
        XunConfigLayout.Layout layout = screenLayout;

        int contentHeight = layout.contentWidgets().height();

        categoryList.setSize(layout.sidebar().width(), contentHeight);
        optionList.setSize(layout.options().width(), contentHeight);

        if (informationPanel != null) {
            informationPanel.setSize(layout.info().width(), contentHeight);
        }

        int categoryWidth = layout.sidebar().width();

        int optionWidth = layout.options().width();

        int gap1 = Math.max(0, layout.options().x() - layout.sidebar().x() - categoryWidth);

        int gap2 = informationPanel == null ? 0 : Math.max(0, layout.info().x() - layout.options().x() - optionWidth);

        GridLayout contentLayout = new GridLayout(layout.sidebar().x(), layout.contentWidgets().y());

        contentLayout.addChild(categoryList, 0, 0, settings -> settings.alignHorizontallyLeft().alignVerticallyTop());

        contentLayout.addChild(optionList, 0, 1, settings -> settings.paddingLeft(gap1).alignHorizontallyLeft().alignVerticallyTop());

        if (informationPanel != null) {
            contentLayout.addChild(informationPanel, 0, 2, settings -> settings.paddingLeft(gap2).alignHorizontallyLeft().alignVerticallyTop());
        }

        contentLayout.arrangeElements();
    }

    public String rebuild(String selectedCategoryKey) {
        String selected = categoryList.rebuild(selectedCategoryKey);

        selectCategory(selected);

        return selected;
    }

    public void selectCategory(String categoryKey) {
        if (categoryKey == null) {
            optionList.rebuild(List.of());
            return;
        }

        categoryList.updateSelection(categoryKey);

        optionList.rebuild(config.getOptionsInCategory(categoryKey));
    }

    public ConfigCategoryList categoryList() {
        return categoryList;
    }

    public ConfigOptionList optionList() {
        return optionList;
    }

    public ConfigInformationPanel informationPanel() {
        return informationPanel;
    }

    public EnumDropdownControlWidget getOpenDropdown() {
        return optionList.getOpenDropdown();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Area area = bounds();
        PanelLayout metrics = layout().panel();

        drawShadow(graphics, area);
        drawGradientPanel(graphics, area, theme.panel().top(), theme.panel().bottom(), theme.panel().border());

        graphics.drawString(font, owner.getTitle(), area.x() + metrics.titleLeft(), area.y() + metrics.titleTop(), theme.text().primary(), false);
        graphics.drawString(font, DESCRIPTION, area.x() + metrics.descriptionLeft(), area.y() + metrics.descriptionTop(), theme.text().muted(), false);

        categoryList.render(graphics, mouseX, mouseY, partialTick);

        optionList.render(graphics, mouseX, mouseY, partialTick);

        if (informationPanel != null) {
            informationPanel.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        List<GuiEventListener> children = new ArrayList<>(3);

        children.add(categoryList);
        children.add(optionList);

        if (informationPanel != null) {
            children.add(informationPanel);
        }

        return children;
    }

    @Override
    protected void updateWidgetNarration(@NotNull net.minecraft.client.gui.narration.NarrationElementOutput output) {
    }

    @Override
    public XunConfigTheme theme() {
        return theme;
    }

    @Override
    public AbstractWidget widget() {
        return this;
    }
}