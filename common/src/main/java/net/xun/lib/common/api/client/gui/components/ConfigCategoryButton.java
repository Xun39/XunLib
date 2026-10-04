package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.util.GuiDrawUtil;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

public class ConfigCategoryButton extends AbstractButton {
    private final XunConfigTheme theme;
    private final IntConsumer onPress;

    private boolean selected;

    public ConfigCategoryButton(Component message, XunConfigTheme theme, boolean selected, IntConsumer onPress) {
        super(0, 0, 100, 22, message);

        this.theme = theme;
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
        boolean hovered = this.isHoveredOrFocused();

        if (selected) {
            graphics.fill(getX(), getY(), getRight(), getBottom(), theme.sidebarSelected());
            GuiDrawUtil.leftStrip(graphics, getX(), getY(), getBottom(), 2, 3, theme.accent());
        }
        else if (hovered) {
            graphics.fill(getX(), getY(), getRight(), getBottom(), theme.sidebarHover());
        }

        int color = selected ? theme.text() : enabledTextColor();

        AbstractWidget.renderScrollingString(graphics, Minecraft.getInstance().font, getMessage(), getX() + 10, getY() + 6, getRight() - 8, getBottom() - 5, color);
    }

    private int enabledTextColor() {
        return active ? theme.textMuted() : theme.textDisabled();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
