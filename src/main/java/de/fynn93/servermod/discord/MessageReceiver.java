package de.fynn93.servermod.discord;

import de.fynn93.servermod.ServerMod;
import de.fynn93.servermod.decorator.TimeDecorator;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;

public class MessageReceiver extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.getMessage().getChannelId().equals(ServerMod.config.channelId)) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        // TODO: Send content to server
        ServerMod.getServer().getPlayerList().broadcastSystemMessage(
                MutableComponent.create(
                        new PlainTextContents.LiteralContents("")
                ).append(TimeDecorator.decorate()).append("[DISCORD] <" + event.getAuthor().getName() + ">").withStyle(ChatFormatting.BLUE).append(": ").append(content)
                , false);
    }
}