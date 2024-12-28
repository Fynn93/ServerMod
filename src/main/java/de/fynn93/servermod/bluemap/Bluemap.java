/*package de.fynn93.servermod.bluemap;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Shape;
import de.fynn93.servermod.ServerMod;
import de.fynn93.servermod.cadmus.CadmusWrapper;
import de.fynn93.servermod.util.Utils;

import java.util.UUID;

public class Bluemap {
    public static void addClaimMarker(UUID claimId, int x, int z) {
        String playerName = Utils.getPlayerNameByUUID(claimId);

        ExtrudeMarker marker = ExtrudeMarker.builder()
                .position(x * 16, 64.0, z * 16)
                .position(x * 16 + 16, 64.0, z * 16 + 16)
                .maxDistance(1000)
                .label(playerName)
                .shape(Shape.builder()
                        .addPoint(new Vector2d(x * 16, z * 16))
                        .addPoint(new Vector2d(x * 16 + 16, z * 16))
                        .addPoint(new Vector2d(x * 16 + 16, z * 16 + 16))
                        .addPoint(new Vector2d(x * 16, z * 16 + 16))
                        .build(), -64, 319)
                .fillColor(Utils.calculateColor(playerName, false))
                .lineColor(Utils.calculateColor(playerName, true))
                .build();

        MarkerSet markerSet = MarkerSet.builder()
                .label("Chunks von " + playerName)
                .build();

        markerSet.getMarkers().put(x + ";" + z, marker);

        ServerMod.bluemap.getWorld("overworld").ifPresent(world -> {
            for (BlueMapMap map : world.getMaps()) {
                if (map.getMarkerSets().containsKey("Chunks von " + playerName))
                    map.getMarkerSets()
                            .get("Chunks von " + playerName)
                            .getMarkers()
                            .put(
                                    x + ";" + z,
                                    marker
                            );
                else
                    map.getMarkerSets().put("Chunks von " + Utils.getPlayerNameByUUID(claimId), markerSet);
            }
        });
    }
}
*/