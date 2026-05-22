package com.damanesign.sdk.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateTransactionRequest {
    private String name;
    private String type;
    private String deliveryMode;
    private String authenticationMode;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate expiresAt;
    private Boolean ordered;
    private List<MemberRequest> members;
    private List<QrCodeFieldRequest> qrCode;
    private ReminderRequest reminder;
    private String workspace;
    private String templateId;
    private String[] tagIds;
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public String getAuthenticationMode() {
        return authenticationMode;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getExpiresAt() {
        return expiresAt;
    }

    public Boolean getOrdered() {
        return ordered;
    }

    public List<MemberRequest> getMembers() {
        return members;
    }

    public List<QrCodeFieldRequest> getQrCode() {
        return qrCode;
    }

    public ReminderRequest getReminder() {
        return reminder;
    }

    public String getWorkspace() {
        return workspace;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String[] getTagIds() {
        return tagIds;
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
        private final CreateTransactionRequest request = new CreateTransactionRequest();

        private Builder() {
        }

        public Builder name(String name) {
            request.name = name;
            return this;
        }

        public Builder type(String type) {
            request.type = type;
            return this;
        }

        public Builder deliveryMode(String deliveryMode) {
            request.deliveryMode = deliveryMode;
            return this;
        }

        public Builder authenticationMode(String authenticationMode) {
            request.authenticationMode = authenticationMode;
            return this;
        }

        public Builder description(String description) {
            request.description = description;
            return this;
        }

        public Builder expiresAt(LocalDate expiresAt) {
            request.expiresAt = expiresAt;
            return this;
        }

        public Builder ordered(Boolean ordered) {
            request.ordered = ordered;
            return this;
        }

        public Builder members(List<MemberRequest> members) {
            request.members = members;
            return this;
        }

        public Builder addMember(MemberRequest member) {
            if (request.members == null) {
                request.members = new ArrayList<>();
            }
            request.members.add(member);
            return this;
        }

        public Builder qrCode(List<QrCodeFieldRequest> qrCode) {
            request.qrCode = qrCode;
            return this;
        }

        public Builder addQrCode(QrCodeFieldRequest qrCode) {
            if (request.qrCode == null) {
                request.qrCode = new ArrayList<>();
            }
            request.qrCode.add(qrCode);
            return this;
        }

        public Builder reminder(ReminderRequest reminder) {
            request.reminder = reminder;
            return this;
        }

        public Builder workspace(String workspace) {
            request.workspace = workspace;
            return this;
        }

        public Builder templateId(String templateId) {
            request.templateId = templateId;
            return this;
        }

        public Builder tagIds(String... tagIds) {
            request.tagIds = tagIds;
            return this;
        }

        public Builder additionalProperty(String name, Object value) {
            request.additionalProperties.put(name, value);
            return this;
        }

        public CreateTransactionRequest build() {
            return request;
        }
    }
}
