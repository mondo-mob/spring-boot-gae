package org.springframework.contrib.gae.datastore.client.model;

import com.google.api.client.util.Key;

public class ResponseError {
    @Key
    private Long code;

    @Key
    private String message;

    public Long getCode() {
        return code;
    }

    public ResponseError setCode(Long code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseError setMessage(String message) {
        this.message = message;
        return this;
    }
}
