package net.xun.lib.common.api.util;

import net.minecraft.network.chat.Component;
import net.xun.lib.common.internal.ModIDManager;

public class TranslationUtil {
    private TranslationUtil() {
    }

    public static String translationKey(String... parts) {
        return String.join(".", parts);
    }

    public static Component translatable(String type, String key) {
        return Component.translatable(translationKey(type, ModIDManager.getModId(), key));
    }

    public static Component translatable(String type, String namespace, String key) {
        return Component.translatable(translationKey(type, namespace, key));
    }
}
