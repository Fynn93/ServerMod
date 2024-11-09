package de.fynn93.servermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fynn93.servermod.discord.Message;
import de.fynn93.servermod.discord.MessageReceiver;
import de.fynn93.servermod.discord.Webhook;
import de.fynn93.servermod.util.DimensionUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String username = "Server";
            String messageString = message.getString();
            String avatarUrl = "https://minecraft.wiki/images/Java_Edition_icon_3.png";

            final Pattern normalChatMessage = Pattern.compile("\\[\\d{2}:\\d{2}\\] <([^>]+)> (.*)", Pattern.MULTILINE);
            final Matcher normalChatMessageMatcher = normalChatMessage.matcher(messageString);

            final Pattern discordPattern = Pattern.compile("\\[\\d{2}:\\d{2}\\] \\[DISCORD", Pattern.MULTILINE);
            final Matcher discordMatcher = discordPattern.matcher(messageString);

            if (normalChatMessageMatcher.find()) {
                // chat message
                username = normalChatMessageMatcher.group(1);
                messageString = normalChatMessageMatcher.group(2);
                avatarUrl = "https://mc-heads.net/avatar/" + username;
            } else if (messageString.endsWith("hat das Spiel betreten")) {
                // player join
                messageString = "<@&" + config.joinPingRoleId + ">: " + messageString;
            } else if (discordMatcher.find())
                return;

            webhook.addToQueue(
                    new Message(
                            username,
                            messageString,
                            avatarUrl
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

        JDA api = JDABuilder
                .createLight(config.token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
        api.addEventListener(new MessageReceiver());

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