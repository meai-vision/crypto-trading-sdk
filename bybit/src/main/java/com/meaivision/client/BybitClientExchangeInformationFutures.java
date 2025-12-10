package com.meaivision.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BybitClientExchangeInformationFutures {

    private static final String URL =
            "https://api.bybit.com/v5/market/instruments-info?category=linear";

    public String getExchangeInformation() {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).GET().build();
        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error occurred during sending a request into " + URL, e);
        }
    }
}
