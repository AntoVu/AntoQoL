package net.antoqol.mixin;

import net.antoqol.events.ClientEventHandler;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinFreecamCamera {

    @Shadow private boolean detached;
    @Shadow protected abstract void setRotation(float yRot, float xRot);
    @Shadow protected abstract void setPosition(Vec3 pos);

    // Detached = vanilla renders the player's own body, like third person
    @Inject(method = "setup", at = @At("TAIL"))
    private void applyFreecam(BlockGetter level, Entity entity, boolean detached, boolean mirror,
            float partialTick, CallbackInfo ci) {
        if (!ClientEventHandler.isFreecamActive()) return;
        this.detached = true;
        setRotation(ClientEventHandler.getFreecamYaw(), ClientEventHandler.getFreecamPitch());
        setPosition(ClientEventHandler.getFreecamPos(partialTick));
    }
}
