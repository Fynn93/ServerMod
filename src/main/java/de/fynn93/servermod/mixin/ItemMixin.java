package de.fynn93.servermod.mixin;

import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Items.class)
public abstract class ItemMixin {
    // set all potion type stack size to 64

    /* disabled because it's not working
    @ModifyArg(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0),
            slice = @Slice(from = @At(value = "NEW", target = ";")))
    private static int onPotion(int old) {
        return 64;
    }

    @ModifyArg(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0),
            slice = @Slice(from = @At(value = "NEW", target = "")))
    private static int onSplashPotion(int old) {
        return 64;
    }

    @ModifyArg(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;", ordinal = 0),
            slice = @Slice(from = @At(value = "NEW", args = "class=net/minecraft/world/item/LingerPotionItem")))
    private static int onLingeringPotion(int old) {
        return 64;
    }*/
}