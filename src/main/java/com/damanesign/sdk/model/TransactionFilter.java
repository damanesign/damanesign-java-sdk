package com.damanesign.sdk.model;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TransactionFilter {
    private final Map<String, Object> parameters = new LinkedHashMap<>();

    public static TransactionFilter create() {
        return new TransactionFilter();
    }

    public TransactionFilter type(List<String> type) {
        parameters.put("type", type);
        return this;
    }

    public TransactionFilter status(List<String> status) {
        parameters.put("status", status);
        return this;
    }

    public TransactionFilter tags(List<String> tags) {
        parameters.put("tags", tags);
        return this;
    }

    public TransactionFilter offset(Integer offset) {
        parameters.put("offset", offset);
        return this;
    }

    public TransactionFilter limit(Integer limit) {
        parameters.put("limit", limit);
        return this;
    }

    public TransactionFilter name(String name) {
        parameters.put("name", name);
        return this;
    }

    public TransactionFilter memberFirstname(String firstname) {
        parameters.put("members.firstname", firstname);
        return this;
    }

    public TransactionFilter memberLastname(String lastname) {
        parameters.put("members.lastname", lastname);
        return this;
    }

    public TransactionFilter creatorId(String creatorId) {
        parameters.put("creatorId", creatorId);
        return this;
    }

    public TransactionFilter workspaceIds(List<String> workspaceIds) {
        parameters.put("workspaceIds", workspaceIds);
        return this;
    }

    public TransactionFilter createdAt(List<LocalDate> createdAt) {
        parameters.put("createdAt", createdAt);
        return this;
    }

    public TransactionFilter expiresAt(List<LocalDate> expiresAt) {
        parameters.put("expiresAt", expiresAt);
        return this;
    }

    public TransactionFilter order(String order) {
        parameters.put("order", order);
        return this;
    }

    public TransactionFilter xlsx(Boolean xlsx) {
        parameters.put("xlsx", xlsx);
        return this;
    }

    public TransactionFilter parameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    public Map<String, Object> toQueryParameters() {
        return new LinkedHashMap<>(parameters);
    }
}
