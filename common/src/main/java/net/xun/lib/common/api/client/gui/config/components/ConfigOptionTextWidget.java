package net.xun.lib.common.api.client.gui.config.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.config.IThemedConfigGui;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.client.gui.config.XunConfigTheme;
import net.xun.lib.common.api.client.gui.config.layout.CardLayout;
import org.jetbrains.annotations.NotNull;

public class ConfigOptionTextWidget extends AbstractWidget implements IThemedConfigGui {
    private final ConfigOption option;
    private final XunConfigTheme theme;

    public ConfigOptionTextWidget(ConfigOption option, XunConfigTheme theme) {
        super(0, 0, 0, 0, option.getDisplayName());

        this.option = option;
        this.theme = theme;

        this.active = false;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        CardLayout metrics = layout().card();

        boolean enabled = option.isVisible();

        int titleColor = enabled ? theme.text().primary() : theme.text().disabled();
        int descriptionColor = enabled ? theme.text().muted() : theme.text().disabled();
        int titleY = getY() + metrics.titleTop();
        int titleBottom = titleY + metrics.titleHeight();

        AbstractWidget.renderScrollingString(graphics, minecraft.font, getMessage(), getX(), titleY, getX() + width, titleBottom, titleColor);

        Component description = Component.translatableWithFallback(option.descriptionKey, option.descriptionFallback);
        String descriptionText = truncate(minecraft.font, description.getString(), width);

        graphics.drawString(minecraft.font, descriptionText, getX(), getY() + metrics.descriptionTop(), descriptionColor, false);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    @Override
    public XunConfigTheme theme() {
        return theme;
    }
}