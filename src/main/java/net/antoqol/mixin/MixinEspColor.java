package net.antoqol.mixin;

import net.antoqol.events.ClientEventHandler;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public class MixinEspColor {

    // Outline color only; the entity's actual team color is untouched
    @Redirect(method = "renderLevel",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"))
    private int espOutlineColor(Entity entity) {
        int color = ClientEventHandler.espColor(entity);
        return color != -1 ? color : entity.getTeamColor();
    }
}
