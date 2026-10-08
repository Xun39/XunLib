package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.gui.components.AbstractWidget;
import net.xun.lib.common.api.util.Area;

public interface IAreaWidget {
    AbstractWidget widget();

    default Area bounds() {
        AbstractWidget widget = widget();

        return Area.of(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
    }

    default void setBounds(Area area) {
        widget().setRectangle(area.width(), area.height(), area.x(), area.y());
    }

    default boolean contains(double mouseX, double mouseY) {
        return bounds().contains(mouseX, mouseY);
    }
}
