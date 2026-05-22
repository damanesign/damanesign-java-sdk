package com.damanesign.sdk.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class FieldRequest {
    public static final String SIGNATURE = "signature-field";
    public static final String NAME = "name-field";
    public static final String EMAIL = "email-field";
    public static final String DATE = "date-field";
    public static final String TEXT = "text-field";

    private String file;
    private Integer page;
    private String position;
    private String type;
    private String value;
    private Map<String, String> metadata;
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    public String getFile() {
        return file;
    }

    public Integer getPage() {
        return page;
    }

    public String getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final FieldRequest request = new FieldRequest();

        private Builder() {
        }

        public Builder file(String file) {
            request.file = file;
            return this;
        }

        public Builder page(Integer page) {
            request.page = page;
            return this;
        }

        public Builder position(String position) {
            request.position = position;
            return this;
        }

        public Builder type(String type) {
            request.type = type;
            return this;
        }

        public Builder value(String value) {
            request.value = value;
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            request.metadata = metadata;
            return this;
        }

        public Builder additionalProperty(String name, Object value) {
            request.additionalProperties.put(name, value);
            return this;
        }

        public FieldRequest build() {
            return request;
        }
    }
}
