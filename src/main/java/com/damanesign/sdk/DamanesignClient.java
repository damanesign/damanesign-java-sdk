package com.damanesign.sdk;

import com.damanesign.sdk.http.DamanesignHttpClient;
import com.damanesign.sdk.model.CreateTransactionRequest;
import com.damanesign.sdk.model.FileResponse;
import com.damanesign.sdk.model.MemberRequest;
import com.damanesign.sdk.model.SealFilter;
import com.damanesign.sdk.model.SealRequest;
import com.damanesign.sdk.model.SealResponse;
import com.damanesign.sdk.model.TransactionFilter;
import com.damanesign.sdk.model.TransactionResponse;

import java.net.URLEncoder;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public final class DamanesignClient implements AutoCloseable {
    private static final String TRANSACTIONS_PATH = "/transactions";
    private static final String FILES_PATH = "/files";
    private static final String SEAL_PATH = "/seal";
    private static final String SEALS_PATH = "/seals";

    private final DamanesignHttpClient httpClient;

    private DamanesignClient(Builder builder) {
        this.httpClient = new DamanesignHttpClient(
                builder.baseUrl,
                builder.apiKey,
                builder.httpClient,
                builder.timeout
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return httpClient.post(TRANSACTIONS_PATH, request, TransactionResponse.class);
    }

    public TransactionResponse getTransaction(String transactionId) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        return httpClient.get(TRANSACTIONS_PATH + "/" + encodePath(transactionId), TransactionResponse.class);
    }

    public TransactionResponse updateTransaction(String transactionId, CreateTransactionRequest request) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(request, "request must not be null");
        return httpClient.put(TRANSACTIONS_PATH + "/" + encodePath(transactionId), request, TransactionResponse.class);
    }

    public void deleteTransaction(String transactionId) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        httpClient.delete(TRANSACTIONS_PATH + "/" + encodePath(transactionId));
    }

    public List<TransactionResponse> listTransactions(TransactionFilter filter) {
        String query = queryString(filter == null ? Map.of() : filter.toQueryParameters());
        return httpClient.getList(TRANSACTIONS_PATH + query, TransactionResponse[].class);
    }

    public List<TransactionResponse> listAssignedTransactions(TransactionFilter filter) {
        String query = queryString(filter == null ? Map.of() : filter.toQueryParameters());
        return httpClient.getList(TRANSACTIONS_PATH + "/assigned" + query, TransactionResponse[].class);
    }

    public TransactionResponse updateMember(String transactionId, String memberId, MemberRequest request) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(memberId, "memberId must not be null");
        Objects.requireNonNull(request, "request must not be null");
        return httpClient.put(
                TRANSACTIONS_PATH + "/" + encodePath(transactionId) + "/member/" + encodePath(memberId),
                request,
                TransactionResponse.class
        );
    }

    public TransactionResponse updateMemberAuthentication(String transactionId, String memberId, String mode) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(memberId, "memberId must not be null");
        Objects.requireNonNull(mode, "mode must not be null");
        return httpClient.putWithoutBody(
                TRANSACTIONS_PATH + "/" + encodePath(transactionId) + "/member/" + encodePath(memberId) + "/authentication/" + encodePath(mode),
                TransactionResponse.class
        );
    }

    public FileResponse uploadFile(Path file) {
        return uploadFile(file, "application/pdf", "signable");
    }

    public FileResponse uploadFile(Path file, String contentType, String type) {
        Objects.requireNonNull(file, "file must not be null");
        String query = "?contentType=" + encode(contentType) + "&type=" + encode(type);
        return httpClient.postMultipartFile(FILES_PATH + "/upload" + query, "file", file, FileResponse.class);
    }

    public void startTransaction(String transactionId) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        httpClient.postWithoutBody(TRANSACTIONS_PATH + "/" + encodePath(transactionId) + "/start");
    }

    public void sendReminder(String transactionId) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        httpClient.postWithoutBody(TRANSACTIONS_PATH + "/" + encodePath(transactionId) + "/reminders");
    }

    public void prolongTransaction(String transactionId, LocalDate expiresAt) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        httpClient.post(TRANSACTIONS_PATH + "/" + encodePath(transactionId) + "/prolong", expiresAt, Void.class);
    }

    public void cancelTransaction(String transactionId) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        httpClient.postWithoutBody(TRANSACTIONS_PATH + "/" + encodePath(transactionId) + "/cancel");
    }

    public String getSignatureUrl(String transactionId, String memberId) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(memberId, "memberId must not be null");
        return httpClient.getString(TRANSACTIONS_PATH + "/" + encodePath(transactionId) + "/member/" + encodePath(memberId) + "/url");
    }

    public SealResponse sealDocument(SealRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return httpClient.post(SEAL_PATH, request, SealResponse.class);
    }

    public List<SealResponse> listSeals(SealFilter filter) {
        String query = queryString(filter == null ? Map.of() : filter.toQueryParameters());
        return httpClient.getList(SEALS_PATH + query, SealResponse[].class);
    }

    public FileResponse getFile(String fileId) {
        Objects.requireNonNull(fileId, "fileId must not be null");
        return httpClient.get(FILES_PATH + "/" + encodePath(fileId), FileResponse.class);
    }

    public byte[] downloadFile(String fileId) {
        Objects.requireNonNull(fileId, "fileId must not be null");
        return httpClient.getBytes(FILES_PATH + "/" + encodePath(fileId) + "/download");
    }

    @Override
    public void close() {
        httpClient.close();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private static String encodePath(String value) {
        return encode(value).replace("+", "%20");
    }

    private static String queryString(Map<String, ?> parameters) {
        if (parameters.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("&");
        parameters.forEach((key, value) -> {
            if (value == null) {
                return;
            }
            if (value instanceof Iterable<?>) {
                for (Object item : (Iterable<?>) value) {
                    if (item != null) {
                        joiner.add(encode(key) + "=" + encode(String.valueOf(item)));
                    }
                }
                return;
            }
            if (value.getClass().isArray()) {
                Object[] values = (Object[]) value;
                for (Object item : values) {
                    if (item != null) {
                        joiner.add(encode(key) + "=" + encode(String.valueOf(item)));
                    }
                }
                return;
            }
            joiner.add(encode(key) + "=" + encode(String.valueOf(value)));
        });
        String query = joiner.toString();
        return query.isEmpty() ? "" : "?" + query;
    }

    public static final class Builder {
        private URI baseUrl;
        private String apiKey;
        private HttpClient httpClient;
        private Duration timeout = Duration.ofSeconds(30);

        private Builder() {
        }

        public Builder baseUrl(String baseUrl) {
            return baseUrl(URI.create(baseUrl));
        }

        public Builder baseUrl(URI baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public DamanesignClient build() {
            if (baseUrl == null) {
                throw new IllegalStateException("baseUrl is required");
            }
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalStateException("apiKey is required");
            }
            return new DamanesignClient(this);
        }
    }
}
