package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.xun.lib.common.api.client.gui.XunConfigScreen;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigOwner;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigOptionList extends AbstractScrollableConfigList<ConfigOptionList.Entry> {
    private final XunConfigOwner owner;

    public ConfigOptionList(XunConfigScreen owner, XunConfigTheme theme, Minecraft minecraft, int x, int y, int width, int height) {
        super(minecraft, width, height, y, 44, theme);

        this.owner = owner;

        setX(x);
    }

    public void rebuild(List<ConfigOption> options) {
        clearEntries();

        for (ConfigOption option : options) {
            addEntry(new Entry(owner, option, theme));
        }

        setScrollAmount(0);
    }

    public static final class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        private final ConfigOptionButton button;

        Entry(XunConfigOwner owner, ConfigOption option, XunConfigTheme theme) {
            this.button = new ConfigOptionButton(owner, option, theme);
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            button.active = button.isActiveForOption();
            button.setRectangle(width, height - 4, left, top + 2);
            button.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(button);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(button);
        }
    }
}
