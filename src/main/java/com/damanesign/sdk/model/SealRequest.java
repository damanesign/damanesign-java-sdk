package com.damanesign.sdk.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SealRequest {
    private String file;
    private String certificate;
    private String code;
    private String image;
    private List<SealFieldRequest> fields;
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    public String getFile() {
        return file;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getCode() {
        return code;
    }

    public String getImage() {
        return image;
    }

    public List<SealFieldRequest> getFields() {
        return fields;
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
        private final SealRequest request = new SealRequest();

        private Builder() {
        }

        public Builder file(String file) {
            request.file = file;
            return this;
        }

        public Builder certificate(String certificate) {
            request.certificate = certificate;
            return this;
        }

        public Builder code(String code) {
            request.code = code;
            return this;
        }

        public Builder image(String image) {
            request.image = image;
            return this;
        }

        public Builder fields(List<SealFieldRequest> fields) {
            request.fields = fields;
            return this;
        }

        public Builder addField(SealFieldRequest field) {
            if (request.fields == null) {
                request.fields = new ArrayList<>();
            }
            request.fields.add(field);
            return this;
        }

        public Builder additionalProperty(String name, Object value) {
            request.additionalProperties.put(name, value);
            return this;
        }

        public SealRequest build() {
            return request;
        }
    }
}
