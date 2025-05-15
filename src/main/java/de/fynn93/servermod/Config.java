package de.fynn93.servermod;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Config {
    /// Player UUID -> PlayerPlayOptions
    /// The options for each player
    public Map<UUID, PlayerPlayOptions> playerOptions = new HashMap<>();

    /// Whether the server allows authentication for the web interface
    public boolean authEnabled = false;

    /// The date when the server will be opened
    public Date serverOpenDate = new Date();

    /// The date when the end will be opened
    /// This is set to 7 days after the server is started
    public Date endOpenDate = Date.from(Instant.now().plusSeconds(60 * 60 * 24 * 7));

    /// The default options for each player
    public PlayerPlayOptions defaultPlayerOptions = new PlayerPlayOptions();

    public static class PlayerPlayOptions {
        /// Whether the player is allowed to use PvP
        public boolean enablePvP = true;
        /// Whether the player is allowed to use keep inventory
        public boolean enableKeepInventory = false;
    }
}