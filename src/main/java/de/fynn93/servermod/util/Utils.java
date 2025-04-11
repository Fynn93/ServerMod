package de.fynn93.servermod.util;

import de.fynn93.servermod.ServerMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {
    public static ServerLevel getDimension(ResourceLocation resourceLocation) {
        return ServerMod.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, resourceLocation));
    }

    public static String getPlayerNameByUUID(UUID playerUUID) {
        AtomicReference<String> returnVal = new AtomicReference<>(playerUUID.toString());
        Objects.requireNonNull(
                        ServerMod.getServer().getProfileCache()
                )
                .get(playerUUID)
                .ifPresent(profile ->
                        returnVal.set(profile.getName())
                );
        return returnVal.get();
    }

    public static int getDamage(ItemStack stack) {
        List<ItemAttributeModifiers.Entry> modifiers = stack.getComponents().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers();

        for (ItemAttributeModifiers.Entry entry : modifiers) {
            if (entry.attribute().value().getDescriptionId().endsWith("attack_damage"))
                return Double.valueOf(entry.modifier().amount()).intValue();
        }
        return 0;
    }
}
