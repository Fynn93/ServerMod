package de.fynn93.servermod;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Config {
    /// Player UUID -> PlayerPlayOptions
    /// The options for each player
    public Map<UUID, PlayerPlayOptions> playerOptions = new HashMap<>();

    /// The default options for each player
    public PlayerPlayOptions defaultPlayerOptions = new PlayerPlayOptions();
    ///// Not used yet /////
    /// The date when the server will be opened
    public Date serverOpenDate = new Date();

    public static class PlayerPlayOptions {
        /// Whether the player is allowed to use PvP
        public boolean enablePvP = true;
        /// Whether the player is allowed to use keep inventory
        public boolean enableKeepInventory = false;
    }

    /// The date when the end will be opened
    /// This is set to 7 days after the server is started
    public Date endOpenDate = new Date(new Date().toInstant().plusSeconds(60 * 60 * 24 * 7).toEpochMilli());
}