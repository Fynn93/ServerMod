package de.fynn93.servermod.mixin;

import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ShearsDispenseItemBehavior.class)
public class ShearsDispenserBehaviorMixin {
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 1))
    public int s(int constant) {
        return 0;
    }
}
