package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.IThemedConfigGui;
import net.xun.lib.common.api.config.XunConfigLayout;
import net.xun.lib.common.api.config.XunConfigOwner;
import net.xun.lib.common.api.config.XunConfigTheme;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

public class ConfigPanel extends AbstractWidget implements IAreaWidget, IThemedConfigGui {
    private static final Component DESCRIPTION = Component.translatableWithFallback(
            "gui.xunlib.config.configure_description", "Configure this mod's settings"
    );

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
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Area area = bounds();
        XunConfigLayout.Panel metrics = layout().panel();

        drawShadow(graphics, area);
        drawGradientPanel(graphics, area, theme.panel().top(), theme.panel().bottom(), theme.panel().border());

        graphics.drawString(font, owner.getTitle(), area.x() + metrics.titleLeft(), area.y() + metrics.titleTop(), theme.text().primary(), false);
        graphics.drawString(font, DESCRIPTION, area.x() + metrics.descriptionLeft(), area.y() + metrics.descriptionTop(), theme.text().muted(), false);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
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