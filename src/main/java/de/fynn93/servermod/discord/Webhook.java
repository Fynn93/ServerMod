package de.fynn93.servermod.discord;

import com.mojang.logging.LogUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class Webhook {
    private final String endpoint;
    private final Queue<Message> messageQueue;

    public Webhook(String endpoint) {
        this.endpoint = endpoint;
        messageQueue = new java.util.LinkedList<>();
    }

    public void addToQueue(Message message) {
        messageQueue.add(message);
    }

    public void sendQueue() {
        if (messageQueue.isEmpty()) return;
        send(messageQueue.poll());
    }

    private final HttpClient client = HttpClient.newHttpClient();

    public void send(Message message) {
        if (message == null) {
            CompletableFuture.completedFuture(null);
            return;
        }
        if (endpoint.isEmpty()) {
            System.out.println("Webhook endpoint is empty.");
            CompletableFuture.completedFuture(null);
            return;
        }

        String jsonPayload = String.format(
                "{\"content\": \"%s\", \"username\": \"%s\", \"avatar_url\": \"%s\"}",
                message.content, message.username, message.avatarUrl
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 429) {
                LogUtils.getLogger().info("Rate limited. Retrying in {}s.", response.headers().firstValue("Retry-After").orElse("0"));
                try {
                    Thread.sleep(Long.parseLong(response.headers().firstValue("Retry-After").orElse("0")) * 1000);
                } catch (InterruptedException ignored) {
                }
                send(message);
            } else if (response.statusCode() != 204) {
                LogUtils.getLogger().info("Failed to send message: {}", response.statusCode());
                LogUtils.getLogger().info("Response: {}", response.body());
            }
        } catch (InterruptedException | IOException ignored) {
        }
    }
}
