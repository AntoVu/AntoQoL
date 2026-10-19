package net.antoqol.mixin;

import net.antoqol.config.ModConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinInvisibility {

    // Not invisible to the local player = vanilla renders it translucent, like an invisible teammate
    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void revealInvisibleEntities(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof LocalPlayer && (Object) this != player
                && ModConfig.isNoDebuffInvisibilityEnabled()) {
            cir.setReturnValue(false);
        }
    }
}
