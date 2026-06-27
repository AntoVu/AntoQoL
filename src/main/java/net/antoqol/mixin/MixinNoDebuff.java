package net.antoqol.mixin;

import net.antoqol.config.ModConfig;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinNoDebuff {

    @Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
    private static void cancelNightVision(LivingEntity entity,
            float partialTicks, CallbackInfoReturnable<Float> cir) {
        if (ModConfig.isNoDebuffNightVisionEnabled()
                && entity.hasEffect(MobEffects.NIGHT_VISION)) {
            cir.setReturnValue(0.0f);
        }
    }
}