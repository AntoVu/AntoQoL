package net.antoqol.mixin;

import net.antoqol.events.ClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.Timer")
public class MixinTimer {

    @Shadow
    private float f_92521_; // SRG name for msPerTick

    @Inject(method = "m_92525_", at = @At("HEAD")) // SRG name for advanceTime
    private void scaleTime(long currentTimeMs, CallbackInfoReturnable<Integer> cir) {
        if (ClientEventHandler.isTimerActive()) {
            f_92521_ = 50.0f / (float) ClientEventHandler.getCurrentTimer();
        } else {
            f_92521_ = 50.0f;
        }
    }
}