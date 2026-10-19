package net.antoqol.mixin;

import net.antoqol.events.ClientEventHandler;
import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Timer.class)
public class MixinTimer {

    @Shadow @Final @Mutable
    private float msPerTick;

    @Inject(method = "advanceTime", at = @At("HEAD"))
    private void scaleTime(long currentTimeMs, CallbackInfoReturnable<Integer> cir) {
        if (ClientEventHandler.isTimerActive()) {
            msPerTick = 50.0f / (float) ClientEventHandler.getCurrentTimer();
        } else {
            msPerTick = 50.0f;
        }
    }
}
