package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.IThemedConfigGui;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigLayout;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

public class ConfigCategoryButton extends AbstractThemedConfigButton implements IAreaWidget, IThemedConfigGui {
    private final IntConsumer onPress;
    private boolean selected;

    public ConfigCategoryButton(Component message, XunConfigTheme theme, boolean selected, IntConsumer onPress) {
        super(theme, theme.layout().category().width(), theme.layout().category().height(), message);

        this.selected = selected;
        this.onPress = onPress;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void onPress() {
        onPress.accept(0);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        XunConfigLayout.Category metrics = layout().category();
        var area = bounds();

        boolean hovered = area.contains(mouseX, mouseY);

        if (selected) {
            fill(graphics, area, theme.sidebar().selected());

            drawLeftStrip(graphics, area, metrics.selectedStripWidth(), metrics.selectedStripInset(), theme.accent().primary());
        }
        else if (hovered) {
            fill(graphics, area, theme.sidebar().hover());
        }

        int textColor = selected ? theme.text().primary() : active ? theme.text().muted() : theme.text().disabled();

        int left = area.x() + metrics.textLeft();
        int top = area.y() + metrics.textTop();
        int right = area.x2() - metrics.textRight();
        int bottom = area.y2() - metrics.textBottom();

        AbstractWidget.renderScrollingString(graphics, Minecraft.getInstance().font, getMessage(), left, top, right, bottom, textColor);
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