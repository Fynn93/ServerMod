package de.fynn93.servermod.mixin;

import de.fynn93.servermod.ServerMod;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    public void dropEquipment(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        UUID uuid = player.getUUID();

        // Check if the player has the keepInventory option enabled
        if (ServerMod.config.playerOptions.get(uuid).enableKeepInventory) {
            ci.cancel();
        }
    }
}
