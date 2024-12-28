package de.fynn93.servermod.discord;

public class Message {
    public String username;
    public String content;
    public String avatarUrl;

    public Message(String username, String content, String avatarUrl) {
        this.username = username;
        this.content = content;
        this.avatarUrl = avatarUrl;
    }
}