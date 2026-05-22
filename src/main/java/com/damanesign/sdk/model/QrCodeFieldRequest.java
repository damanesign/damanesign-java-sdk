package com.damanesign.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class QrCodeFieldRequest {
    private String file;
    private Integer page;
    private String position;

    public String getFile() {
        return file;
    }

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
        private final QrCodeFieldRequest request = new QrCodeFieldRequest();

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

        public QrCodeFieldRequest build() {
            return request;
        }
    }
}
