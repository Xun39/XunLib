package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.IThemedConfigGui;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

public class ReadOnlyValueWidget extends AbstractWidget implements IAreaWidget, IThemedConfigGui {
    private final ConfigOption option;
    private final XunConfigTheme theme;

    public ReadOnlyValueWidget(ConfigOption option, XunConfigTheme theme, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());

        this.option = option;
        this.theme = theme;

        active = false;
    }

    @Override
    public XunConfigTheme theme() {
        return theme;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        String text = String.valueOf(option.getValue());

        int padding = layout().control().valueRightPadding();
        int rightEdge = bounds().x2() - padding;
        int maxWidth = Math.max(0, bounds().width() - padding);

        drawRightAligned(graphics, font, text, rightEdge, bounds().centerY() - font.lineHeight / 2, theme.text().muted(), maxWidth);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    @Override
    public AbstractWidget widget() {
        return this;
    }
}