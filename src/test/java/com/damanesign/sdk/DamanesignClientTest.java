package com.damanesign.sdk;

import com.damanesign.sdk.model.CreateTransactionRequest;
import com.damanesign.sdk.model.FieldRequest;
import com.damanesign.sdk.model.FileResponse;
import com.damanesign.sdk.model.MemberRequest;
import com.damanesign.sdk.model.TransactionResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DamanesignClientTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void uploadFilePostsMultipartPayload() throws Exception {
        AtomicReference<String> method = new AtomicReference<>();
        AtomicReference<String> path = new AtomicReference<>();
        AtomicReference<String> authorization = new AtomicReference<>();
        AtomicReference<String> contentType = new AtomicReference<>();
        startServer(exchange -> {
            method.set(exchange.getRequestMethod());
            path.set(exchange.getRequestURI().toString());
            authorization.set(exchange.getRequestHeaders().getFirst("x-api-key"));
            contentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
            respond(exchange, 201, "{\"id\":\"file_123\",\"name\":\"contract.pdf\",\"contentType\":\"application/pdf\",\"type\":\"signable\"}");
        });

        Path file = Files.createTempFile("contract", ".pdf");
        Files.writeString(file, "%PDF-1.4 test", StandardCharsets.UTF_8);

        DamanesignClient client = DamanesignClient.builder()
                .baseUrl(serverUrl())
                .apiKey("test-token")
                .build();

        FileResponse response = client.uploadFile(file);

        assertEquals("POST", method.get());
        assertEquals("/files/upload?contentType=application%2Fpdf&type=signable", path.get());
        assertEquals("test-token", authorization.get());
        assertEquals(true, contentType.get().startsWith("multipart/form-data; boundary="));
        assertEquals("file_123", response.getId());
    }

    @Test
    void createTransactionPostsJsonPayload() throws Exception {
        AtomicReference<String> authorization = new AtomicReference<>();
        AtomicReference<JsonNode> requestBody = new AtomicReference<>();
        startServer(exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("x-api-key"));
            requestBody.set(objectMapper.readTree(exchange.getRequestBody()));
            respond(exchange, 201, "{\"id\":\"tx_123\",\"name\":\"Contrat client\",\"status\":\"draft\"}");
        });

        DamanesignClient client = DamanesignClient.builder()
                .baseUrl(serverUrl())
                .apiKey("test-token")
                .build();

        TransactionResponse response = client.createTransaction(CreateTransactionRequest.builder()
                .name("Contrat client")
                .type("simple")
                .authenticationMode("email")
                .ordered(false)
                .addMember(MemberRequest.builder()
                        .firstname("Sara")
                        .lastname("Amrani")
                        .email("sara@example.com")
                        .phone("+212600000000")
                        .addField(FieldRequest.builder()
                                .file("file_123")
                                .type(FieldRequest.SIGNATURE)
                                .page(1)
                                .position("141,268,151,101")
                                .build())
                        .build())
                .build());

        assertEquals("test-token", authorization.get());
        assertEquals("Contrat client", requestBody.get().get("name").asText());
        assertEquals("simple", requestBody.get().get("type").asText());
        assertEquals("sara@example.com", requestBody.get().get("members").get(0).get("email").asText());
        assertEquals("file_123", requestBody.get().get("members").get(0).get("fields").get(0).get("file").asText());
        assertEquals("tx_123", response.getId());
        assertEquals("draft", response.getStatus());
    }

    @Test
    void createTransactionThrowsOnApiError() throws Exception {
        startServer(exchange -> respond(exchange, 400, "{\"message\":\"Invalid payload\"}"));

        DamanesignClient client = DamanesignClient.builder()
                .baseUrl(serverUrl())
                .apiKey("test-token")
                .build();

        DamanesignException exception = assertThrows(DamanesignException.class,
                () -> client.createTransaction(CreateTransactionRequest.builder().name("Invalid").build()));

        assertEquals(400, exception.getStatusCode());
        assertEquals("{\"message\":\"Invalid payload\"}", exception.getResponseBody());
    }

    private void startServer(ExchangeHandler handler) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", handler::handle);
        server.start();
    }

    private String serverUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }

    @FunctionalInterface
    private interface ExchangeHandler {
        void handle(HttpExchange exchange) throws IOException;
    }
}
