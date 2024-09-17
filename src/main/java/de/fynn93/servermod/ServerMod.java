package de.fynn93.servermod;

import de.fynn93.servermod.util.DimensionUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.Optional;

import static net.minecraft.commands.Commands.literal;

public class ServerMod implements ModInitializer {
    private static MinecraftServer _server;

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
                            o.ifPresent(pos -> context.getSource().getPlayer().teleportTo(DimensionUtils.getDimension(pos.dimension().location()),
                                    pos.pos().getX(),
                                    pos.pos().getY(),
                                    pos.pos().getZ(),
                                    0, 0
                            ));
                            return 1;
                        })
                )
        );

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            _server = server;
        });
    }
}