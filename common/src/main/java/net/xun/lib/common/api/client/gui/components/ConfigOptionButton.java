package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.util.GuiDrawUtil;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigOwner;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

public class ConfigOptionButton extends AbstractThemedConfigButton {
    private final XunConfigOwner owner;
    private final ConfigOption option;

    ConfigOptionButton(XunConfigOwner owner, ConfigOption option, XunConfigTheme theme) {
        super(theme, 100, 40, option.getDisplayName());

        this.owner = owner;
        this.option = option;

        this.active = option.isVisible() && isBoolean();
    }

    private boolean isBoolean() {
        return option.type == boolean.class || option.type == Boolean.class;
    }

    @Override
    public void onPress() {
        if (!option.isVisible() || !isBoolean()) {
            return;
        }

        option.setValue(!Boolean.TRUE.equals(option.getValue()));
    }

    public boolean isActiveForOption() {
        return this.active;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Keep the screen's information panel in sync.
        if (isHovered()) {
            owner.setHoveredOption(option);
        }

        boolean enabled = option.isVisible();
        boolean hovered = enabled && isHoveredOrFocused();

        int background = !enabled ? theme.cardDisabled() : hovered ? theme.cardHover() : theme.cardBackground();

        int borderColor = !enabled ? 0x10FFFFFF : hovered ? theme.accent() : theme.cardBorder();

        GuiDrawUtil.dropShadow(graphics, getX(), getY(), getRight(), getBottom(), theme.panelShadow());
        graphics.fill(getX(), getY(), getRight(), getBottom(), background);
        GuiDrawUtil.border(graphics, getX(), getY(), getRight(), getBottom(), borderColor);
        if (hovered) {
            GuiDrawUtil.leftStrip(graphics, getX(), getY(), getBottom(), 2, 4, theme.accent());
        }

        int textColor = enabled ? theme.text() : theme.textDisabled();
        int mutedColor = enabled ? theme.textMuted() : theme.textDisabled();

        Minecraft minecraft = Minecraft.getInstance();

        int textRight = getRight() - 58;

        // Title
        AbstractWidget.renderScrollingString(graphics, minecraft.font, getMessage(), getX() + 11, getY() + 6, textRight, getY() + 19, textColor);

        // Description
        Component description = Component.translatableWithFallback(option.descriptionKey, option.descriptionFallback);

        String descriptionText = GuiDrawUtil.truncate(minecraft.font, description.getString(), Math.max(30, textRight - (getX() + 11)));

        graphics.drawString(minecraft.font, descriptionText, getX() + 11, getY() + 22, mutedColor, false);

        if (isBoolean()) {
            GuiDrawUtil.toggle(graphics, getRight() - 44, getY() + 11, 34, 16, Boolean.TRUE.equals(option.getValue()), enabled, theme);
        }
        else {
            GuiDrawUtil.drawRightAligned(graphics, minecraft.font, String.valueOf(option.getValue()), getRight() - 12, getY() + 14, mutedColor, 0);
        }
    }
}
