package net.xun.lib.common.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VisibleWhen {
    /**
     * Field name in the same config class that this option depends on
     */
    String field();

    /**
     * Expected string value of the parent field required to enable this option
     */
    String is() default "true";
}