package com.meaivision.client;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

public class BybitRestClient {
    private static final String BASE_URL = "https://api.bybit.com";
    private final String apiKey;
    private final String apiSecret;
    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public BybitRestClient(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.client = new OkHttpClient();
    }

    public String sendGet(String path, Map<String, String> queryParams) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
        if (queryParams != null) queryParams.forEach(urlBuilder::addQueryParameter);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .addHeader("X-BAPI-API-KEY", apiKey)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String sendPost(String path, Map<String, String> bodyParams) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (bodyParams != null) bodyParams.forEach(formBuilder::add);
        Request request = new Request.Builder()
                .url(BASE_URL + path)
                .post(formBuilder.build())
                .addHeader("X-BAPI-API-KEY", apiKey)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}