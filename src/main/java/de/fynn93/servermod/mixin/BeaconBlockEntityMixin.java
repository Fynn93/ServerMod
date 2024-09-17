package de.fynn93.servermod.mixin;

import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
    @ModifyConstant(method = "applyEffects", constant = @Constant(intValue = 10))
    private static int modifyBeaconRange(int in) {
        return in * 500;
    }
}
