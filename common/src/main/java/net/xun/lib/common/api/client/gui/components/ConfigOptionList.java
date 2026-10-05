package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.xun.lib.common.api.client.gui.XunConfigScreen;
import net.xun.lib.common.api.config.ConfigOption;
import net.xun.lib.common.api.config.XunConfigOwner;
import net.xun.lib.common.api.config.XunConfigTheme;

import java.util.List;

public class ConfigOptionList extends AbstractScrollableConfigList<ConfigOptionList.Entry> {
    private final XunConfigOwner owner;

    public ConfigOptionList(XunConfigScreen owner, XunConfigTheme theme, Minecraft minecraft, int x, int y, int width, int height) {
        super(minecraft, width, height, y, theme.layout().card().rowHeight(), theme);

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

    public EnumDropdownControlWidget getOpenDropdown() {
        for (Entry entry : children()) {
            ConfigOptionCard card = entry.getWidget();

            if (card.controlWidget() instanceof EnumDropdownControlWidget dropdown && dropdown.isExpanded()) {
                return dropdown;
            }
        }

        return null;
    }

    public static final class Entry extends SingleWidgetEntry<Entry, ConfigOptionCard> {
        Entry(XunConfigOwner owner, ConfigOption option, XunConfigTheme theme) {
            super(new ConfigOptionCard(owner, option, theme, theme.layout().card().height()), theme.layout().card().entryPaddingY());
        }
    }
}