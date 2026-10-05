package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.IThemedConfigGui;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigLayout;
import net.xun.lib.common.api.config.XunConfigOwner;
import net.xun.lib.common.api.config.XunConfigTheme;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigOptionCard extends AbstractContainerWidget implements IThemedConfigGui, IAreaWidget {
    private final XunConfigOwner owner;
    private final ConfigOption option;
    private final XunConfigTheme theme;
    private final AbstractWidget controlWidget;

    public ConfigOptionCard(XunConfigOwner owner, ConfigOption option, XunConfigTheme theme, int height) {
        super(0, 0, 0, height, option.getDisplayName());

        this.owner = owner;
        this.option = option;
        this.theme = theme;

        this.active = option.isVisible();

        this.controlWidget = OptionControlFactory.createControl(option, theme);
    }

    public ConfigOption option() {
        return option;
    }

    public AbstractWidget controlWidget() {
        return controlWidget;
    }

    public Area controlColumn() {
        XunConfigLayout layout = layout();
        return bounds().withRightEdge(layout.control().columnWidth());
    }

    public Area textArea() {
        XunConfigLayout layout = layout();

        Area card = bounds();
        Area controls = controlColumn();

        int left = card.x() + layout.card().paddingLeft();
        int right = controls.x() - layout.card().controlGap();

        return Area.fromCorners(left, card.y(), Math.max(left, right), card.y2());
    }

    private Area controlBounds() {
        Area column = controlColumn();
        return column.centered(controlWidget.getWidth(), controlWidget.getHeight());
    }

    private void updateLayout() {
        if (controlWidget == null) {
            return;
        }
        Area area = controlBounds();
        controlWidget.setRectangle(area.width(), area.height(), area.x(), area.y());
    }

    private void updateActiveState() {
        boolean enabled = option.isVisible();

        this.active = enabled;

        controlWidget.active = enabled;
        controlWidget.visible = true;
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

        Minecraft minecraft = Minecraft.getInstance();
        XunConfigLayout.Card metrics = layout().card();

        Area text = textArea();
        Area titleArea = Area.of(text.x(), text.y() + metrics.titleTop(), text.width(), metrics.titleHeight());
        Area descriptionArea = Area.of(text.x(), text.y() + metrics.descriptionTop(), text.width(), metrics.descriptionHeight());

        int textColor = active ? theme.text().primary() : theme.text().disabled();
        int mutedColor = active ? theme.text().muted() : theme.text().disabled();

        AbstractWidget.renderScrollingString(graphics, minecraft.font, getMessage(), titleArea.x(), titleArea.y(), titleArea.x2(), titleArea.y2(), textColor);

        Component description = Component.translatableWithFallback(option.descriptionKey, option.descriptionFallback);
        String descriptionText = truncate(minecraft.font, description.getString(), descriptionArea.width());

        graphics.drawString(minecraft.font, descriptionText, descriptionArea.x(), descriptionArea.y(), mutedColor, false);
        controlWidget.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(controlWidget);
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
        return this.theme;
    }

    private static final class OptionControlFactory {
        private OptionControlFactory() {
        }

        public static AbstractWidget createControl(ConfigOption option, XunConfigTheme theme) {
            XunConfigLayout layout = theme.layout();
            Class<?> type = option.type;

            if (type == boolean.class || type == Boolean.class) {
                XunConfigLayout.Toggle metrics = layout.toggle();
                return new ToggleControlWidget(option, theme, 0, 0, metrics.width(), metrics.height());
            }

            if (type.isEnum()) {
                XunConfigLayout.Dropdown metrics = layout.dropdown();
                return new EnumDropdownControlWidget(option, theme, 0, 0, metrics.width(), metrics.height());
            }

            if (Number.class.isAssignableFrom(type) || type == byte.class || type == short.class || type == int.class || type == long.class || type == float.class || type == double.class) {
                XunConfigLayout.Slider metrics = layout.slider();
                return new NumberSliderControlWidget(option, theme, 0, 0, metrics.width(), metrics.height());
            }

            XunConfigLayout.Control metrics = layout.control();
            return new ReadOnlyValueWidget(option, theme, 0, 0, metrics.defaultWidth(), metrics.defaultHeight());
        }
    }
}