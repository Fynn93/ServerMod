package de.fynn93.servermod.util;

import de.fynn93.servermod.ServerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class DimensionUtils {
    public static ServerLevel getDimension(ResourceLocation resourceLocation) {
        ResourceKey<Level> resourceKey = ResourceKey.create(Registries.DIMENSION, resourceLocation);
        return ServerMod.getServer().getLevel(resourceKey);
    }
}
