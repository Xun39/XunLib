package net.xun.lib.common.api;

import net.xun.lib.common.internal.misc.ModIDManager;

public class ModSetup {

    /**
     * Set your mod ID,
     * <p>
     *     used in {@link net.xun.lib.common.api.util.CommonUtils} for all mod ID
     *     related methods
     * @param modId Your mod ID
     */
    public static void setModId(String modId) {
        ModIDManager.setModId(modId);
    }
}
