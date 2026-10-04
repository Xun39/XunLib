package net.xun.lib.common.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XunConfig {
    String modId();

    ConfigType type() default ConfigType.COMMON;

    String fileName() default "";
}
