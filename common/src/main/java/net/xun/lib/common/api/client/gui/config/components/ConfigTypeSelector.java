package net.xun.lib.common.api.client.gui.config.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.components.IAreaWidget;
import net.xun.lib.common.api.client.gui.config.IConfigOwnerScreen;
import net.xun.lib.common.api.client.gui.config.IThemedConfigGui;
import net.xun.lib.common.api.config.ConfigType;
import net.xun.lib.common.api.client.gui.config.XunConfigTheme;
import net.xun.lib.common.api.util.Area;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumSet;

public class ConfigTypeSelector extends AbstractWidget implements IAreaWidget, IThemedConfigGui {
    private static final ConfigType[] TYPES = { ConfigType.COMMON, ConfigType.CLIENT, ConfigType.SERVER };

    private final Font font;
    private final IConfigOwnerScreen owner;
    private final XunConfigTheme theme;
    private final EnumSet<ConfigType> availableTypes;

    private ConfigType selectedType;

    public ConfigTypeSelector(IConfigOwnerScreen owner, Font font, XunConfigTheme theme, int x, int y, int width, int height, Collection<ConfigType> availableTypes, ConfigType selectedType) {
        super(x, y, width, height, Component.translatableWithFallback("gui.xunlib.config.type", "Configuration Type"));

        this.owner = owner;
        this.font = font;
        this.theme = theme;

        this.availableTypes = availableTypes.isEmpty() ? EnumSet.noneOf(ConfigType.class) : EnumSet.copyOf(availableTypes);
        this.selectedType = selectedType;
    }

    public ConfigType getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(ConfigType type) {
        if (!availableTypes.contains(type) || selectedType == type) {
            return;
        }

        selectedType = type;
        owner.selectConfigType(type);
    }

    public boolean isAvailable(ConfigType type) {
        return availableTypes.contains(type);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int segmentWidth = width / TYPES.length;

        for (int i = 0; i < TYPES.length; i++) {
            ConfigType type = TYPES[i];

            int left = getX() + i * segmentWidth;
            int right = i == TYPES.length - 1 ? getX() + width : left + segmentWidth;

            boolean available = availableTypes.contains(type);
            boolean selected = type == selectedType;
            boolean hovered = available && mouseX >= left && mouseX < right && mouseY >= getY() && mouseY < getY() + height;

            int background = getBackgroundColor(available, selected, hovered);

            graphics.fill(left, getY(), right, getY() + height, background);

            drawBorder(graphics, Area.fromCorners(left, getY(), right, getY() + height), selected ? theme.accent().primary() : theme.card().border());

            Component label = getTypeName(type);

            int textColor = getTextColor(available, selected);

            int textWidth = font.width(label);
            int textX = left + (right - left - textWidth) / 2;
            int textY = getY() + (height - font.lineHeight) / 2;

            graphics.drawString(font, label, textX, textY, textColor);
        }
    }

    private int getBackgroundColor(boolean available, boolean selected, boolean hovered) {
        if (!available) {
            return theme.card().disabled();
        }

        if (selected) {
            return theme.sidebar().selected();
        }

        if (hovered) {
            return theme.card().hover();
        }

        return theme.card().background();
    }

    private int getTextColor(boolean available, boolean selected) {
        if (!available) {
            return theme.text().disabled();
        }

        if (selected) {
            return theme.text().primary();
        }

        return theme.text().muted();
    }

    private static Component getTypeName(ConfigType type) {
        return Component.translatableWithFallback(
                "gui.xunlib.config.type." + type.name().toLowerCase(), switch (type) {
                    case COMMON -> "Common";
                    case CLIENT -> "Client";
                    case SERVER -> "Server";
                }
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || button != 0 || !isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int segmentWidth = width / TYPES.length;
        int index = Math.min(TYPES.length - 1, Math.max(0, (int) ((mouseX - getX()) / segmentWidth)));

        ConfigType type = TYPES[index];

        if (!availableTypes.contains(type)) {
            return false;
        }

        setSelectedType(type);

        Minecraft minecraft = Minecraft.getInstance();
        playDownSound(minecraft.getSoundManager());

        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, owner.getTitle());
        output.add(
                NarratedElementType.USAGE,
                Component.translatable("gui.xunlib.config.type.selected", getTypeName(selectedType))
        );
    }

    @Override
    public AbstractWidget widget() {
        return this;
    }

    @Override
    public XunConfigTheme theme() {
        return this.theme;
    }
}