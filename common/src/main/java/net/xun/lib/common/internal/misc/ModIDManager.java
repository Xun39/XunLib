package net.xun.lib.common.internal.misc;

import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

@ApiStatus.Internal
public class ModIDManager {

    private static String modId = null;

    public static String getModId() {
        if (modId == null) {
            modId = autoDetectModId();
            if (modId == null) {
                throw new IllegalStateException("Mod ID not set and auto-detection failed. Call ModSetup.setModId() during mod initialization.");
            }
        }
        return modId;
    }

    public static void setModId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Mod ID cannot not be null or empty");
        }
        modId = id.toLowerCase(Locale.ROOT);
    }


    private static String autoDetectModId() {
        try {
            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                String className = element.getClassName();

                String packageName = Class.forName(className).getPackageName();
                String[] parts = packageName.split("\\.");
                if (parts.length >= 2) return parts[1];
            }
        } catch (ClassNotFoundException ignored) {}
        return null;
    }
}
