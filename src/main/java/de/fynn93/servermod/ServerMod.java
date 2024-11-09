package de.fynn93.servermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import de.fynn93.servermod.discord.Message;
import de.fynn93.servermod.discord.Webhook;
import de.fynn93.servermod.util.DimensionUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.minecraft.commands.Commands.literal;

public class ServerMod implements ModInitializer {
    private static MinecraftServer _server;
    public static Webhook webhook;
    public static Config config = new Config();

    public static MinecraftServer getServer() {
        return _server;
    }

    @Override
    public void onInitialize() {
        // register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("b")
                        .executes(context -> {
                            Optional<GlobalPos> o = Objects.requireNonNull(context.getSource().getPlayer()).getLastDeathLocation();
                            o.ifPresent(pos -> {
                                /*context.getSource().getPlayer().teleport(
                                        new TeleportTransition(
                                                DimensionUtils.getDimension(pos.dimension().location()),
                                                new Vec3(pos.pos().getX(), pos.pos().getY(), pos.pos().getZ()),
                                                Vec3.ZERO,
                                                0.0f, 0.0f,
                                                Set.of(),
                                                TeleportTransition.DO_NOTHING
                                        )
                                );*/
                                context.getSource().getPlayer().teleportTo(
                                        DimensionUtils.getDimension(pos.dimension().location()),
                                        pos.pos().getX(),
                                        pos.pos().getY(),
                                        pos.pos().getZ(),
                                        0.0f, 0.0f
                                );
                            });
                            return 1;
                        })
                )
        );

        ServerLifecycleEvents.SERVER_STARTED.register(server -> _server = server);
        ServerMessageEvents.GAME_MESSAGE.register((server, message, overlay) -> {
            webhook.addToQueue(
                    new Message(
                            "Server",
                            "<@&" + ServerMod.config.joinPingRoleId + ">: " + message.getString(),
                            "https://minecraft.wiki/images/Java_Edition_icon_3.png"
                    )
            );
        });
        ServerMessageEvents.CHAT_MESSAGE.register((playerChatMessage, player, bound) -> {
            webhook.addToQueue(
                    new Message(
                            player.getName().getString(),
                            playerChatMessage.signedContent(),
                            "https://mc-heads.net/avatar/" + player.getStringUUID()
                    )
            );
        });

        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("servermod");
        configPath.toFile().mkdirs();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Path configFilePath = configPath.resolve("config.json");
        if (!configFilePath.toFile().exists()) {
            try {
                Files.writeString(configFilePath, gson.toJson(new Config()));
            } catch (IOException ignored) {
            }
        }

        try {
            config = gson.fromJson(Files.readString(configFilePath), Config.class);
        } catch (IOException ignored) {
        }

        webhook = new Webhook(config.webhookUrl);

        Thread thread = new Thread(() -> {
            while (true) {
                webhook.sendQueue();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        });
        thread.start();
    }
}