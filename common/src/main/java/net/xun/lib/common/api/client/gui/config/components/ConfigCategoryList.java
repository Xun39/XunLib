package net.xun.lib.common.api.client.gui.config.components;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.client.gui.components.SingleWidgetEntry;
import net.xun.lib.common.api.client.gui.config.IConfigOwnerScreen;
import net.xun.lib.common.api.client.gui.config.XunConfigTheme;
import net.xun.lib.common.api.config.ConfigDefinition;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ConfigCategoryList extends AbstractThemedConfigList<ConfigCategoryList.Entry> {
    private final IConfigOwnerScreen owner;
    private final ConfigDefinition config;

    /**
     * Categories whose children are currently visible.
     */
    private final Set<String> expandedCategories = new LinkedHashSet<>();

    private String selectedCategoryKey;

    public ConfigCategoryList(IConfigOwnerScreen owner, ConfigDefinition config, XunConfigTheme theme, Minecraft minecraft, int x, int y, int width, int height) {
        super(minecraft, width, height, y, theme.layout().category().rowHeight(), theme);

        this.owner = owner;
        this.config = config;
        setX(x);
    }

    public String rebuild(String requestedSelectedCategory) {
        double oldScroll = getScrollAmount();

        selectedCategoryKey = isValidCategory(requestedSelectedCategory) ? requestedSelectedCategory : firstRootCategory();
        expandAncestors(selectedCategoryKey);

        clearEntries();

        for (String root : config.getRootCategoryKeys()) {
            addVisibleCategory(root);
        }

        setScrollAmount(Math.min(oldScroll, getMaxScroll()));
        return selectedCategoryKey;
    }

    private void addVisibleCategory(String categoryKey) {
        ConfigDefinition.CategoryNode node = config.getCategory(categoryKey);

        if (node == null) {
            return;
        }

        addEntry(new Entry(this, owner, config, theme, node, selectedCategoryKey, expandedCategories.contains(categoryKey)));

        if (expandedCategories.contains(categoryKey)) {
            for (String child : config.getCategoryChildren(categoryKey)) {
                addVisibleCategory(child);
            }
        }
    }

    private void expandAncestors(String categoryKey) {
        if (categoryKey == null) {
            return;
        }

        String parent = config.getCategoryParent(categoryKey);

        while (parent != null) {
            expandedCategories.add(parent);
            parent = config.getCategoryParent(parent);
        }
    }

    private boolean isValidCategory(String categoryKey) {
        return categoryKey != null && config.getCategory(categoryKey) != null;
    }

    private String firstRootCategory() {
        List<String> roots = config.getRootCategoryKeys();

        return roots.isEmpty() ? null : roots.getFirst();
    }

    public String getSelectedCategoryKey() {
        return selectedCategoryKey;
    }

    public void updateSelection(String categoryKey) {
        selectedCategoryKey = categoryKey;

        for (Entry entry : children()) {
            entry.setSelected(entry.categoryKey().equals(categoryKey));
        }
    }

    public void toggleCategory(String categoryKey) {
        if (!config.hasCategoryChildren(categoryKey)) {
            return;
        }

        if (!expandedCategories.add(categoryKey)) {
            expandedCategories.remove(categoryKey);
        }

        rebuild(selectedCategoryKey);
    }

    public boolean isExpanded(String categoryKey) {
        return expandedCategories.contains(categoryKey);
    }

    public void collapseAll() {
        expandedCategories.clear();
        rebuild(selectedCategoryKey);
    }

    public void expandAll() {
        expandedCategories.clear();

        for (String category : config.getCategoryKeys()) {
            if (config.hasCategoryChildren(category)) {
                expandedCategories.add(category);
            }
        }

        rebuild(selectedCategoryKey);
    }

    public static final class Entry extends SingleWidgetEntry<Entry, ConfigCategoryButton> {

        private final String categoryKey;

        Entry(ConfigCategoryList list, IConfigOwnerScreen owner, ConfigDefinition config, XunConfigTheme theme, ConfigDefinition.CategoryNode node, String selectedCategoryKey, boolean expanded) {
            super(new ConfigCategoryButton(
                    Component.translatableWithFallback(node.key(), node.fallback()),
                    theme,
                    node.depth(),
                    config.hasCategoryChildren(node.key()),
                    expanded,
                    node.key().equals(selectedCategoryKey),
                    () -> owner.selectCategory(node.key()), () -> list.toggleCategory(node.key())
                  ), theme.layout().category().entryPaddingY()
            );

            this.categoryKey = node.key();
        }

        public String categoryKey() {
            return categoryKey;
        }

        public void setSelected(boolean selected) {
            widget.setSelected(selected);
        }
    }
}