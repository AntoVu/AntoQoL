package net.antoqol.mixin;

import net.antoqol.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinInvisibility {

    @Inject(method = "shouldRender", at = @At("RETURN"), cancellable = true)
    private <T extends Entity> void revealInvisibleEntities(
            T entity,
            net.minecraft.client.renderer.culling.Frustum frustum,
            double camX, double camY, double camZ,
            CallbackInfoReturnable<Boolean> cir) {

        if (!ModConfig.isNoDebuffInvisibilityEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Dont reveal local player
        if (entity == mc.player) return;

        // If entity is invisible force render
        if (entity.isInvisible() && !cir.getReturnValue()) {
            cir.setReturnValue(true);
        }
    }
}