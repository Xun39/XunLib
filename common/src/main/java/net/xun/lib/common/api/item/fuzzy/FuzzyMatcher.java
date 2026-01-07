package net.xun.lib.common.api.item.fuzzy;

import net.minecraft.world.item.ItemStack;
import net.xun.lib.common.api.exceptions.InvalidMatcherConfigurationException;
import net.xun.lib.common.api.inventory.predicates.InventoryPredicate;

import java.util.*;

/**
 * Advanced item comparison system with configurable matching rules, supporting both
 * individual item checks and pairwise item comparisons.
 * <p>
 * Instances are immutable and thread-safe. Configure using the fluent methods that
 * return new instances with updated settings.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Durability comparison control</li>
 *   <li>Enchantment matching toggle</li>
 *   <li>Data component filtering (whitelist/blacklist)</li>
 *   <li>Tag-based category matching</li>
 *   <li>Count comparison modes (exact, at-least, ignore)</li>
 *   <li>Custom validation rules via {@link InventoryPredicate}</li>
 *   <li>Empty item handling</li>
 * </ul>
 *
 * <h2>Preconfigured Matchers</h2>
 * <ul>
 *   <li>{@link #BASIC} - Default strict comparison ignoring durability, enchantments, and components</li>
 *   <li>{@link #IGNORE_ALL} - Loose matching only checking item types</li>
 *   <li>{@link #STRICT} - Exact match of all item properties</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 * Basic configuration:
 * <pre>{@code
 * FuzzyMatcher matcher = new FuzzyMatcher(
 *      new FuzzyConfig()
 *          .withIgnoreDurability(true)
 *          .withRequiredTag(ItemTags.SWORDS)
 * );
 * }</pre>
 *
 *
 * @see FuzzyConfig For configuration
 * @see InventoryPredicate For single-item matching rules
 * @see InvalidMatcherConfigurationException For configuration error details
 */
public class FuzzyMatcher {

    /**
     * Preconfigured matcher that ignores durability, enchantments, and all data components.
     * Compares item types and count strictly.
     */
    public static final FuzzyMatcher BASIC = new FuzzyMatcher(
            new FuzzyConfig()
                    .withIgnoreDurability(true)
                    .withIgnoreEnchantments(true)
                    .withComponentFilter(FuzzyConfig.FilterMode.BLACKLIST, Set.of())
    );

    /**
     * Preconfigured matcher that ignores count, durability, enchantments, and all data components.
     * Only compares item types.
     */
    public static final FuzzyMatcher IGNORE_ALL = new FuzzyMatcher(
            new FuzzyConfig()
                    .withIgnoreDurability(true)
                    .withIgnoreEnchantments(true)
                    .withCountMode(FuzzyConfig.CountMode.IGNORE)
                    .withComponentFilter(FuzzyConfig.FilterMode.BLACKLIST, Set.of())
    );

    /**
     * Strict matcher requiring exact match of:
     * <ul>
     *   <li>Item type</li>
     *   <li>Count</li>
     *   <li>Durability</li>
     *   <li>Enchantments</li>
     *   <li>All other data components</li>
     * </ul>
     */
    public static final FuzzyMatcher STRICT = new FuzzyMatcher(new FuzzyConfig());

    private final FuzzyConfig config;

    public FuzzyMatcher(FuzzyConfig config) {
        this.config = config;
    }

    /**
     * Tests if two item stacks match according to the configured rules.
     *
     * <p>Execution order:
     * <ol>
     *   <li>Empty item check (both must be empty or both non-empty)</li>
     *   <li>Core item type/tag verification</li>
     *   <li>Durability comparison (if enabled)</li>
     *   <li>Enchantment comparison (if enabled)</li>
     *   <li>Data component filtering</li>
     *   <li>Count comparison using configured mode</li>
     *   <li>Custom predicate validation</li>
     * </ol>
     *
     * @param a First item stack to compare
     * @param b Second item stack to compare
     * @return true if items match all configured rules, false otherwise
     * @throws InvalidMatcherConfigurationException if matcher contains conflicting rules:
     * <ul>
     *   <li>Custom rules combined with tag requirements</li>
     *   <li>Custom rules used with attribute ignoring</li>
     * </ul>
     * @throws NullPointerException if either input stack is null
     */
    public boolean matches(ItemStack a, ItemStack b) {
        validateConfiguration();

        if (a.isEmpty() != b.isEmpty()) return false;
        if (a.isEmpty()) return true;

        return compareCore(a, b)
                && compareDurability(a, b)
                && compareEnchantments(a, b)
                && compareComponents(a, b)
                && compareCount(a, b)
                && validateCustomRules(a, b);
    }

    private boolean compareCore(ItemStack a, ItemStack b) {
        if (config.requiredTag != null) {
            return a.is(config.requiredTag) && b.is(config.requiredTag);
        }
        return a.getItem() == b.getItem();
    }

    private boolean compareDurability(ItemStack a, ItemStack b) {
        return config.ignoreDurability || a.getDamageValue() == b.getDamageValue();
    }

    private boolean compareEnchantments(ItemStack a, ItemStack b) {
        return config.ignoreEnchantments ||
                Objects.equals(a.getEnchantments(), b.getEnchantments());
    }

    private boolean compareComponents(ItemStack a, ItemStack b) {
        return a.getComponents().stream()
                .filter(entry -> config.componentFilter.test(entry.type()))
                .allMatch(entry ->
                        Objects.equals(entry.type(), b.get(entry.type()))
                );
    }

    private boolean compareCount(ItemStack a, ItemStack b) {
        return switch (config.countMode) {
            case IGNORE -> true;
            case EXACT -> a.getCount() == b.getCount();
            case AT_LEAST -> a.getCount() >= b.getCount();
        };
    }

    private boolean validateCustomRules(ItemStack a, ItemStack b) {
        return config.predicates.stream().allMatch(rule -> rule.test(a) && rule.test(b));
    }

    private void validateConfiguration() {
        if (config.requiredTag != null && !config.predicates.isEmpty()) {
            throw new InvalidMatcherConfigurationException("Cannot combine tag requirements with custom rules");
        }
        if ((config.ignoreDurability || config.ignoreEnchantments || config.countMode != FuzzyConfig.CountMode.IGNORE) &&
                !config.predicates.isEmpty()) {
            throw new InvalidMatcherConfigurationException("Cannot apply custom rules while ignoring attributes");
        }
    }
}
