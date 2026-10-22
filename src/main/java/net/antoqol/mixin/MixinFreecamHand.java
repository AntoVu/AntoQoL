package net.antoqol.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.antoqol.events.ClientEventHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinFreecamHand {

    // The first-person hand would float in front of the freecam
    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void hideHand(PoseStack poseStack, Camera camera, float partialTick, CallbackInfo ci) {
        if (ClientEventHandler.isFreecamActive()) ci.cancel();
    }
}
