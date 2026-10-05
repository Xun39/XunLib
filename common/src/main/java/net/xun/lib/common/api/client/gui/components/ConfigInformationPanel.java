package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.IThemedConfigGui;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigLayout;
import net.xun.lib.common.api.config.XunConfigOwner;
import net.xun.lib.common.api.config.XunConfigTheme;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

public class ConfigInformationPanel extends AbstractWidget implements IAreaWidget, IThemedConfigGui {
    private final Font font;
    private final XunConfigTheme theme;
    private final XunConfigOwner owner;

    public ConfigInformationPanel(Font font, XunConfigTheme theme, XunConfigOwner owner, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());

        this.font = font;
        this.theme = theme;
        this.owner = owner;

        active = false;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Area area = bounds();

        XunConfigLayout.Information metrics = layout().information();

        fill(graphics, area, theme.card().background());
        drawBorder(graphics, area, theme.card().border());

        ConfigOption option = owner.getHoveredOption();

        Component title;
        Component description;

        if (option == null) {
            title = Component.translatableWithFallback("gui.xunlib.config.information", "Information");
            description = Component.translatableWithFallback("gui.xunlib.config.hover_option", "Hover over an option to view its details.");
        }
        else {
            title = option.getDisplayName();

            description = option.isVisible()
                    ? option.getDescription()
                    : Component.translatableWithFallback(
                            "gui.xunlib.config.disabled_by_dependency",
                            "Disabled by dependency setting: " + (option.dependsOnField != null ? option.dependsOnField : "unknown")
            );
        }

        int titleX = Math.max(0, area.width() - metrics.paddingLeft() - metrics.paddingRight());
        Area titleArea = Area.of(
                area.x() + metrics.paddingLeft(),
                area.y() + metrics.titleTop(),
                titleX,
                font.lineHeight
        );
        Area descriptionArea = Area.of(
                area.x() + metrics.paddingLeft(),
                area.y() + metrics.descriptionTop(),
                titleX,
                Math.max(0, area.height() - metrics.descriptionTop())
        );

        graphics.drawString(font, title, titleArea.x(), titleArea.y(), theme.accent().primary(), false);
        graphics.drawWordWrap(font, description, descriptionArea.x(), descriptionArea.y(), descriptionArea.width(), theme.text().muted());


        if (option != null && option.isVisible()) {
            Area separator = Area.of(descriptionArea.x(), area.y() + metrics.typeSeparatorTop(), descriptionArea.width(), metrics.typeSeparatorHeight());

            fill(graphics, separator, theme.panel().border());

            graphics.drawString(
                    font, Component.translatableWithFallback("gui.xunlib.config.option_type", "Type"),
                    area.x() + metrics.paddingLeft(),
                    area.y() + metrics.typeLabelTop(),
                    theme.text().muted(),
                    false
            );

            graphics.drawString(font, option.type.getSimpleName(), area.x() + metrics.paddingLeft(), area.y() + metrics.typeValueTop(), theme.text().primary(), false);
        }
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