package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.config.XunConfigOwner;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigTheme;
import net.xun.lib.common.api.client.util.GuiDrawUtil;
import org.jetbrains.annotations.NotNull;

public class ConfigInformationPanel extends AbstractWidget {
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
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(getX(), getY(), getRight(), getBottom(), theme.cardBackground());

        GuiDrawUtil.border(graphics, getX(), getY(), getRight(), getBottom(), theme.cardBorder());

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
                    "Disabled by dependency setting: " + (option.dependsOnField != null ? option.dependsOnField : "unknown"));
        }

        graphics.drawString(font, title, getX() + 10, getY() + 12, theme.accent(), false);

        graphics.drawWordWrap(font, description, getX() + 10, getY() + 34, getWidth() - 20, theme.textMuted());

//        if (option != null && option.isVisible()) {
//            graphics.fill(getX() + 10, getY() + 64, getRight() - 10, getY() + 65, theme.panelBorder());
//
//            graphics.drawString(font, Component.translatableWithFallback("xunlib.gui.config.type", "Type"), getX() + 10, getY() + 75, theme.textMuted(), false);
//
//            graphics.drawString(font, option.type.getSimpleName(), getX() + 10, getY() + 89, theme.text(), false);
//        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
    }
}