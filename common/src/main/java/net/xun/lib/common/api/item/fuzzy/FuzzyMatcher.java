package net.xun.lib.common.api.item.fuzzy;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.xun.lib.common.api.inventory.predicates.InventoryPredicate;
import net.xun.lib.common.api.item.InvalidMatcherConfigurationException;

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
 *   <li>Durability/enchantment/data component control</li>
 *   <li>Tag-based category matching</li>
 *   <li>Single-item predicates ({@link InventoryPredicate})</li>
 *   <li>Data component whitelisting/blacklisting</li>
 *   <li>Empty item handling</li>
 *   <li>Count comparison modes</li>
 * </ul>
 *
 * <h2>Preconfigured Matchers</h2>
 * <ul>
 *   <li>{@link #BASIC} - Ignores durability, enchantments, and all data components</li>
 *   <li>{@link #IGNORE_ALL} - Ignores count, durability, enchantments, and data</li>
 *   <li>{@link #STRICT} - Requires exact match of all properties</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 * Basic configuration:
 * <pre>{@code
 * FuzzyMatcher matcher = FuzzyMatcher.create()
 *     .ignoreDurability()
 *     .whitelistDataComponents(DataComponents.LORE)
 *     .withRequiredTag(ItemTags.ARROWS);
 * }</pre>
 *
 * @see InventoryPredicate For single-item matching rules
 * @see #create() Entry point for configuration
 */
public class FuzzyMatcher {

    /**
     * Preconfigured matcher that ignores durability, enchantments, and all data components.
     * Compares item types and count strictly.
     */
    public static final FuzzyMatcher BASIC = FuzzyMatcher.create()
            .ignoreDurability()
            .ignoreEnchantments()
            .ignoreAllDataComponents();

    /**
     * Preconfigured matcher that ignores count, durability, enchantments, and all data components.
     * Only compares item types.
     */
    public static final FuzzyMatcher IGNORE_ALL = FuzzyMatcher.create()
            .ignoreCount()
            .ignoreDurability()
            .ignoreEnchantments()
            .ignoreAllDataComponents();

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
    public static final FuzzyMatcher STRICT = FuzzyMatcher.create();

    private final boolean ignoreDurability;
    private final boolean ignoreEnchantments;
    private final boolean ignoreDataComponent;
    private final boolean ignoreCount;
    private final List<InventoryPredicate> customRules;
    private final TagKey<Item> requiredTag;
    private final Set<DataComponentType<?>> componentWhitelist;

    /**
     * Creates a new FuzzyMatcher with default strict settings.
     *
     * <p>Default configuration:
     * <ul>
     *   <li>Compare durability: enabled</li>
     *   <li>Compare enchantments: enabled</li>
     *   <li>Compare data components: all</li>
     *   <li>Compare count: enabled</li>
     *   <li>No custom rules</li>
     * </ul>
     */
    public static FuzzyMatcher create() {
        return new FuzzyMatcher(
                false, // ignoreDurability
                false, // ignoreEnchantments
                false, // ignoreDataComponent
                false, // ignoreCount
                List.of(),
                null,
                Set.of()
        );
    }

    private FuzzyMatcher(boolean ignoreDurability,
                         boolean ignoreEnchantments,
                         boolean ignoreDataComponent,
                         boolean ignoreCount,
                         List<InventoryPredicate> customRules,
                         TagKey<Item> requiredTag,
                         Set<DataComponentType<?>> nbtWhitelist) {
        this.ignoreDurability = ignoreDurability;
        this.ignoreEnchantments = ignoreEnchantments;
        this.ignoreDataComponent = ignoreDataComponent;
        this.ignoreCount = ignoreCount;
        this.customRules = List.copyOf(customRules);
        this.requiredTag = requiredTag;
        this.componentWhitelist = Set.copyOf(nbtWhitelist);
    }

    // ================== CONFIGURATION METHODS ================== //

    /**
     * Configures the matcher to ignore item durability values.
     *
     * @return New matcher instance ignoring durability
     */
    public FuzzyMatcher ignoreDurability() {
        return new FuzzyMatcher(
                true, this.ignoreEnchantments, this.ignoreDataComponent, this.ignoreCount,
                this.customRules, this.requiredTag, this.componentWhitelist
        );
    }

    /**
     * Configures the matcher to ignore item enchantments.
     *
     * @return New matcher instance ignoring enchantments
     */
    public FuzzyMatcher ignoreEnchantments() {
        return new FuzzyMatcher(
                this.ignoreDurability, true, this.ignoreDataComponent, this.ignoreCount,
                this.customRules, this.requiredTag, this.componentWhitelist
        );
    }

    /**
     * Configures the matcher to ignore all data components.
     *
     * @return New matcher instance ignoring data components
     */
    public FuzzyMatcher ignoreAllDataComponents() {
        return new FuzzyMatcher(
                this.ignoreDurability,
                this.ignoreEnchantments,
                true,
                this.ignoreCount,
                this.customRules,
                this.requiredTag,
                Set.of()
        );
    }

    /**
     * Whitelists specific data components to compare, ignoring all others.
     *
     * <p>Example:
     * <pre>{@code
     * .whitelistDataComponents(DataComponents.LORE, DataComponents.CUSTOM_MODEL_DATA)
     * }</pre>
     *
     * @param whitelist Data components to compare
     * @return New matcher instance with component whitelist
     */
    public FuzzyMatcher whitelistDataComponents(DataComponentType<?>... whitelist) {
        return new FuzzyMatcher(
                this.ignoreDurability,
                this.ignoreEnchantments,
                false, // Ensure data components are compared
                this.ignoreCount,
                this.customRules,
                this.requiredTag,
                new HashSet<>(Arrays.asList(whitelist))
        );
    }

    /**
     * Configures the matcher to ignore item count.
     *
     * @return New matcher instance ignoring count
     */
    public FuzzyMatcher ignoreCount() {
        return new FuzzyMatcher(
                this.ignoreDurability, this.ignoreEnchantments, this.ignoreDataComponent, true,
                this.customRules, this.requiredTag, this.componentWhitelist
        );
    }

    /**
     * Adds a custom predicate that both items must satisfy individually.
     * <p>
     * The predicate is applied to each item separately. Both items must pass
     * the predicate for the match to succeed.
     *
     * <p>Example - match unbreakable items:
     * <pre>{@code
     * .withRule(stack -> stack.getComponents().contains(DataComponents.UNBREAKABLE))
     * }</pre>
     *
     * @param rule The predicate to apply to both items
     * @return A new FuzzyMatcher instance with the added rule
     * @see InventoryPredicate Predefined common predicates
     */
    public FuzzyMatcher withRule(InventoryPredicate rule) {
        List<InventoryPredicate> newRules = new ArrayList<>(this.customRules);
        newRules.add(rule);
        return new FuzzyMatcher(
                this.ignoreDurability, this.ignoreEnchantments, this.ignoreDataComponent, this.ignoreCount,
                newRules, this.requiredTag, this.componentWhitelist
        );
    }

    /**
     * Requires both items to belong to the specified tag category.
     * <p>
     * This is a convenience method for tag-based matching. Equivalent to:
     * <pre>{@code
     * .withRule(InventoryPredicate.matchesTag(tag))
     * }</pre>
     *
     * @param tag The item tag both items must belong to
     * @return A new FuzzyMatcher instance with tag requirement
     * @see InventoryPredicate#matchesTag(TagKey)
     */
    public FuzzyMatcher withRequiredTag(TagKey<Item> tag) {
        return new FuzzyMatcher(
                this.ignoreDurability, this.ignoreEnchantments, this.ignoreDataComponent, this.ignoreCount,
                this.customRules, tag, this.componentWhitelist
        );
    }

    // ================== CORE MATCHING LOGIC ================== //

    /**
     * Tests if two item stacks match according to the configured rules.
     *
     * <p>Execution order:
     * <ol>
     *   <li>Empty check (both must be empty or both non-empty)</li>
     *   <li>Core item type/tag verification</li>
     *   <li>Durability comparison (if enabled)</li>
     *   <li>Enchantment comparison (if enabled)</li>
     *   <li>Data component check (whitelist or full comparison)</li>
     *   <li>Custom predicate validation</li>
     *   <li>Pairwise rule evaluation</li>
     * </ol>
     *
     * @param a First item stack to compare
     * @param b Second item stack to compare
     * @return true if items match under current configuration, false otherwise
     * @throws InvalidMatcherConfigurationException If matcher has conflicting rules
     * @throws NullPointerException If either stack is null
     */
    public boolean matches(ItemStack a, ItemStack b) {
        validateConfiguration();

        // Handle empty items
        if (a.isEmpty() != b.isEmpty()) return false;
        if (a.isEmpty()) return true;

        // Core item comparison
        if (!compareItems(a, b)) return false;

        // State-dependent comparisons
        if (!ignoreDurability && a.getDamageValue() != b.getDamageValue()) return false;
        if (!ignoreCount && a.getCount() != b.getCount()) return false;
        if (!ignoreEnchantments && !compareEnchantments(a, b)) return false;
        if (!ignoreDataComponent && !compareComponents(a, b)) return false;

        return validateCustomRules(a, b);
    }

    private boolean compareItems(ItemStack a, ItemStack b) {
        if (requiredTag != null) {
            return a.is(requiredTag) && b.is(requiredTag);
        }
        return a.getItem() == b.getItem();
    }

    private boolean compareEnchantments(ItemStack a, ItemStack b) {
        return Objects.equals(a.getEnchantments(), b.getEnchantments());
    }

    private boolean compareComponents(ItemStack a, ItemStack b) {
        if (componentWhitelist.isEmpty()) {
            return Objects.equals(a.getComponents(), b.getComponents());
        }

        return componentWhitelist.stream().allMatch(componentType ->
                Objects.equals(a.get(componentType), b.get(componentType))
        );
    }

    private boolean validateCustomRules(ItemStack a, ItemStack b) {
        return customRules.stream().allMatch(rule ->
                rule.test(a) && rule.test(b)
        );
    }

    // ================== VALIDATION & UTILITIES ================== //

    private void validateConfiguration() {
        if (requiredTag != null && !customRules.isEmpty()) {
            throw new InvalidMatcherConfigurationException("Cannot combine tag requirements with custom rules");
        }
        if ((!ignoreCount || !ignoreDurability || !ignoreEnchantments) && !customRules.isEmpty()) {
            throw new InvalidMatcherConfigurationException("Cannot apply custom rules while ignoring attributes");
        }
    }
}
