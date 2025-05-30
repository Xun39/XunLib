package net.xun.lib.fabric.internal.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.xun.lib.common.internal.XunLibConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    
    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        
        XunLibConstants.LOGGER.info("This line is printed by an example mod mixin from Fabric!");
        XunLibConstants.LOGGER.info("MC Version: {}", Minecraft.getInstance().getVersionType());
    }
}