package de.fynn93.servermod.mixin;

import de.fynn93.servermod.decorator.TimeDecorator;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "getChatDecorator", at = @At("HEAD"), cancellable = true)
    public void getDecorator(CallbackInfoReturnable<ChatDecorator> cir) {
        cir.setReturnValue(new TimeDecorator());
        cir.cancel();
    }
}
