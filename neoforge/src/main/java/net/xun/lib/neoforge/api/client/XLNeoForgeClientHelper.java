package net.xun.lib.neoforge.api.client;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.xun.lib.common.api.client.gui.XunConfigScreen;
import net.xun.lib.common.api.config.XunConfigTheme;

public class XLNeoForgeClientHelper {
    /**
     * Call this in your downstream mod's client initialization constructor:
     * XunNeoForgeClientHelper.registerConfigScreen("your_modid");
     */
    public static void registerConfigScreen(String modId) {
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (container, parent) -> new XunConfigScreen(parent, modId)
        );
    }

    public static void registerConfigScreen(String modId, XunConfigTheme theme) {
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (container, parent) -> new XunConfigScreen(parent, modId, theme)
        );
    }
}
