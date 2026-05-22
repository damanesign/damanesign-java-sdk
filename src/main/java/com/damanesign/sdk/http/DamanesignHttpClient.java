package com.damanesign.sdk.http;

import com.damanesign.sdk.DamanesignException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class DamanesignHttpClient implements AutoCloseable {
    private final URI baseUrl;
    private final String apiKey;
    private final HttpClient httpClient;
    private final Duration timeout;
    private final ObjectMapper objectMapper;

    public DamanesignHttpClient(URI baseUrl, String apiKey, HttpClient httpClient, Duration timeout) {
        this.baseUrl = trimTrailingSlash(Objects.requireNonNull(baseUrl, "baseUrl must not be null"));
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey must not be null");
        this.timeout = timeout == null ? Duration.ofSeconds(30) : timeout;
        this.httpClient = httpClient == null
                ? HttpClient.newBuilder().connectTimeout(this.timeout).build()
                : httpClient;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public <T> T get(String path, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("x-api-key", apiKey)
                .GET()
                .build();

        HttpResponse<String> response = send(request);
        ensureSuccess(response);
        return readJson(response.body(), responseType);
    }

    public <T> List<T> getList(String path, Class<T[]> arrayType) {
        T[] array = get(path, arrayType);
        return Arrays.asList(array);
    }

    public byte[] getBytes(String path) {
        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(timeout)
                .header("Accept", "application/octet-stream")
                .header("x-api-key", apiKey)
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new DamanesignException(
                        "Damanesign API request failed with status " + response.statusCode(),
                        response.statusCode(),
                        new String(response.body(), StandardCharsets.UTF_8)
                );
            }
            return response.body();
        } catch (IOException e) {
            throw new DamanesignException("Unable to call Damanesign API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DamanesignException("Damanesign API call was interrupted", e);
        }
    }

    public <T> T post(String path, Object body, Class<T> responseType) {
        String payload = writeJson(body);
        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = send(request);
        ensureSuccess(response);

        return readJson(response.body(), responseType);
    }

    public <T> T put(String path, Object body, Class<T> responseType) {
        String payload = writeJson(body);
        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .PUT(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = send(request);
        ensureSuccess(response);

        return readJson(response.body(), responseType);
    }

    public <T> T putWithoutBody(String path, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("x-api-key", apiKey)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = send(request);
        ensureSuccess(response);

        return readJson(response.body(), responseType);
    }

    public void delete(String path) {
        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("x-api-key", apiKey)
                .DELETE()
                .build();

        HttpResponse<String> response = send(request);
        ensureSuccess(response);
    }

    public void postWithoutBody(String path) {
        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("x-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = send(request);
        ensureSuccess(response);
    }

    public String getString(String path) {
        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("x-api-key", apiKey)
                .GET()
                .build();

        HttpResponse<String> response = send(request);
        ensureSuccess(response);
        return response.body();
    }

    public <T> T postMultipartFile(String path, String fieldName, Path file, Class<T> responseType) {
        String boundary = "----DamanesignJavaSdk" + UUID.randomUUID();
        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(timeout)
                .header("Accept", "application/json")
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("x-api-key", apiKey)
                .POST(ofFileMultipart(fieldName, file, boundary))
                .build();

        HttpResponse<String> response = send(request);
        ensureSuccess(response);

        return readJson(response.body(), responseType);
    }

    @Override
    public void close() {
        // java.net.http.HttpClient has no resources to close on Java 11.
    }

    private URI resolve(String path) {
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
        return baseUrl.resolve(normalizedPath);
    }

    private HttpRequest.BodyPublisher ofFileMultipart(String fieldName, Path file, String boundary) {
        try {
            String filename = file.getFileName().toString();
            List<byte[]> parts = new ArrayList<>();
            parts.add(("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + escape(filename) + "\"\r\n"
                    + "Content-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            parts.add(Files.readAllBytes(file));
            parts.add(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
            return HttpRequest.BodyPublishers.ofByteArrays(parts);
        } catch (IOException e) {
            throw new DamanesignException("Unable to read file for upload", e);
        }
    }

    private String escape(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new DamanesignException("Unable to call Damanesign API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DamanesignException("Damanesign API call was interrupted", e);
        }
    }

    private void ensureSuccess(HttpResponse<String> response) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new DamanesignException(
                    "Damanesign API request failed with status " + response.statusCode(),
                    response.statusCode(),
                    response.body()
            );
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new DamanesignException("Unable to serialize Damanesign request", e);
        }
    }

    private <T> T readJson(String body, Class<T> responseType) {
        if (responseType == Void.class) {
            return null;
        }
        try {
            return objectMapper.readValue(body, responseType);
        } catch (IOException e) {
            throw new DamanesignException("Unable to deserialize Damanesign response", e);
        }
    }

    private static URI trimTrailingSlash(URI uri) {
        String value = uri.toString();
        return value.endsWith("/") ? URI.create(value) : URI.create(value + "/");
    }
}
