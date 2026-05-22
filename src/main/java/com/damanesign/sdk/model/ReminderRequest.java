package com.damanesign.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ReminderRequest {
    private String id;
    private Boolean enabled;
    private Integer interval;
    private Integer limit;
    private Integer counter;

    public String getId() {
        return id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Integer getInterval() {
        return interval;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getCounter() {
        return counter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final ReminderRequest request = new ReminderRequest();

        private Builder() {
        }

        public Builder id(String id) {
            request.id = id;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            request.enabled = enabled;
            return this;
        }

        public Builder interval(Integer interval) {
            request.interval = interval;
            return this;
        }

        public Builder limit(Integer limit) {
            request.limit = limit;
            return this;
        }

        public Builder counter(Integer counter) {
            request.counter = counter;
            return this;
        }

        public ReminderRequest build() {
            return request;
        }
    }
}
