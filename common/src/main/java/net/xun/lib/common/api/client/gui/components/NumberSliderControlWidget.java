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

import java.util.Locale;

public class NumberSliderControlWidget extends AbstractWidget implements IAreaWidget, IThemedConfigGui {
    private final ConfigOption option;
    private final XunConfigTheme theme;

    private double min;
    private double max;
    private double step;

    public NumberSliderControlWidget(ConfigOption option, XunConfigTheme theme, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());

        this.option = option;
        this.theme = theme;

        extractBounds();
        updateActiveState();
    }

    private void extractBounds() {
        min = option.getMin() != null ? option.getMin().doubleValue() : 0.0;
        max = option.getMax() != null ? option.getMax().doubleValue() : 100.0;
        step = option.getStep() != null ? option.getStep().doubleValue() : isIntegerType() ? 1.0 : 0.1;
    }

    private void updateActiveState() {
        active = option.isVisible();
    }

    private boolean isIntegerType() {
        Class<?> type = option.type;
        return type == byte.class || type == Byte.class || type == short.class || type == Short.class || type == int.class || type == Integer.class || type == long.class || type == Long.class;
    }

    @Override
    public XunConfigTheme theme() {
        return theme;
    }

    private double fraction() {
        Number value = option.getValue() instanceof Number number ? number : null;
        double current = value != null ? value.doubleValue() : min;

        if (max <= min) {
            return 0.0;
        }

        return Math.clamp((current - min) / (max - min), 0.0, 1.0);
    }

    private void updateValueFromMouse(double mouseX) {
        var area = bounds();

        if (max <= min || area.width() <= 0) {
            return;
        }

        double fraction = Math.clamp((mouseX - area.x()) / (double) area.width(), 0.0, 1.0);
        double value = min + fraction * (max - min);

        if (step > 0.0) {
            value = Math.round((value - min) / step) * step + min;
        }

        value = Math.clamp(value, min, max);
        writeValue(value);
    }

    private void writeValue(double value) {
        Class<?> type = option.type;

        if (type == byte.class || type == Byte.class) {
            option.setValue((byte) Math.round(value));
        }
        else if (type == short.class || type == Short.class) {
            option.setValue((short) Math.round(value));
        }
        else if (type == int.class || type == Integer.class) {
            option.setValue((int) Math.round(value));
        }
        else if (type == long.class || type == Long.class) {
            option.setValue(Math.round(value));
        }
        else if (type == float.class || type == Float.class) {
            option.setValue((float) value);
        }
        else if (type == double.class || type == Double.class) {
            option.setValue(value);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        updateValueFromMouse(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (!active) {
            return;
        }

        updateValueFromMouse(mouseX);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateActiveState();
        var area = bounds();

        drawSlider(graphics, area, fraction(), active);

        Font font = Minecraft.getInstance().font;
        String valueText = formatValue(option.getValue());

        int textColor = active ? theme.text().primary() : theme.text().disabled();
        int textX = area.centerX() - font.width(valueText) / 2;
        int textY = area.centerY() - font.lineHeight / 2 + 1;

        graphics.drawString(font, valueText, textX, textY, textColor, true);
    }

    private String formatValue(Object value) {
        if (!(value instanceof Number number)) {
            return "0";
        }

        if (isIntegerType()) {
            return String.valueOf(number.longValue());
        }

        return String.format(Locale.ROOT, "%.2f", number.doubleValue());
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    @Override
    public AbstractWidget widget() {
        return this;
    }
}