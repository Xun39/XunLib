package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.config.XunConfigTheme;

public class ActionButton extends AbstractThemedConfigButton {
    private final Runnable onPress;

    public ActionButton(Component message, XunConfigTheme theme, Runnable onPress) {
        super(theme, 76, 22, message);

        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        onPress.run();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = isHoveredOrFocused();

        int background = hovered ? theme.accentSecondary() : theme.accent();

        graphics.fill(getX(), getY(), getRight(), getBottom(), background);
        graphics.fill(getX(), getY(), getRight(), getY() + 1, 0x45FFFFFF);

        AbstractWidget.renderScrollingString(graphics, Minecraft.getInstance().font, getMessage(), getX() + 8, getY() + 6, getRight() - 8, getBottom() - 5, theme.text());
    }
}
