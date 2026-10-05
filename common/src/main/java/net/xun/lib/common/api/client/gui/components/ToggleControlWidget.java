package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.IThemedConfigGui;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

public class ToggleControlWidget extends AbstractWidget implements IAreaWidget, IThemedConfigGui {
    private final ConfigOption option;
    private final XunConfigTheme theme;

    public ToggleControlWidget(ConfigOption option, XunConfigTheme theme, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.option = option;
        this.theme = theme;
        this.active = option.isVisible();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        boolean currentValue = Boolean.TRUE.equals(option.getValue());
        option.setValue(!currentValue);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        drawToggle(graphics, bounds(), Boolean.TRUE.equals(option.getValue()), option.isVisible());
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        // To add later
    }

    @Override
    public XunConfigTheme theme() {
        return this.theme;
    }

    @Override
    public AbstractWidget widget() {
        return this;
    }
}
