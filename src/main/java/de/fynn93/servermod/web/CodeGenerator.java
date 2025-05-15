package de.fynn93.servermod.web;

import de.fynn93.servermod.util.Utils;
import net.minecraft.server.level.ServerPlayer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CodeGenerator {
    public static String generate(ServerPlayer player) {
        // Generate a unique code for the player
        // Format: UUID;PlayerName
        // Will be base64 encoded and aes encrypted
        var uuid = player.getUUID();
        var name = player.getName().getString();
        var string = "%s;%s".formatted(uuid, name);
        var encrypted = Utils.encryptAesECB(string, System.getenv("MINECRAFT_AUTH_KEY"));
        return URLEncoder.encode(encrypted, StandardCharsets.UTF_8);
    }
}
