package net.antoqol.mixin;

import net.antoqol.events.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinEspGlow {

    // ESP targets get vanilla's glowing outline, which shows through walls
    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void espGlow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ClientEventHandler.espColor(entity) != -1) cir.setReturnValue(true);
    }
}
