package de.fynn93.servermod.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Leashable.class)
public interface LeashableMixin {
    @Inject(method = "leashTooFarBehaviour", at = @At("HEAD"))
    private void leashTooFarBehaviour(CallbackInfo ci) {
        Entity entity = ((Leashable) this).getLeashHolder();
        if (entity != null) {
            // teleport leashed entity to holder
            ((Entity) this).teleportTo(entity.getX(), entity.getY(), entity.getZ());
        }
    }
}
