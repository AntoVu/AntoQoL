package net.antoqol.mixin;

import net.antoqol.config.ModConfig;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinBlockSlow {
    
    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    private void cancelStuckInBlock(
            net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.world.phys.Vec3 motionMultiplier,
            CallbackInfo ci) {
        if (ModConfig.isNoSlowdownBlockEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("RETURN"), cancellable = true)
    private void cancelBlockSpeedFactor(CallbackInfoReturnable<Float> cir) {
        if (ModConfig.isNoSlowdownBlockEnabled()) {
            cir.setReturnValue(1.0f);
        }
    }
}