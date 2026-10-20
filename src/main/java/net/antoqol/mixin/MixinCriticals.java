package net.antoqol.mixin;

import net.antoqol.config.ModConfig;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MixinCriticals {

    // Before the attack packet, report a tiny hop off the ground: the server then sees a falling,
    // airborne player and scores the hit as a critical
    @Inject(method = "attack", at = @At("HEAD"))
    private void sendCritPackets(Player player, Entity target, CallbackInfo ci) {
        if (!ModConfig.isCriticalsEnabled() || !(target instanceof LivingEntity)) return;
        if (!(player instanceof LocalPlayer local)) return;
        if (!local.onGround() || local.isInWater() || local.isInLava() || local.onClimbable() || local.isPassenger()) return;

        double x = local.getX(), y = local.getY(), z = local.getZ();
        local.connection.send(new ServerboundMovePlayerPacket.Pos(x, y + 0.0625, z, false));
        local.connection.send(new ServerboundMovePlayerPacket.Pos(x, y, z, false));
    }
}
