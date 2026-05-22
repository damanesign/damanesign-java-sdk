package com.damanesign.sdk.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MemberRequest {
    public static final String SIGNER = "signer";
    public static final String VALIDATOR = "validator";

    private String type;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String authenticationMode;
    private Integer position;
    private String user;
    private List<FieldRequest> fields;
    private String signatureType;
    private String consentText;
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    public String getType() {
        return type;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAuthenticationMode() {
        return authenticationMode;
    }

    public Integer getPosition() {
        return position;
    }

    public String getUser() {
        return user;
    }

    public List<FieldRequest> getFields() {
        return fields;
    }

    public String getSignatureType() {
        return signatureType;
    }

    public String getConsentText() {
        return consentText;
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
        private final MemberRequest request = new MemberRequest();

        private Builder() {
        }

        public Builder type(String type) {
            request.type = type;
            return this;
        }

        public Builder firstname(String firstname) {
            request.firstname = firstname;
            return this;
        }

        public Builder lastname(String lastname) {
            request.lastname = lastname;
            return this;
        }

        public Builder email(String email) {
            request.email = email;
            return this;
        }

        public Builder phone(String phone) {
            request.phone = phone;
            return this;
        }

        public Builder authenticationMode(String authenticationMode) {
            request.authenticationMode = authenticationMode;
            return this;
        }

        public Builder position(Integer position) {
            request.position = position;
            return this;
        }

        public Builder user(String user) {
            request.user = user;
            return this;
        }

        public Builder fields(List<FieldRequest> fields) {
            request.fields = fields;
            return this;
        }

        public Builder addField(FieldRequest field) {
            if (request.fields == null) {
                request.fields = new ArrayList<>();
            }
            request.fields.add(field);
            return this;
        }

        public Builder signatureType(String signatureType) {
            request.signatureType = signatureType;
            return this;
        }

        public Builder consentText(String consentText) {
            request.consentText = consentText;
            return this;
        }

        public Builder additionalProperty(String name, Object value) {
            request.additionalProperties.put(name, value);
            return this;
        }

        public MemberRequest build() {
            return request;
        }
    }
}
