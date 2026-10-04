package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.config.ConfigDefinition;
import net.xun.lib.common.api.config.XunConfigOwner;
import net.xun.lib.common.api.config.XunConfigTheme;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigCategoryList extends ContainerObjectSelectionList<ConfigCategoryList.Entry> {
    private final XunConfigOwner owner;
    private final ConfigDefinition config;
    private final XunConfigTheme theme;

    public ConfigCategoryList(XunConfigOwner owner, ConfigDefinition config, XunConfigTheme theme, Minecraft minecraft, int x, int y, int width, int height) {
        super(minecraft, width, height, y, 28);

        this.owner = owner;
        this.config = config;
        this.theme = theme;

        setX(x);
    }

    public void rebuild(int selectedIndex) {
        clearEntries();

        List<String> categories = config.getCategoryKeys();

        for (int i = 0; i < categories.size(); i++) {
            String categoryKey = categories.get(i);

            addEntry(new Entry(owner, config, theme, categoryKey, i, i == selectedIndex));
        }

        setScrollAmount(0);
    }

    public void updateSelection(int selectedIndex) {
        for (int i = 0; i < children().size(); i++) {
            children().get(i).setSelected(i == selectedIndex);
        }
    }

    @Override
    public int getRowLeft() {
        return getX() + 4;
    }

    @Override
    public int getRowRight() {
        return getX() + getWidth() - 10;
    }

    @Override
    public int getRowWidth() {
        return getWidth() - 14;
    }

    @Override
    protected int getScrollbarPosition() {
        return getX() + getWidth() - 2;
    }

    @Override
    protected void renderListBackground(@NotNull GuiGraphics graphics) {
        // Intentionally empty.
        // The screen provides the surrounding panel.
    }

    @Override
    protected void renderListSeparators(@NotNull GuiGraphics graphics) {
        // Intentionally empty.
    }

    @Override
    protected void renderSelection(@NotNull GuiGraphics graphics, int top, int width, int height, int outerColor, int innerColor) {
        // Selection is represented by CategoryButton itself.
    }

    public static final class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        private final ConfigCategoryButton button;

        Entry(XunConfigOwner owner, ConfigDefinition config, XunConfigTheme theme, String categoryKey, int index, boolean selected) {
            this.button = new ConfigCategoryButton(
                    Component.translatableWithFallback(categoryKey, config.getCategoryFallbackName(categoryKey)),
                    theme,
                    selected,
                    ignored -> owner.selectCategory(index)
            );
        }

        void setSelected(boolean selected) {
            button.setSelected(selected);
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            button.setRectangle(width, height - 4, left, top + 2);
            button.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(button);
        }

        @Override
        public @NotNull List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() {
            return List.of(button);
        }
    }
}
