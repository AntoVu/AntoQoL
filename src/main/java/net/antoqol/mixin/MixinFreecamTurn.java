package net.antoqol.mixin;

import net.antoqol.events.ClientEventHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinFreecamTurn {

    // Mouse look turns the freecam instead of the player
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void turnFreecam(double yRot, double xRot, CallbackInfo ci) {
        if ((Object) this instanceof LocalPlayer && ClientEventHandler.isFreecamActive()) {
            ClientEventHandler.turnFreecam(yRot, xRot);
            ci.cancel();
        }
    }
}
