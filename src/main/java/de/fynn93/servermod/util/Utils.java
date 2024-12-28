package de.fynn93.servermod.util;

import de.bluecolored.bluemap.api.math.Color;
import de.fynn93.servermod.ServerMod;
import earth.terrarium.cadmus.api.teams.TeamApi;
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

    public static String getPlayerNameByUUID(UUID claimId) {
        AtomicReference<String> returnVal = new AtomicReference<>(claimId.toString());
        Objects.requireNonNull(ServerMod.getServer().getProfileCache()).get(claimId).ifPresent(profile -> returnVal.set(profile.getName()));
        var retValue = returnVal.get();
        try {
            UUID.fromString(retValue);
            return getTeamNameByUUID(claimId);
        } catch (Exception e) {
            return retValue;
        }
    }

    public static String getTeamNameByUUID(UUID claimId) {
        return TeamApi.API.getName(ServerMod.getServer(), claimId).getString();
    }

    public static Color calculateColor(String playerName, boolean isLine) {
        int hash = playerName.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;

        return new Color(!isLine ? (int) Math.floor(r * .784313725) : r, g, b, isLine ? 1f : .3f);
    }
}
