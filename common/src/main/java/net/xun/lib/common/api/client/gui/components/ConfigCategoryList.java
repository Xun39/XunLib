package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.config.ConfigDefinition;
import net.xun.lib.common.api.config.XunConfigOwner;
import net.xun.lib.common.api.config.XunConfigTheme;

import java.util.List;

public class ConfigCategoryList extends AbstractScrollableConfigList<ConfigCategoryList.Entry> {
    private final XunConfigOwner owner;
    private final ConfigDefinition config;

    public ConfigCategoryList(XunConfigOwner owner, ConfigDefinition config, XunConfigTheme theme, Minecraft minecraft, int x, int y, int width, int height) {
        super(minecraft, width, height, y, theme.layout().category().rowHeight(), theme);

        this.owner = owner;
        this.config = config;

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

    public static final class Entry extends SingleWidgetEntry<Entry, ConfigCategoryButton> {
        Entry(XunConfigOwner owner, ConfigDefinition config, XunConfigTheme theme, String categoryKey, int index, boolean selected) {
            super(
                    new ConfigCategoryButton(
                            Component.translatableWithFallback(categoryKey, config.getCategoryFallbackName(categoryKey)),
                            theme, selected, ignored -> owner.selectCategory(index)
                    ), theme.layout().category().entryPaddingY()
            );
        }

        void setSelected(boolean selected) {
            widget.setSelected(selected);
        }
    }
}