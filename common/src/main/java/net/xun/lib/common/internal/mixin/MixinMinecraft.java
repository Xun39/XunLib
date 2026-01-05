package net.xun.lib.common.internal.mixin;

import net.minecraft.client.Minecraft;
import net.xun.lib.common.internal.XunLibConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info) {

        XunLibConstants.LOGGER.info("This line is printed by an example mod common mixin!");
        XunLibConstants.LOGGER.info("MC Version: {}", Minecraft.getInstance().getVersionType());
    }
}
