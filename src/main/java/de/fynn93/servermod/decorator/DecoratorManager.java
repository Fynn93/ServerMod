package de.fynn93.servermod.decorator;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class DecoratorManager implements ChatDecorator {
    private static final ArrayList<ChatDecorator> decorators = new ArrayList<>();

    public static void registerDecorator(ChatDecorator decorator) {
        decorators.add(decorator);
    }

    @Override
    public @NotNull MutableComponent decorate(@Nullable ServerPlayer serverPlayer, @NotNull Component component) {
        assert serverPlayer != null;

        MutableComponent base = MutableComponent.create(new PlainTextContents.LiteralContents(""));
        for (ChatDecorator decorator : decorators) {
            base.append(decorator.decorate(serverPlayer, component));
            base.append(MutableComponent.create(new PlainTextContents.LiteralContents(" ")).withStyle(ChatFormatting.RESET));
        }

        // Append player name
        base.append(MutableComponent.create(
                new PlainTextContents.LiteralContents("<" + serverPlayer.getName().getString() + "> ")
        ).append(component).withStyle(ChatFormatting.RESET));
        return base;
    }
}
