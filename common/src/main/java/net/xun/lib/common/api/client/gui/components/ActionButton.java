package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.config.XunConfigLayout;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

public class ActionButton extends AbstractThemedConfigButton implements IAreaWidget {
    private final Runnable onPress;

    public ActionButton(Component message, XunConfigTheme theme, Runnable onPress) {
        super(theme, theme.layout().button().width(), theme.layout().button().height(), message);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        onPress.run();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        XunConfigLayout.Button metrics = layout().button();
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
}