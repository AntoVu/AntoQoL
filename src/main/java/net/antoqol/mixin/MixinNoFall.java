package net.antoqol.mixin;

import net.antoqol.config.ModConfig;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class MixinNoFall {

    // Report on-ground in movement packets once falling: the server then applies its (still < 3 block)
    // fall distance, which deals no damage, and resets it every tick. Short drops are left alone so jump crits work.
    @Redirect(method = "sendPosition",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;onGround()Z"))
    private boolean spoofOnGround(LocalPlayer player) {
        if (ModConfig.isNoFallEnabled() && player.fallDistance > 2.0f && !player.isFallFlying()) return true;
        return player.onGround();
    }
}
