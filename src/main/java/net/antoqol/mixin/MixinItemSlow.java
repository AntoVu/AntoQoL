package net.antoqol.mixin;

import net.antoqol.config.ModConfig;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class MixinItemSlow {

    // aiStep checks isUsingItem to cut movement input to 20% and to block starting a sprint;
    // item use itself is untouched
    @Redirect(method = "aiStep",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean skipItemSlowdown(LocalPlayer player) {
        return !ModConfig.isNoSlowdownItemEnabled() && player.isUsingItem();
    }
}
