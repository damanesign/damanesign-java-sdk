package com.damanesign.sdk.model;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SealFilter {
    private final Map<String, Object> parameters = new LinkedHashMap<>();

    public static SealFilter create() {
        return new SealFilter();
    }

    public SealFilter name(String name) {
        parameters.put("name", name);
        return this;
    }

    public SealFilter certificateId(String certificateId) {
        parameters.put("certificateId", certificateId);
        return this;
    }

    public SealFilter offset(Integer offset) {
        parameters.put("offset", offset);
        return this;
    }

    public SealFilter limit(Integer limit) {
        parameters.put("limit", limit);
        return this;
    }

    public SealFilter parameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    public Map<String, Object> toQueryParameters() {
        return new LinkedHashMap<>(parameters);
    }
}
