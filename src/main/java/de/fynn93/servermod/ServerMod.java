package de.fynn93.servermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fynn93.servermod.decorator.DecoratorManager;
import de.fynn93.servermod.decorator.TimeDecorator;
import de.fynn93.servermod.dispenser.DispenserBehavior;
import de.fynn93.servermod.web.CodeGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.DispenserBlock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import static net.minecraft.commands.Commands.literal;

public class ServerMod implements ModInitializer {
    private static MinecraftServer _server;
    public static Config config = new Config();

    public static boolean usesDurability = false;

    public static MinecraftServer getServer() {
        return _server;
    }

    public static void loadConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("servermod");
        configPath.toFile().mkdirs();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();

        Path configFilePath = configPath.resolve("config.json");

        // Create config file if not exists
        if (!configFilePath.toFile().exists()) {
            try {
                Files.writeString(configFilePath, gson.toJson(new Config()));
            } catch (IOException ignored) {
            }
        }

        // Load config
        try {
            config = gson.fromJson(Files.readString(configFilePath), Config.class);
        } catch (IOException ignored) {
        }
    }

    public static void saveConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("servermod");
        configPath.toFile().mkdirs();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();

        Path configFilePath = configPath.resolve("config.json");

        // Save config
        try {
            Files.writeString(configFilePath, gson.toJson(config));
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onInitialize() {
        DecoratorManager.registerDecorator(new TimeDecorator());

        // register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                dispatcher.register(literal("nv")
                        .executes(commandContext -> {
                            ServerPlayer player = commandContext.getSource().getPlayer();
                            assert player != null;
                            if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                                player.removeEffect(MobEffects.NIGHT_VISION);
                                return 1;
                            }
                            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 255, false, false));
                            return 1;
                        })
                );
            dispatcher.register(literal("servermodreload")
                    .requires(source -> source.hasPermission(2))
                    .executes(commandContext -> {
                        loadConfig();
                        return 1;
                    })
            );
            dispatcher.register(literal("servermodsave")
                    .requires(source -> source.hasPermission(2))
                    .executes(commandContext -> {
                        saveConfig();
                        return 1;
                    })
            );
            if (config.authEnabled) {
                dispatcher.register(literal("auth")
                        .executes(commandContext -> {
                            ServerPlayer player = commandContext.getSource().getPlayer();
                            assert player != null;
                            var component = MutableComponent.create(new PlainTextContents.LiteralContents("Drücke "))
                                    .append(MutableComponent.create(new PlainTextContents.LiteralContents("hier"))
                                            .withStyle(style -> style.withClickEvent(
                                                    new ClickEvent(
                                                            ClickEvent.Action.OPEN_URL,
                                                            "https://fynn93.dev/minecraft/authenticate.php?code=%s"
                                                                    .formatted(CodeGenerator.generate(player))
                                                    )
                                            ).withUnderlined(true).withBold(true).withColor(ChatFormatting.GREEN))
                                    ).append(" um dich zu authentifizieren!");
                            player.sendSystemMessage(component);
                            return 1;
                        })
                );
                }
        });

        loadConfig();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            _server = server;
            FakePlayer player = FakePlayer.get(_server.overworld());
            DispenseItemBehavior behaviors = DispenserBehavior.getDispenserBehavior(player);
            BuiltInRegistries.ITEM.forEach(item -> {
                if (DispenserBlock.DISPENSER_REGISTRY.containsKey(item)) return;
                DispenserBlock.registerBehavior(item, behaviors);
            });
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            _server = null;
            saveConfig();
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer.getLastDeathLocation().isEmpty()) {
                return;
            }
            GlobalPos o = newPlayer.getLastDeathLocation().get();
            newPlayer.sendSystemMessage(MutableComponent.create(new PlainTextContents.LiteralContents("Du bist bei "))
                    .append(String.valueOf(o.pos().getX()))
                    .append(" ")
                    .append(String.valueOf(o.pos().getY()))
                    .append(" ")
                    .append(String.valueOf(o.pos().getZ()))
                    .append(" gestorben!")
            );
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerMod.config.playerOptions.computeIfAbsent(handler.getPlayer().getUUID(), k -> ServerMod.config.defaultPlayerOptions);
            if (config.serverOpenDate.after(new Date()) && !handler.getPlayer().hasPermissions(2)) {
                var reason = MutableComponent.create(new PlainTextContents.LiteralContents("Der Server ist noch nicht geöffnet!\n"))
                        .append("Der Server öffnet am ")
                        .append(MutableComponent.create(
                                new PlainTextContents.LiteralContents(config.serverOpenDate.toString())
                        ).withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                        .append("!");

                ClientboundDisconnectPacket packet = new ClientboundDisconnectPacket(reason);
                handler.send(packet);
            }
        });
    }
}