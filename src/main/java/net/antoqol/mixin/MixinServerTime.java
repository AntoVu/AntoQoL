package net.antoqol.mixin;

import net.antoqol.events.ClientEventHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinServerTime {

    // TAIL only runs on the main thread: the network-thread call bails out early to reschedule
    @Inject(method = "handleSetTime", at = @At("TAIL"))
    private void onSetTime(ClientboundSetTimePacket packet, CallbackInfo ci) {
        ClientEventHandler.onServerTime(packet.getGameTime());
    }
}
