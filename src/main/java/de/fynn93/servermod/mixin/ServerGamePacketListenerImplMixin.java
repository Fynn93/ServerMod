package de.fynn93.servermod.mixin;

import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Inject(method = "broadcastChatMessage", at = @At("HEAD"), cancellable = true)
    public void broadcastChatMessage(PlayerChatMessage playerChatMessage, CallbackInfo ci) {
        assert playerChatMessage.unsignedContent() != null;
        ((ServerGamePacketListenerImpl) (Object) this).server.getPlayerList().broadcastSystemMessage(playerChatMessage.unsignedContent(), false);
        this.detectRateSpam();
        ci.cancel();
    }

    @Shadow
    protected abstract void detectRateSpam();
}
