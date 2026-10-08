package net.xun.lib.common.api.client.gui.config.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.components.ActionButton;
import net.xun.lib.common.api.client.gui.components.IAreaWidget;
import net.xun.lib.common.api.client.gui.config.IThemedConfigGui;
import net.xun.lib.common.api.client.gui.config.XunConfigTheme;
import net.xun.lib.common.api.client.gui.config.layout.ButtonLayout;
import org.jetbrains.annotations.NotNull;

public class ThemedActionButton extends ActionButton implements IAreaWidget, IThemedConfigGui {
    private final XunConfigTheme theme;

    public ThemedActionButton(XunConfigTheme theme, Component message, Runnable onPress) {
        super(0, 0, theme.layout().button().width(), theme.layout().button().height(), message, onPress);
        this.theme = theme;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        ButtonLayout metrics = layout().button();
        var area = bounds();

        boolean hovered = isHoveredOrFocused();
        int background = hovered ? theme.accent().secondary() : theme.accent().primary();

        fill(graphics, area, background);

        int highlightHeight = metrics.highlightHeight();
        if (highlightHeight > 0) {
            fill(graphics, area.topEdge(highlightHeight), theme.overlay().buttonHighlight());
        }

        int left = area.x() + metrics.textPadding();
        int top = area.y() + metrics.textTop();
        int right = area.x2() - metrics.textPadding();
        int bottom = area.y2() - metrics.textBottom();

        AbstractWidget.renderScrollingString(graphics, Minecraft.getInstance().font, getMessage(), left, top, right, bottom, theme.text().primary());
    }

    @Override
    public AbstractWidget widget() {
        return this;
    }

    @Override
    public XunConfigTheme theme() {
        return this.theme;
    }
}
