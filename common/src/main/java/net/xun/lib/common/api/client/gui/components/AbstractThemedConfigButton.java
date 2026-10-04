package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractThemedConfigButton extends AbstractButton {
    protected final XunConfigTheme theme;

    protected AbstractThemedConfigButton(XunConfigTheme theme, int w, int h, Component msg) {
        super(0, 0, w, h, msg);
        this.theme = theme;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput out) {
        defaultButtonNarrationText(out);
    }
}
