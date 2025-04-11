package de.fynn93.servermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fynn93.servermod.decorator.DecoratorManager;
import de.fynn93.servermod.decorator.TimeDecorator;
import de.fynn93.servermod.dispenser.DispenserBehavior;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.DispenserBlock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraft.commands.Commands.literal;

public class ServerMod implements ModInitializer {
    private static MinecraftServer _server;
    public static Config config = new Config();

    public static boolean usesDurability = false;

    public static MinecraftServer getServer() {
        return _server;
    }

    @Override
    public void onInitialize() {
        DecoratorManager.registerDecorator(new TimeDecorator());

        // register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
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
                )
        );

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

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            _server = server;
            FakePlayer player = FakePlayer.get(_server.overworld());
            DispenseItemBehavior behaviors = DispenserBehavior.getDispenserBehavior(player);
            BuiltInRegistries.ITEM.forEach(item -> {
                if (DispenserBlock.DISPENSER_REGISTRY.containsKey(item)) return;
                DispenserBlock.registerBehavior(item, behaviors);
            });
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
    }
}