package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.IThemedConfigGui;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigLayout;
import net.xun.lib.common.api.config.XunConfigTheme;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

public class EnumDropdownControlWidget extends AbstractWidget implements IAreaWidget, IThemedConfigGui {
    private final ConfigOption option;
    private final XunConfigTheme theme;

    private boolean expanded;

    public EnumDropdownControlWidget(ConfigOption option, XunConfigTheme theme, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());

        this.option = option;
        this.theme = theme;
        this.active = option.isVisible();
    }

    @Override
    public XunConfigTheme theme() {
        return theme;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    private Object[] enumValues() {
        Object[] values = option.type.getEnumConstants();
        return values != null ? values : new Object[0];
    }

    public Area popupArea() {
        XunConfigLayout.Dropdown metrics = layout().dropdown();
        return bounds().offset(0, bounds().height() + metrics.popupGap()).withHeight(enumValues().length * metrics.itemHeight());
    }

    public boolean popupContains(double mouseX, double mouseY) {
        return expanded && popupArea().contains(mouseX, mouseY);
    }

    public boolean handlePopupClick(double mouseX, double mouseY, int button) {
        if (!expanded || button != 0 || !popupContains(mouseX, mouseY)) {
            return false;
        }

        XunConfigLayout.Dropdown metrics = layout().dropdown();
        Area popup = popupArea();

        int index = (int) ((mouseY - popup.y()) / metrics.itemHeight());

        Object[] values = enumValues();

        if (index < 0 || index >= values.length) {
            return false;
        }

        option.setValue(values[index]);
        expanded = false;
        playDownSound(Minecraft.getInstance().getSoundManager());

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || button != 0) {
            return false;
        }

        if (popupContains(mouseX, mouseY)) {
            return handlePopupClick(mouseX, mouseY, button);
        }

        if (!bounds().contains(mouseX, mouseY)) {
            if (expanded) {
                expanded = false;
            }

            return false;
        }

        expanded = !expanded;
        playDownSound(Minecraft.getInstance().getSoundManager());

        return true;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        active = option.isVisible();
        Area button = bounds();

        boolean hovered = active && button.contains(mouseX, mouseY);
        int background = !active ? theme.card().disabled() : hovered || expanded ? theme.card().hover() : theme.card().background();
        int border = !active ? theme.card().disabledBorder() : hovered || expanded ? theme.accent().primary() : theme.card().border();

        fill(graphics, button, background);
        drawBorder(graphics, button, border);

        Font font = Minecraft.getInstance().font;

        XunConfigLayout.Dropdown metrics = layout().dropdown();
        int textPadding = metrics.textPadding();

        String value = String.valueOf(option.getValue());

        String display = truncate(font, value, Math.max(0, button.width() - textPadding * 2 - metrics.chevronSize()));
        int textColor = active ? theme.text().primary() : theme.text().disabled();
        graphics.drawString(font, display, button.x() + textPadding, button.centerY() - font.lineHeight / 2 + 1, textColor, false);

        drawChevron(graphics, button.x() + button.width() - textPadding - metrics.chevronSize(), button.centerY(), expanded, textColor);
    }

    public void renderPopup(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!expanded || !active) {
            return;
        }

        Object[] values = enumValues();
        if (values.length == 0) {
            return;
        }

        XunConfigLayout.Dropdown metrics = layout().dropdown();
        Area popup = popupArea();

        Font font = Minecraft.getInstance().font;

        drawShadow(graphics, popup);
        fill(graphics, popup, theme.card().background());
        drawBorder(graphics, popup, theme.card().border());

        Object current = option.getValue();

        for (int i = 0; i < values.length; i++) {
            Object value = values[i];

            Area item = Area.of(popup.x(), popup.y() + i * metrics.itemHeight(), popup.width(), metrics.itemHeight());

            boolean hovered = item.contains(mouseX, mouseY);
            boolean selected = value.equals(current);

            if (hovered) {
                fill(graphics, item.inset(1), theme.card().hover());
            }
            else if (selected) {
                fill(graphics, item.inset(1), theme.overlay().selection());
            }

            if (selected) {
                drawLeftStrip(graphics, item, layout().decoration().accentStripWidth(), layout().decoration().accentStripInset(), theme.accent().primary());
            }

            int color = hovered ? theme.text().primary() : selected ? theme.accent().primary() : theme.text().muted();
            String text = truncate(font, String.valueOf(value), Math.max(0, item.width() - metrics.textPadding() * 2));
            graphics.drawString(font, text, item.x() + metrics.textPadding(), item.centerY() - font.lineHeight / 2 + 1, color, false);
        }
    }

    private void drawChevron(GuiGraphics graphics, int cx, int cy, boolean up, int color) {
        int size = layout().dropdown().chevronSize();
        int half = Math.max(1, size / 2);

        if (up) {
            graphics.fill(cx - half, cy + half, cx + half + 1, cy + half + 1, color);
            graphics.fill(cx - half + 1, cy, cx + half, cy + 1, color);
            graphics.fill(cx, cy - half, cx + 1, cy, color);
        }
        else {
            graphics.fill(cx - half, cy - half, cx + half + 1, cy - half + 1, color);
            graphics.fill(cx - half + 1, cy, cx + half, cy + 1, color);
            graphics.fill(cx, cy + half, cx + 1, cy + half + 1, color);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    @Override
    public AbstractWidget widget() {
        return this;
    }
}