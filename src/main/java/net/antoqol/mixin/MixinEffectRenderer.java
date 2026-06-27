package net.antoqol.mixin;

import net.antoqol.config.ModConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinEffectRenderer {

    @Inject(method = "hasEffect", at = @At("RETURN"), cancellable = true)
    private void suppressDebuffEffects(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) return;
        // Check this entity is the local player
        if (!((Object) this instanceof net.minecraft.client.player.LocalPlayer)) return;

        // Blindness
        if (effect == MobEffects.BLINDNESS && ModConfig.isNoDebuffBlindnessEnabled()) {
            cir.setReturnValue(false);
        }

        // Darkness
        if (effect == MobEffects.DARKNESS && ModConfig.isNoDebuffDarknessEnabled()) {
            cir.setReturnValue(false);
        }

        // Nausea
        if (effect == MobEffects.CONFUSION && ModConfig.isNoDebuffNauseaEnabled()) {
            cir.setReturnValue(false);
        }
    }
}