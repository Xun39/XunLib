package net.xun.lib.common.api.client.gui.config.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.components.IAreaWidget;
import net.xun.lib.common.api.client.gui.config.IConfigOwnerScreen;
import net.xun.lib.common.api.client.gui.config.IThemedConfigGui;
import net.xun.lib.common.api.client.gui.config.XunConfigTheme;
import net.xun.lib.common.api.client.gui.config.layout.InformationLayout;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigInformationPanel extends AbstractContainerWidget implements IAreaWidget, IThemedConfigGui {
    private final XunConfigTheme theme;
    private final IConfigOwnerScreen owner;

    private final InformationSectionWidget titleWidget;
    private final InformationSectionWidget descriptionWidget;
    private final InformationSectionWidget typeWidget;

    private ConfigOption lastOption;
    private boolean lastOptionVisible;

    private int lastWidth = -1;
    private int lastHeight = -1;

    public ConfigInformationPanel(Font font, XunConfigTheme theme, IConfigOwnerScreen owner, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());

        this.theme = theme;
        this.owner = owner;

        this.active = false;

        this.titleWidget = new InformationSectionWidget(InformationSection.TITLE, font, theme, owner);

        this.descriptionWidget = new InformationSectionWidget(InformationSection.DESCRIPTION, font, theme, owner);

        this.typeWidget = new InformationSectionWidget(InformationSection.TYPE, font, theme, owner);

        this.titleWidget.visible = true;
        this.descriptionWidget.visible = true;
        this.typeWidget.visible = false;

        updateLayout();
    }

    private void updateLayout() {
        if (titleWidget == null || descriptionWidget == null || typeWidget == null) {
            return;
        }

        InformationLayout metrics = layout().information();

        int innerWidth = Math.max(0, width - metrics.paddingLeft() - metrics.paddingRight());

        ConfigOption option = owner.getHoveredOption();
        boolean showType = option != null && option.isVisible();

        int titleX = getX() + metrics.paddingLeft();
        int titleY = getY() + metrics.paddingTop();
        int titleHeight = Math.max(0, metrics.titleHeight());
        titleWidget.setRectangle(innerWidth, titleHeight, titleX, titleY);

        int descriptionX = getX() + metrics.paddingLeft();
        int descriptionY = getY() + metrics.descriptionTop();
        int descriptionHeight = descriptionWidget.getPreferredHeight(innerWidth, option);
        descriptionHeight = Math.max(0, descriptionHeight);
        descriptionWidget.setRectangle(innerWidth, descriptionHeight, descriptionX, descriptionY);

        int typeY = descriptionY + descriptionHeight + (showType ? metrics.descriptionTypeGap() : 0);
        int typeHeight = showType ? Math.max(0, height - (typeY - getY())) : 0;
        typeWidget.setRectangle(innerWidth, typeHeight, getX() + metrics.paddingLeft(), typeY);
        typeWidget.visible = showType;

        lastOption = option;
        lastOptionVisible = showType;
        lastWidth = width;
        lastHeight = height;
    }

    private void ensureLayout() {
        ConfigOption option = owner.getHoveredOption();

        boolean optionVisible = option != null && option.isVisible();

        if (option != lastOption || optionVisible != lastOptionVisible || width != lastWidth || height != lastHeight) {
            updateLayout();
        }
    }

    @Override
    public void setRectangle(int width, int height, int x, int y) {
        super.setRectangle(width, height, x, y);
        updateLayout();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        updateLayout();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        updateLayout();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        updateLayout();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        updateLayout();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ensureLayout();
        Area area = bounds();

        fill(graphics, area, theme.card().background());
        drawBorder(graphics, area, theme.card().border());

        titleWidget.render(graphics, mouseX, mouseY, partialTick);
        descriptionWidget.render(graphics, mouseX, mouseY, partialTick);

        if (typeWidget.visible) {
            typeWidget.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(titleWidget, descriptionWidget, typeWidget);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
    }

    @Override
    public XunConfigTheme theme() {
        return theme;
    }

    @Override
    public AbstractWidget widget() {
        return this;
    }

    private enum InformationSection {
        TITLE, DESCRIPTION, TYPE
    }

    private static final class InformationSectionWidget extends AbstractWidget implements IThemedConfigGui {

        private final InformationSection section;
        private final Font font;
        private final XunConfigTheme theme;
        private final IConfigOwnerScreen owner;

        private InformationSectionWidget(InformationSection section, Font font, XunConfigTheme theme, IConfigOwnerScreen owner) {
            super(0, 0, 0, 0, Component.empty());

            this.section = section;
            this.font = font;
            this.theme = theme;
            this.owner = owner;

            this.active = false;
        }

        private int getPreferredHeight(int availableWidth, ConfigOption option) {
            if (section != InformationSection.DESCRIPTION || availableWidth <= 0) {
                return 0;
            }

            Component description = getDescription(option);

            int lineCount = font.split(description, availableWidth).size();
            return lineCount * font.lineHeight;
        }

        private Component getDescription(ConfigOption option) {
            if (option == null) {
                return Component.translatableWithFallback("gui.xunlib.config.hover_option", "Hover over an option to view its details.");
            }

            if (option.isVisible()) {
                return option.getDescription();
            }

            return Component.translatableWithFallback("gui.xunlib.config.disabled_by_dependency", "Disabled by dependency setting: " + (option.dependsOnField != null ? option.dependsOnField : "unknown"));
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            ConfigOption option = owner.getHoveredOption();

            switch (section) {
                case TITLE -> renderTitle(graphics, option);
                case DESCRIPTION -> renderDescription(graphics, option);
                case TYPE -> renderType(graphics, option);
            }
        }

        private void renderTitle(GuiGraphics graphics, ConfigOption option) {
            Component title = option == null ? Component.translatableWithFallback("gui.xunlib.config.information", "Information") : option.getDisplayName();

            graphics.drawString(font, title, getX(), getY(), theme.accent().primary(), false);
        }

        private void renderDescription(GuiGraphics graphics, ConfigOption option) {
            if (width <= 0 || height <= 0) {
                return;
            }

            Component description = getDescription(option);
            graphics.drawWordWrap(font, description, getX(), getY(), width, theme.text().muted());
        }

        private void renderType(GuiGraphics graphics, ConfigOption option) {
            if (option == null || !option.isVisible() || width <= 0 || height <= 0) {
                return;
            }

            InformationLayout metrics = layout().information();

            if (metrics.typeSeparatorHeight() > 0) {
                fill(graphics, Area.of(getX(), getY(), width, metrics.typeSeparatorHeight()), theme.panel().border());
            }

            graphics.drawString(font, Component.translatableWithFallback("gui.xunlib.config.option_type", "Type"), getX(), getY() + metrics.typeLabelOffsetY(), theme.text().muted(), false);

            String typeName = option.type.getSimpleName();
            String valueText = formatValue(option.getValue());

            Component typeComponent = Component.literal(typeName);
            Component valueComponent = Component.literal("  •  " + valueText);

            int valueY = getY() + metrics.typeValueOffsetY();
            graphics.drawString(font, typeComponent, getX(), valueY, theme.text().primary(), false);

            int valueX = getX() + font.width(typeName);
            graphics.drawString(font, valueComponent, valueX, valueY, theme.text().muted(), false);
        }

        private String formatValue(Object value) {
            if (value == null) {
                return "null";
            }

            return String.valueOf(value);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        }

        @Override
        public XunConfigTheme theme() {
            return theme;
        }
    }
}
