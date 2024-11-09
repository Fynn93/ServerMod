package de.fynn93.servermod.discord;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class Webhook {
    public String endpoint;

    public Webhook(String endpoint) {
        this.endpoint = endpoint;
    }

    public CompletableFuture<Void> send(Message message) {
        if (endpoint.isEmpty()) {
            System.out.println("Webhook endpoint is empty.");
            return CompletableFuture.completedFuture(null);
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

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 204) {
                        System.out.println("Message sent successfully.");
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
