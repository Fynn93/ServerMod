package de.fynn93.servermod.util;

import de.fynn93.servermod.ServerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

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
}
