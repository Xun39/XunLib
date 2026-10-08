package net.xun.lib.common.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a nested configuration group.
 *
 * <p>Configuration groups form the category hierarchy displayed by the
 * XunLib configuration screen.</p>
 *
 * <p>The Java field name determines the group's path. The optional
 * {@link #category()} value determines the group's display/fallback name.</p>
 *
 * <p>For example:</p>
 *
 * <pre>
 * {@code
 * @ConfigGroup(category = "worldgen")
 * public final WorldgenConfig worldgen = new WorldgenConfig();
 * }
 * </pre>
 *
 * <p>creates the category:</p>
 *
 * <pre>
 * worldgen
 * </pre>
 *
 * <p>A nested group such as {@code generateStructures} inside {@code worldgen}
 * becomes:</p>
 *
 * <pre>
 * worldgen.generate_structures
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigGroup {

    /**
     * Display/fallback name for this group.
     *
     * <p>An empty value uses a humanized version of the Java field name.</p>
     */
    String category() default "";
}
