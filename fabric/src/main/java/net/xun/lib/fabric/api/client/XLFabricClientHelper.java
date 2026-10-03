package net.xun.lib.fabric.api.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.xun.lib.common.api.client.gui.XunConfigScreen;
import net.xun.lib.common.api.config.XunConfigTheme;

public class XLFabricClientHelper {
    /**
     * Downstream mods can return this in their ModMenuApi implementation:
     */
    public static ConfigScreenFactory<?> createScreenFactory(String modId) {
        return parent -> new XunConfigScreen(parent, modId);
    }

    public static ConfigScreenFactory<?> createScreenFactory(String modId, XunConfigTheme theme) {
        return parent -> new XunConfigScreen(parent, modId, theme);
    }
}
