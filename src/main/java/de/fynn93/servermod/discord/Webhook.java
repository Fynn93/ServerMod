package de.fynn93.servermod.discord;

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
        send(messageQueue.poll());
    }

    public void send(Message message) {
        if (message == null) {
            System.out.println("Message is null.");
            CompletableFuture.completedFuture(null);
            return;
        }
        if (endpoint.isEmpty()) {
            System.out.println("Webhook endpoint is empty.");
            CompletableFuture.completedFuture(null);
            return;
        }

        HttpClient client = HttpClient.newHttpClient();

        String jsonPayload = String.format(
                "{\"content\": \"%s\", \"username\": \"%s\", \"avatar_url\": \"%s\"}",
                message.content, message.username, message.avatarUrl
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 204) {
                        System.out.println("Message sent successfully.");
                    } else if (response.statusCode() == 429) {
                        System.out.println("Rate limited. Retrying in " + response.headers().firstValue("Retry-After").orElse("0") + "s.");
                        try {
                            Thread.sleep(Long.parseLong(response.headers().firstValue("Retry-After").orElse("0")) * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        send(message);
                    } else {
                        System.out.println("Failed to send message: " + response.statusCode());
                        System.out.println("Response: " + response.body());
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}
