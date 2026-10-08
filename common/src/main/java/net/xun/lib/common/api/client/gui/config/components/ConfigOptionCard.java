package net.xun.lib.common.api.client.gui.config.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.xun.lib.common.api.client.gui.components.IAreaWidget;
import net.xun.lib.common.api.client.gui.config.IThemedConfigGui;
import net.xun.lib.common.api.client.gui.config.IConfigOwnerScreen;
import net.xun.lib.common.api.client.gui.config.layout.*;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.client.gui.config.XunConfigLayout;
import net.xun.lib.common.api.client.gui.config.XunConfigTheme;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigOptionCard extends AbstractContainerWidget implements IThemedConfigGui, IAreaWidget {
    private final IConfigOwnerScreen owner;
    private final ConfigOption option;
    private final XunConfigTheme theme;

    private final ConfigOptionTextWidget textWidget;
    private final AbstractWidget controlWidget;

    public ConfigOptionCard(IConfigOwnerScreen owner, ConfigOption option, XunConfigTheme theme, int height) {
        super(0, 0, 0, height, option.getDisplayName());

        this.owner = owner;
        this.option = option;
        this.theme = theme;

        this.textWidget = new ConfigOptionTextWidget(option, theme);

        this.controlWidget = OptionControlFactory.createControl(option, theme);

        this.active = option.isVisible();

        updateLayout();
    }

    public ConfigOption option() {
        return option;
    }

    public AbstractWidget controlWidget() {
        return controlWidget;
    }

    private void updateActiveState() {
        boolean enabled = option.isVisible();

        active = enabled;

        textWidget.active = false;
        textWidget.visible = true;

        controlWidget.active = enabled;
        controlWidget.visible = true;
    }

    private void updateLayout() {
        if (textWidget == null || controlWidget == null) {
            return;
        }

        CardLayout metrics = layout().card();

        int controlColumnWidth = Math.max(controlWidget.getWidth(), layout().control().columnWidth());
        int gap = Math.max(0, metrics.controlGap());

        int availableWidth = Math.max(0, width - metrics.paddingLeft() - metrics.paddingRight());

        int textWidth = Math.max(0, availableWidth - controlColumnWidth - gap);
        int textHeight = Math.max(0, height);

        textWidget.setSize(textWidth, textHeight);

        FrameLayout controlCell = new FrameLayout(controlColumnWidth, textHeight);
        controlCell.addChild(controlWidget, settings -> settings.alignHorizontallyRight().alignVerticallyMiddle());
        controlCell.arrangeElements();

        GridLayout rowLayout = new GridLayout();
        rowLayout.columnSpacing(gap);

        rowLayout.addChild(textWidget, 0, 0, settings -> settings.alignHorizontallyLeft().alignVerticallyMiddle());
        rowLayout.addChild(controlCell, 0, 1, settings -> settings.alignHorizontallyLeft().alignVerticallyMiddle());

        rowLayout.setPosition(getX() + metrics.paddingLeft(), getY());
        rowLayout.arrangeElements();
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
        updateActiveState();

        Area card = bounds();
        boolean hovered = active && card.contains(mouseX, mouseY);

        if (hovered) {
            owner.setHoveredOption(option);
        }

        drawCard(graphics, card, active, hovered);

        textWidget.render(graphics, mouseX, mouseY, partialTick);
        controlWidget.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(textWidget, controlWidget);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    @Override
    public AbstractWidget widget() {
        return this;
    }

    @Override
    public XunConfigTheme theme() {
        return theme;
    }

    private static final class OptionControlFactory {
        private OptionControlFactory() {
        }

        public static AbstractWidget createControl(ConfigOption option, XunConfigTheme theme) {
            XunConfigLayout layout = theme.layout();
            Class<?> type = option.type;

            if (type == boolean.class || type == Boolean.class) {
                ToggleLayout metrics = layout.toggle();
                return new ToggleControlWidget(option, theme, 0, 0, metrics.width(), metrics.height());
            }
            if (type.isEnum()) {
                DropdownLayout metrics = layout.dropdown();
                return new EnumDropdownControlWidget(option, theme, 0, 0, metrics.width(), metrics.height());
            }
            if (Number.class.isAssignableFrom(type) || type == byte.class || type == short.class || type == int.class || type == long.class || type == float.class || type == double.class) {
                SliderLayout metrics = layout.slider();
                return new NumberSliderControlWidget(option, theme, 0, 0, metrics.width(), metrics.height());
            }

            ControlLayout metrics = layout.control();
            return new ReadOnlyValueWidget(option, theme, 0, 0, metrics.defaultWidth(), metrics.defaultHeight());
        }
    }
}