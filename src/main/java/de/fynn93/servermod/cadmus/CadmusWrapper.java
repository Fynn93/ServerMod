/*package de.fynn93.servermod.cadmus;

import de.fynn93.servermod.ServerMod;
import de.fynn93.servermod.bluemap.Bluemap;
import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.api.teams.TeamApi;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.UUID;

public class CadmusWrapper {
    public static void getAllClaimsForLevel(ServerLevel serverLevel) {
        while (!FabricLoader.getInstance().isModLoaded("cadmus")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        var map = ClaimApi.API.getAllClaims(serverLevel);
        for (Object2ObjectMap.Entry<ChunkPos, ObjectBooleanPair<UUID>> entry : map.object2ObjectEntrySet()) {
            var uuid = entry.getValue().left();
            var chunkPos = entry.getKey();
            Bluemap.addClaimMarker(uuid, chunkPos.x, chunkPos.z);
        }
    }
}
*/