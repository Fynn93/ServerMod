package de.fynn93.servermod.mixin;

import de.fynn93.servermod.ServerMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Date;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;", at = @At("HEAD"), cancellable = true)
    public void teleport(TeleportTransition teleportTransition, CallbackInfoReturnable<Entity> cir) {
        if (teleportTransition.newLevel().dimension() == Level.END && ServerMod.config.endOpenDate.after(new Date())) {
            cir.setReturnValue(((ServerPlayer) (Object) this));
        }
    }

    @Inject(method = "canHarmPlayer", at = @At("HEAD"), cancellable = true)
    public void canHarmPlayer(Player other, CallbackInfoReturnable<Boolean> cir) {
        boolean thisPvP = ServerMod.config.playerOptions.get(((ServerPlayer) (Object) this).getUUID()).enablePvP;
        boolean otherPvP = ServerMod.config.playerOptions.get(other.getUUID()).enablePvP;

        if (!thisPvP || !otherPvP) {
            cir.setReturnValue(false);
        }
    }
}
