package com.damanesign.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SealFieldRequest {
    private Integer page;
    private String position;

    public Integer getPage() {
        return page;
    }

    public String getPosition() {
        return position;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final SealFieldRequest request = new SealFieldRequest();

        private Builder() {
        }

        public Builder page(Integer page) {
            request.page = page;
            return this;
        }

        public Builder position(String position) {
            request.position = position;
            return this;
        }

        public SealFieldRequest build() {
            return request;
        }
    }
}
