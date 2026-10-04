package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.util.GuiDrawUtil;
import net.xun.lib.common.api.config.XunConfigOwner;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

public class ConfigPanel extends AbstractWidget {
    private final Font font;
    private final XunConfigTheme theme;
    private final XunConfigOwner owner;

    public ConfigPanel(Font font, XunConfigTheme theme, XunConfigOwner owner, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());

        this.font = font;
        this.theme = theme;
        this.owner = owner;

        active = false;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        GuiDrawUtil.dropShadow(graphics, getX(), getY(), getRight(), getBottom(), theme.panelShadow());
        GuiDrawUtil.gradientPanel(graphics, getX(), getY(), getRight(), getBottom(), theme.panelTop(), theme.panelBottom(), theme.panelBorder());
        graphics.drawString(font, owner.getTitle(), getX() + 16, getY() + 12, theme.text(), false);
        graphics.drawString(
                font,
                Component.translatableWithFallback(
                        "gui.xunlib.config.configure_description",
                        "Configure this mod's settings"
                ),
                getX() + 16, getY() + 26, theme.textMuted(), false
        );
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
    }
}
